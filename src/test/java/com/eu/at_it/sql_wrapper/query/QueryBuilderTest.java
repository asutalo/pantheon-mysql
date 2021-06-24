package com.eu.at_it.sql_wrapper.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static com.mysql.cj.MysqlType.INT;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class QueryBuilderTest {
    public static final String SOME_KEY = "SOME_KEY";
    public static final String SOME_OTHER_KEY = "SOME_OTHER_KEY";
    public static final String ADDITIONAL_KEY = "KEY";
    private static final String SOME_TABLE = "SOME_TABLE";
    private static final String SOME_WHERE_KEY = "SOME_WHERE_KEY";

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private MySqlValue mockMySqlValue;

    @Test
    void buildSelectQuery() {
        String expectedQuery = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?;", SOME_TABLE, SOME_WHERE_KEY, SOME_OTHER_KEY);
        when(mockMySqlValue.getKey()).thenReturn(SOME_WHERE_KEY).thenReturn(SOME_OTHER_KEY);

        QueryBuilder queryBuilder = new QueryBuilder()
                .select()
                .from(SOME_TABLE)
                .where()
                .keyIsVal(mockMySqlValue)
                .and()
                .keyIsVal(mockMySqlValue);

        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
        verify(mockMySqlValue, never()).setParamIndex(anyInt());
    }

    @Test
    void buildInsertQuery() {
        String expectedQuery = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?);", SOME_TABLE, SOME_WHERE_KEY, SOME_OTHER_KEY);
        when(mockMySqlValue.getKey()).thenReturn(SOME_WHERE_KEY).thenReturn(SOME_OTHER_KEY);

        QueryBuilder queryBuilder = new QueryBuilder()
                .insert(SOME_TABLE, List.of(mockMySqlValue, mockMySqlValue));

        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
        verify(mockMySqlValue).setParamIndex(1);
        verify(mockMySqlValue).setParamIndex(2);

    }

    @Test
    void buildUpdateQuery() {
        String expectedQuery = String.format("UPDATE %s SET %s = ?, %s = ? WHERE %s = ? AND %s = ?;", SOME_TABLE, SOME_KEY, SOME_OTHER_KEY, SOME_WHERE_KEY, ADDITIONAL_KEY);

        MySqlValue mySqlValue = new MySqlValue(INT, SOME_KEY, 2);
        MySqlValue mySqlValue1 = new MySqlValue(INT, SOME_OTHER_KEY, 2);
        MySqlValue mySqlValue2 = new MySqlValue(INT, SOME_WHERE_KEY, 3);
        MySqlValue mySqlValue3 = new MySqlValue(INT, ADDITIONAL_KEY, 4);

        QueryBuilder queryBuilder = new QueryBuilder()
                .update(SOME_TABLE, List.of(mySqlValue, mySqlValue1))
                .where()
                .keyIsVal(mySqlValue2)
                .and()
                .keyIsVal(mySqlValue3);


        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
        Assertions.assertEquals(1, mySqlValue.getParamIndex());
        Assertions.assertEquals(2, mySqlValue1.getParamIndex());
        Assertions.assertEquals(0, mySqlValue2.getParamIndex());
        Assertions.assertEquals(0, mySqlValue3.getParamIndex());
    }

    @Test
    void buildDeleteQuery() {
        String expectedQuery = String.format("DELETE FROM %s WHERE %s = ? AND %s = ?;", SOME_TABLE, SOME_WHERE_KEY, SOME_OTHER_KEY);

        when(mockMySqlValue.getKey()).thenReturn(SOME_WHERE_KEY).thenReturn(SOME_OTHER_KEY);

        QueryBuilder queryBuilder = new QueryBuilder()
                .delete()
                .from(SOME_TABLE)
                .where()
                .keyIsVal(mockMySqlValue)
                .and()
                .keyIsVal(mockMySqlValue);

        verify(mockMySqlValue, never()).setParamIndex(anyInt());
        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
    }

    @Test
    void buildStatement() throws SQLException {
        QueryPart mockQueryPart = mock(QueryPart.class);
        List<QueryPart> mockQueryParts = List.of(mockQueryPart, mockQueryPart);
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.setQueryParts(mockQueryParts);

        when(mockQueryPart.apply(anyString())).thenReturn("query");
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);

        queryBuilder.prepareStatement(mockConnection);

        verify(mockQueryPart, times(2)).apply(mockPreparedStatement);
    }
}