package com.eu.at_it.sql_wrapper.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static com.mysql.cj.MysqlType.INT;
import static com.mysql.cj.MysqlType.VARCHAR;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class QueryBuilderTest {
    public static final String SOME_KEY = "SOME_KEY";
    public static final String SOME_OTHER_KEY = "SOME_OTHER_KEY";
    private static final String SOME_TABLE = "SOME_TABLE";
    private static final String SOME_WHERE_KEY = "SOME_WHERE_KEY";
    private static final String SOME_AND_KEY = "SOME_AND_KEY";
    private static final int SOME_INT_VAL = 1;
    private static final String SOME_STRING_VAL = "SOME_STRING_VAL";

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Test
    void buildSelectQuery() {
        String expectedQuery = "SELECT * FROM " + SOME_TABLE + " WHERE " + SOME_WHERE_KEY + " = ? AND " + SOME_AND_KEY + " = ?";

        QueryBuilder queryBuilder = new QueryBuilder()
                .selectAll()
                .from(SOME_TABLE)
                .where()
                .keyIsVal(INT, SOME_WHERE_KEY, SOME_INT_VAL)
                .and()
                .keyIsVal(VARCHAR, SOME_AND_KEY, SOME_STRING_VAL);

        assertQueryPartsList(queryBuilder.getQueryParts());
        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
    }

    @Test
    void buildInsertQuery() {
        String expectedQuery = "INSERT INTO " + SOME_TABLE + " SELECT ? AS " + SOME_KEY + ", ? AS " + SOME_OTHER_KEY;

        QueryBuilder queryBuilder = new QueryBuilder()
                .insert(SOME_TABLE)
                .select()
                .valAsKey(INT, SOME_INT_VAL, SOME_KEY)
                .valAsKey(INT, SOME_INT_VAL, SOME_OTHER_KEY);

        assertQueryPartsList(queryBuilder.getQueryParts());
        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
    }

    @Test
    void buildUpdateQuery() {
        String expectedQuery = "UPDATE " + SOME_TABLE + " SET " + SOME_KEY + " = ?, " + SOME_OTHER_KEY + " = ? WHERE " + SOME_WHERE_KEY + " = ? AND " + SOME_AND_KEY + " = ?";

        QueryBuilder queryBuilder = new QueryBuilder()
                .update(SOME_TABLE)
                .set()
                .keyIsVal(INT, SOME_KEY, SOME_INT_VAL)
                .keyIsVal(VARCHAR, SOME_OTHER_KEY, SOME_STRING_VAL)
                .where()
                .keyIsVal(INT, SOME_WHERE_KEY, SOME_INT_VAL)
                .and()
                .keyIsVal(VARCHAR, SOME_AND_KEY, SOME_STRING_VAL);

        assertQueryPartsList(queryBuilder.getQueryParts());
        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
    }

    @Test
    void buildDeleteQuery() {
        String expectedQuery = "DELETE FROM " + SOME_TABLE + " WHERE " + SOME_WHERE_KEY + " = ? AND " + SOME_AND_KEY + " = ?";

        QueryBuilder queryBuilder = new QueryBuilder()
                .delete()
                .from(SOME_TABLE)
                .where()
                .keyIsVal(INT, SOME_WHERE_KEY, SOME_INT_VAL)
                .and()
                .keyIsVal(VARCHAR, SOME_AND_KEY, SOME_STRING_VAL);

        assertQueryPartsList(queryBuilder.getQueryParts());

        Assertions.assertEquals(expectedQuery, queryBuilder.buildQueryString());
    }

    @Test
    void buildStatement() throws SQLException {
        QueryPart mockQueryPart = mock(QueryPart.class);
        List<QueryPart> mockQueryParts = List.of(mockQueryPart, mockQueryPart);

        QueryBuilder queryBuilder = new QueryBuilder().and();
        queryBuilder.setQueryParts(mockQueryParts);

        queryBuilder.prepareStatement(mockPreparedStatement);

        verify(mockQueryPart, times(2)).apply(mockPreparedStatement);
    }

    private void assertQueryPartsList(List<QueryPart> actualQueryParts) {
        Assertions.assertNotNull(actualQueryParts);
        Assertions.assertEquals(LinkedList.class, actualQueryParts.getClass());
    }
}