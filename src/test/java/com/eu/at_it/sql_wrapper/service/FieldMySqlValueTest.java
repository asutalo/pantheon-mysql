package com.eu.at_it.sql_wrapper.service;

import com.eu.at_it.sql_wrapper.query.MySqlValue;
import com.mysql.cj.MysqlType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class FieldMySqlValueTest extends FunctionsTestBase {
    @Test
    void apply_shouldExtractFieldValueFromObjectAndReturnItAsMySqlValue() {
        Field testField = getField();
        TestClass testClass = new TestClass();
        MySqlValue expected = new MySqlValue(MysqlType.INT, testField.getName(), testClass.getVal());

        MySqlValue actual = new FieldMySqlValue<>(testField, MysqlType.INT).apply(testClass);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void apply_shouldThrowExceptionWhenFieldValueCannotBeRetrieved() {
        Field testField = getField();

        Assertions.assertThrows(RuntimeException.class, () -> new FieldMySqlValue<>(testField, MysqlType.INT).apply(new Object()));
    }
}