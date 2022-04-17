package com.eu.at_it.pantheon.mysql.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

class ResultSetFieldValueSetterTest extends FunctionsTestBase {
    @Test
    void accept_shouldSetValueFromResultSet() {
        int expected = 42;
        Field testField = getField();
        Map<String, Object> row = Map.of(testField.getName(), expected);

        TestClass testClass = new TestClass();
        ResultSetFieldValueSetter<TestClass> resultSetFieldValueSetter = new ResultSetFieldValueSetter<>(testField);

        resultSetFieldValueSetter.accept(testClass, row);

        Assertions.assertEquals(expected, testClass.getVal());
    }
}