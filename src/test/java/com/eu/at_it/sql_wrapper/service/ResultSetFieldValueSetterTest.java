package com.eu.at_it.sql_wrapper.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResultSetFieldValueSetterTest extends FunctionsTestBase {
    @Test
    void accept_shouldSetValueFromResultSet() throws SQLException {
        int expected = 42;
        ResultSet mockResultSet = mock(ResultSet.class);
        Field testField = getField();
        TestClass testClass = new TestClass();
        ResultSetFieldValueSetter<TestClass> resultSetFieldValueSetter = new ResultSetFieldValueSetter<>(testField);
        when(mockResultSet.getObject(testField.getName())).thenReturn(expected);

        resultSetFieldValueSetter.accept(testClass, mockResultSet);

        Assertions.assertEquals(expected, testClass.getVal());
    }

    @Test
    void accept_shouldThrowExceptionWhenResultSetThrowsException() throws SQLException {
        ResultSet mockResultSet = mock(ResultSet.class);
        Field testField = getField();
        TestClass testClass = new TestClass();
        ResultSetFieldValueSetter<TestClass> resultSetFieldValueSetter = new ResultSetFieldValueSetter<>(testField);
        when(mockResultSet.getObject(testField.getName())).thenThrow(SQLException.class);

        Assertions.assertThrows(RuntimeException.class, () -> resultSetFieldValueSetter.accept(testClass, mockResultSet));
    }
}