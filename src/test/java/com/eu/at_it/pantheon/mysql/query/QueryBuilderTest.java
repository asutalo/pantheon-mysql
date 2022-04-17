package com.eu.at_it.pantheon.mysql.query;

import com.eu.at_it.pantheon.helper.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.mysql.cj.MysqlType.INT;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
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
        String expectedQuery = String.format("SELECT * FROM %s %s WHERE %s = ? AND %s = ?;", SOME_TABLE, SOME_TABLE, SOME_WHERE_KEY, SOME_OTHER_KEY);
        when(mockMySqlValue.getKey()).thenReturn(SOME_WHERE_KEY).thenReturn(SOME_OTHER_KEY);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.select();
        queryBuilder.from(SOME_TABLE);
        queryBuilder.where();
        queryBuilder.keyIsVal(mockMySqlValue);
        queryBuilder.and();
        queryBuilder.keyIsVal(mockMySqlValue);

        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
        verify(mockMySqlValue, never()).setParamIndex(anyInt());
    }

    @Test
    void buildSelectQueryWithColumnsAndAliases() {
        String SOME_ALIAS = "someAlias";
        String SOME_OTHER_ALIAS = "someOtherAlias";

        ArrayList<Pair<String, String>> someColumnsAndAliases = new ArrayList<>(List.of(new Pair<>(SOME_KEY, SOME_ALIAS), new Pair<>(SOME_OTHER_KEY, SOME_OTHER_ALIAS)));

        String expectedQuery = String.format("SELECT %s AS %s, %s AS %s FROM %s %s WHERE %s = ? AND %s = ?;", SOME_KEY, SOME_ALIAS, SOME_OTHER_KEY, SOME_OTHER_ALIAS, SOME_TABLE, SOME_TABLE, SOME_WHERE_KEY, SOME_OTHER_KEY);
        when(mockMySqlValue.getKey()).thenReturn(SOME_WHERE_KEY).thenReturn(SOME_OTHER_KEY);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.select(someColumnsAndAliases);
        queryBuilder.from(SOME_TABLE);
        queryBuilder.where();
        queryBuilder.keyIsVal(mockMySqlValue);
        queryBuilder.and();
        queryBuilder.keyIsVal(mockMySqlValue);

        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
        verify(mockMySqlValue, never()).setParamIndex(anyInt());
    }

    @Test
    void buildInsertQuery() {
        String expectedQuery = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?);", SOME_TABLE, SOME_WHERE_KEY, SOME_OTHER_KEY);
        when(mockMySqlValue.getKey()).thenReturn(SOME_WHERE_KEY).thenReturn(SOME_OTHER_KEY);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.insert(SOME_TABLE, new LinkedList<>(List.of(mockMySqlValue, mockMySqlValue)));

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

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.update(SOME_TABLE, new LinkedList<>(List.of(mySqlValue, mySqlValue1)));
        queryBuilder.where();
        queryBuilder.keyIsVal(mySqlValue2);
        queryBuilder.and();
        queryBuilder.keyIsVal(mySqlValue3);


        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
        Assertions.assertEquals(1, mySqlValue.getParamIndex());
        Assertions.assertEquals(2, mySqlValue1.getParamIndex());
        Assertions.assertEquals(0, mySqlValue2.getParamIndex());
        Assertions.assertEquals(0, mySqlValue3.getParamIndex());
    }

    @Test
    void buildDeleteQuery() {
        String expectedQuery = String.format("DELETE FROM %s %s WHERE %s = ? AND %s = ?;", SOME_TABLE, SOME_TABLE, SOME_WHERE_KEY, SOME_OTHER_KEY);

        when(mockMySqlValue.getKey()).thenReturn(SOME_WHERE_KEY).thenReturn(SOME_OTHER_KEY);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.delete();
        queryBuilder.from(SOME_TABLE);
        queryBuilder.where();
        queryBuilder.keyIsVal(mockMySqlValue);
        queryBuilder.and();
        queryBuilder.keyIsVal(mockMySqlValue);

        verify(mockMySqlValue, never()).setParamIndex(anyInt());
        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
    }

    @Test
    void buildStatement() throws SQLException {
        QueryPart mockQueryPart = mock(QueryPart.class);
        List<QueryPart> mockQueryParts = new LinkedList<>(List.of(mockQueryPart, mockQueryPart));
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.setQueryParts(mockQueryParts);

        when(mockQueryPart.apply(anyString())).thenReturn("query");
        when(mockConnection.prepareStatement(anyString(), eq(RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);

        queryBuilder.prepareStatement(mockConnection);

        verify(mockQueryPart, times(2)).apply(mockPreparedStatement);
    }

    @Test
    void equals() {
        QueryBuilder queryBuilder1 = new QueryBuilder();
        queryBuilder1.select();
        queryBuilder1.from(SOME_TABLE);

        QueryBuilder queryBuilder2 = new QueryBuilder();
        queryBuilder2.select();
        queryBuilder2.from(SOME_TABLE);

        Assertions.assertEquals(queryBuilder1, queryBuilder2);
    }

    @Test
    void equalsReturnsFalseWhenOrderOfApplicationDiffers() {
        QueryBuilder correct = new QueryBuilder();
        correct.select();
        correct.from(SOME_TABLE);

        QueryBuilder incorrect = new QueryBuilder();
        incorrect.from(SOME_TABLE);
        incorrect.select();

        Assertions.assertNotEquals(correct, incorrect);
    }

    @Test
    void hashcode() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.select();
        queryBuilder.from(SOME_TABLE);

        Assertions.assertEquals(queryBuilder.hashCode(), queryBuilder.hashCode());
    }
}