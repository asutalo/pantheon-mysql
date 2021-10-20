package com.eu.at_it.sql_wrapper.service;

import com.eu.at_it.sql_wrapper.query.MySqlValue;
import com.mysql.cj.MysqlType;

import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * Function to convert a file/variable from an object into a MySqlValue
 */
class FieldMySqlValue<T> implements Function<T, MySqlValue> {
    private final Field field;
    private final String fieldName;
    private final MysqlType mysqlType;

    /**
     * @param field     reflection of the variable that is used to fetch the value for MySqlValue
     * @param mysqlType desired MySql type for the value of the field to have
     */
    FieldMySqlValue(Field field, MysqlType mysqlType) {
        this.field = field;
        this.mysqlType = mysqlType;
        fieldName = field.getName();
    }

    /**
     * @param field     reflection of the variable that is used to fetch the value for MySqlValue
     * @param mysqlType desired MySql type for the value of the field to have
     */
    FieldMySqlValue(Field field, MysqlType mysqlType, String fieldName) {
        this.field = field;
        this.mysqlType = mysqlType;
        this.fieldName = fieldName;
    }

    @Override
    public MySqlValue apply(T valueOf) {
        try {
            Object fieldValue = field.get(valueOf);
            return new MySqlValue(mysqlType, fieldName, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch field value", e);
        }
    }

    String getFieldName() {
        return fieldName;
    }
}
