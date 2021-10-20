package com.eu.at_it.sql_wrapper.service;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;

class ResultSetFieldValueSetter<T> implements BiConsumer<T, ResultSet> {
    private final String fieldName;
    private final FieldValueSetter<T> fieldValueSetter;

    ResultSetFieldValueSetter(Field fieldToSet) {
        fieldName = fieldToSet.getName();
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
    }

    ResultSetFieldValueSetter(Field fieldToSet, String fieldName) {
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
        this.fieldName = fieldName;
    }

    @Override
    public void accept(T setFieldOn, ResultSet resultSet) {
        try {
            fieldValueSetter.accept(setFieldOn, resultSet.getObject(fieldName));
        } catch (SQLException e) {
            throw new RuntimeException("Field not returned from query", e);
        }
    }

    String getFieldName() {
        return fieldName;
    }
}
