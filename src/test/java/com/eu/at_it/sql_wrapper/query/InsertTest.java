package com.eu.at_it.sql_wrapper.query;

import com.mysql.cj.MysqlType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsertTest {
    private static final String SOME_TABLE = "SOME_TABLE";
    private static final String SOME_QUERY = "";

    @Mock
    private MySqlValue mockMySqlValue;

    @Test
    void apply() {
        when(mockMySqlValue.getKey()).thenReturn("name").thenReturn("age");

        String expectedQuery = "INSERT INTO SOME_TABLE (name, age) VALUES (?, ?)";

        assertEquals(expectedQuery, insert().apply(SOME_QUERY));
    }

    @Test
    void applyOnPreparedStatement() throws SQLException {
        String someName = "someName";
        int someInt = 20;
        MysqlType nameType = MysqlType.VARCHAR;
        MysqlType ageType = MysqlType.INT;
        int first = 0;
        int second = 1;

        when(mockMySqlValue.getValue()).thenReturn(someName).thenReturn(someInt);
        when(mockMySqlValue.getMysqlType()).thenReturn(nameType).thenReturn(ageType);
        when(mockMySqlValue.getParamIndex()).thenReturn(first).thenReturn(second);

        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        insert().apply(mockPreparedStatement);

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
        LinkedList<MySqlValue> expected = new LinkedList<>(List.of(mockMySqlValue, mockMySqlValue));

        assertEquals(expected, new Insert(SOME_TABLE, expected).getValues());
    }

    @Test
    void equals() {
        Insert insert1 = insert();
        Insert insert2 = insert();

        Assertions.assertEquals(insert1, insert2);
    }

    @Test
    void hashcode() {
        Insert Insert = insert();

        Assertions.assertEquals(Insert.hashCode(), Insert.hashCode());
    }

    private Insert insert() {
        return new Insert(SOME_TABLE, new LinkedList<>(List.of(mockMySqlValue, mockMySqlValue)));
    }
}