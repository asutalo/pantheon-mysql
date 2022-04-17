package com.eu.at_it.pantheon.mysql.service;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BiConsumer;

class ResultSetFieldValueSetter<T> implements BiConsumer<T, Map<String, Object>> {
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
    public void accept(T setFieldOn, Map<String, Object> resultSet) {
        fieldValueSetter.accept(setFieldOn, resultSet.get(fieldName));
    }

    String getFieldName() {
        return fieldName;
    }
}
