package com.eu.at_it.sql_wrapper.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class FieldValueSetterTest extends FunctionsTestBase {

    @Test
    void accept_shouldUpdateFieldValue() {
        int expected = 2;
        TestClass testClass = new TestClass();
        Field testField = getField();

        new FieldValueSetter<>(testField).accept(testClass, expected);
        int actual = testClass.getVal();

        Assertions.assertNotEquals(START_VALUE, actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void accept_shouldThrowExceptionWhenValueCannotBeSet() {
        TestClass testClass = new TestClass();
        Field testField = getField();

        Assertions.assertThrows(RuntimeException.class, () -> new FieldValueSetter<>(testField).accept(testClass, "notApplicable"));
    }
}