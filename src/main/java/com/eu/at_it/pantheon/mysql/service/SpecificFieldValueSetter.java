package com.eu.at_it.pantheon.mysql.service;

import com.eu.at_it.pantheon.helper.Pair;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BiConsumer;

class SpecificFieldValueSetter<T> implements BiConsumer<T, Map<String, Object>> {
    private final String fieldName;
    private final String aliasFieldName;
    private final FieldValueSetter<T> fieldValueSetter;

    SpecificFieldValueSetter(Field fieldToSet, String tableName) {
        fieldName = fieldToSet.getName();
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
        aliasFieldName = alias(fieldName, tableName);
    }

    SpecificFieldValueSetter(Field fieldToSet, String fieldName, String tableName) {
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
        this.fieldName = fieldName;
        aliasFieldName = alias(fieldName, tableName);
    }

    @Override
    public void accept(T setFieldOn, Map<String, Object> row) {
        fieldValueSetter.accept(setFieldOn, row.get(aliasFieldName));
    }

    String getFieldName() {
        return fieldName;
    }

    String getAliasFieldName() {
        return aliasFieldName;
    }

    Pair<String, String> fieldNameAndAlias() {
        return new Pair<>(fieldName, aliasFieldName);
    }

    private String alias(String fieldName, String tableName) {
        return tableName.concat("_").concat(fieldName);
    }
}
