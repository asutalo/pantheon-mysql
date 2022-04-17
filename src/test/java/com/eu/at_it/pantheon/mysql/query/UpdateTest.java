package com.eu.at_it.pantheon.mysql.query;

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
class UpdateTest {
    private static final String SOME_TABLE = "SOME_TABLE";
    private static final String SOME_QUERY = "";
    @Mock
    private MySqlValue mockMySqlValue;

    @Test
    void apply() {
        when(mockMySqlValue.getKey()).thenReturn("name").thenReturn("age");

        String expectedQuery = "UPDATE SOME_TABLE SET name = ?, age = ?";

        assertEquals(expectedQuery, update().apply(SOME_QUERY));
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
        update().apply(mockPreparedStatement);

        verify(mockPreparedStatement).setObject(first, someName, nameType);
        verify(mockPreparedStatement).setObject(second, someInt, ageType);
    }

    @Test
    void getTableName() {
        assertEquals(SOME_TABLE, update().getTableName());
    }

    @Test
    void equals() {
        Update update1 = update();
        Update update2 = update();

        Assertions.assertEquals(update1, update2);
    }

    @Test
    void hashcode() {
        Update update = update();

        Assertions.assertEquals(update.hashCode(), update.hashCode());
    }

    private Update update() {
        return new Update(SOME_TABLE, new LinkedList<>(List.of(mockMySqlValue, mockMySqlValue)));
    }
}