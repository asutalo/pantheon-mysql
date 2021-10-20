package com.eu.at_it.sql_wrapper.service;

import com.eu.at_it.sql_wrapper.service.annotations.MySqlField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

// utility class to make testing of the GenericDataAccessService easier
class GenericDataAccessServiceProvider {
    static final String NO_PRIMARY_KEY_FOUND = "No primary key found";
    static final String THERE_CAN_BE_ONLY_ONE_PRIMARY_KEY = "There can be only one primary key";
    static final String FAILED_TO_LOCATE_AN_EMPTY_CONSTRUCTOR = "Failed to locate an empty constructor";
    private static GenericDataAccessServiceProvider INSTANCE;

    private GenericDataAccessServiceProvider() {
    }

    static synchronized GenericDataAccessServiceProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GenericDataAccessServiceProvider();
        }

        return INSTANCE;
    }

    //for injecting mocks in tests to simplify instantiation of the GenericDataAccessService
    static void setInstance(GenericDataAccessServiceProvider instance) {
        INSTANCE = instance;
    }

    <T> String getTableName(Class<T> tClass) {
        return tClass.getSimpleName();
    }

    <T> Instantiator<T> getInstantiator(Class<T> tClass) {
        try {
            Constructor<T> declaredConstructor = tClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return new Instantiator<>(declaredConstructor);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(FAILED_TO_LOCATE_AN_EMPTY_CONSTRUCTOR, e);
        }
    }

    <T> LinkedList<FieldMySqlValue<T>> getFieldMySqlValues(Class<T> tClass) {
        LinkedList<FieldMySqlValue<T>> getters = new LinkedList<>();

        for (Field field : getDeclaredFields(tClass)) {
            field.setAccessible(true);
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);

            if (!mySqlFieldInfo.primary()) {
                getters.add(new FieldMySqlValue<>(field, mySqlFieldInfo.type()));
            }
        }

        return getters;
    }

    <T> LinkedList<ResultSetFieldValueSetter<T>> getResultSetFieldValueSetters(Class<T> tClass) {
        LinkedList<ResultSetFieldValueSetter<T>> setters = new LinkedList<>();
        for (Field field : getDeclaredFields(tClass)) {
            field.setAccessible(true);
            setters.add(new ResultSetFieldValueSetter<>(field));
        }

        return setters;
    }

    <T> FieldMySqlValue<T> getPrimaryKeyFieldMySqlValue(Class<T> tClass) {
        for (Field field : getDeclaredFields(tClass)) {
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);

            if (mySqlFieldInfo.primary()) {
                field.setAccessible(true);

                return new FieldMySqlValue<>(field, mySqlFieldInfo.type());
            }
        }

        throw new RuntimeException(NO_PRIMARY_KEY_FOUND);
    }

    <T> FieldValueSetter<T> getPrimaryKeyFieldValueSetter(Class<T> tClass) {
        for (Field field : getDeclaredFields(tClass)) {
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);

            if (mySqlFieldInfo.primary()) {
                field.setAccessible(true);
                return new FieldValueSetter<>(field);
            }
        }

        throw new RuntimeException(NO_PRIMARY_KEY_FOUND);
    }

    <T> void validateClass(Class<T> tClass) {
        try {
            tClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(FAILED_TO_LOCATE_AN_EMPTY_CONSTRUCTOR, e);
        }

        List<Field> primaryKeys = new ArrayList<>();
        for (Field field : getDeclaredFields(tClass)) {
            MySqlField mySqlFieldInfo = field.getAnnotation(MySqlField.class);

            if (mySqlFieldInfo.primary()) {
                primaryKeys.add(field);
            }
        }

        if (primaryKeys.isEmpty())
            throw new RuntimeException(NO_PRIMARY_KEY_FOUND);
        else if (primaryKeys.size() > 1)
            throw new RuntimeException(THERE_CAN_BE_ONLY_ONE_PRIMARY_KEY);
    }

    private <T> List<Field> getDeclaredFields(Class<T> tClass) {
        return Arrays.stream(tClass.getDeclaredFields()).filter(field -> field.getAnnotation(MySqlField.class) != null).collect(Collectors.toList());
    }
}
