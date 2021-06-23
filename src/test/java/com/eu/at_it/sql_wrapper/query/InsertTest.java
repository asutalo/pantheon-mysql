package com.eu.at_it.sql_wrapper.query;

import com.mysql.cj.MysqlType;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InsertTest {
    private static final String SOME_TABLE = "SOME_TABLE";
    private static final String SOME_QUERY = "";

    @Test
    void apply() {
        MySqlValue mockMySqlValue = mock(MySqlValue.class);
        when(mockMySqlValue.getKey()).thenReturn("name").thenReturn("age");

        String expectedQuery = "INSERT INTO SOME_TABLE (name, age) VALUES (?, ?)";

        assertEquals(expectedQuery, new Insert(SOME_TABLE, List.of(mockMySqlValue, mockMySqlValue)).apply(SOME_QUERY));
    }

    @Test
    void applyOnPreparedStatement() throws SQLException {
        String someName = "someName";
        int someInt = 20;
        MysqlType nameType = MysqlType.VARCHAR;
        MysqlType ageType = MysqlType.INT;
        int first = 0;
        int second = 1;

        MySqlValue mockMySqlValue = mock(MySqlValue.class);
        when(mockMySqlValue.getValue()).thenReturn(someName).thenReturn(someInt);
        when(mockMySqlValue.getMysqlType()).thenReturn(nameType).thenReturn(ageType);
        when(mockMySqlValue.getParamIndex()).thenReturn(first).thenReturn(second);

        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        new Insert(SOME_TABLE, List.of(mockMySqlValue, mockMySqlValue)).apply(mockPreparedStatement);

        verify(mockPreparedStatement).setObject(first, someName, nameType);
        verify(mockPreparedStatement).setObject(second, someInt, ageType);
    }

    @Test
    void getTableName() {
        assertEquals(SOME_TABLE, new Insert(SOME_TABLE).getTableName());
    }

    @Test
    void getValues() {
        MySqlValue mockMySqlValue = mock(MySqlValue.class);
        List<MySqlValue> expected = List.of(mockMySqlValue, mockMySqlValue);

        assertEquals(expected, new Insert(SOME_TABLE, expected).getValues());
    }
}