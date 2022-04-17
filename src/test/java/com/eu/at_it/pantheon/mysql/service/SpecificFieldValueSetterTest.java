package com.eu.at_it.pantheon.mysql.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

class SpecificFieldValueSetterTest extends FunctionsTestBase {
    @Test
    void accept_shouldSetValueFromRow() {
        int expected = 42;
        Field testField = getField();
        SpecificFieldValueSetter<TestClass> specificFieldValueSetter = new SpecificFieldValueSetter<>(testField, "someTableName");
        Map<String, Object> row = Map.of(specificFieldValueSetter.getAliasFieldName(), expected);

        TestClass testClass = new TestClass();

        specificFieldValueSetter.accept(testClass, row);

        Assertions.assertEquals(expected, testClass.getVal());
    }
}