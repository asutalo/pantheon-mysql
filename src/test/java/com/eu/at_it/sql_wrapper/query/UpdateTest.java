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

class UpdateTest {
    private static final String SOME_TABLE = "SOME_TABLE";
    private static final String SOME_QUERY = "";

    @Test
    void apply() {
        MySqlValue mockMySqlValue = mock(MySqlValue.class);
        when(mockMySqlValue.getKey()).thenReturn("name").thenReturn("age");

        String expectedQuery = "UPDATE SOME_TABLE SET name = ?, age = ?";

        assertEquals(expectedQuery, new Update(SOME_TABLE, List.of(mockMySqlValue, mockMySqlValue)).apply(SOME_QUERY));
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
        new Update(SOME_TABLE, List.of(mockMySqlValue, mockMySqlValue)).apply(mockPreparedStatement);

        verify(mockPreparedStatement).setObject(first, someName, nameType);
        verify(mockPreparedStatement).setObject(second, someInt, ageType);
    }

    @Test
    void getTableName() {
        MySqlValue mockMySqlValue = mock(MySqlValue.class);

        assertEquals(SOME_TABLE, new Update(SOME_TABLE, List.of(mockMySqlValue)).getTableName());
    }

    @Test
    void getValues() {
        MySqlValue mockMySqlValue = mock(MySqlValue.class);
        List<MySqlValue> expected = List.of(mockMySqlValue, mockMySqlValue);

        assertEquals(expected, new Update(SOME_TABLE, expected).getValues());
    }
}