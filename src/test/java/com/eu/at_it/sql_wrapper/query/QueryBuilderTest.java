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
    void buildSelectQuery() throws SQLException {
        String expectedQuery = "SELECT * FROM " + SOME_TABLE + " WHERE " + SOME_WHERE_KEY + " = ? AND " + SOME_AND_KEY + " = ?";

        List<QueryPart> actualQueryParts = new QueryBuilder()
                .selectAll()
                .from(SOME_TABLE)
                .where()
                .keyIsVal(INT, SOME_WHERE_KEY, SOME_INT_VAL)
                .and()
                .keyIsVal(VARCHAR, SOME_AND_KEY, SOME_STRING_VAL)
                .build();

        assertQueryPartsList(actualQueryParts);

        String actualQuery = prepareQuery(actualQueryParts);

        Assertions.assertEquals(expectedQuery, actualQuery);

        verify(mockPreparedStatement).setObject(1, SOME_INT_VAL, INT);
        verify(mockPreparedStatement).setObject(2, SOME_STRING_VAL, VARCHAR);
    }

    @Test
    void buildInsertQuery() throws SQLException {
        String expectedQuery = "INSERT INTO " + SOME_TABLE + " SELECT ? AS " + SOME_KEY + ", ? AS " + SOME_OTHER_KEY;

        List<QueryPart> actualQueryParts = new QueryBuilder()
                .insert(SOME_TABLE)
                .select()
                .valAsKey(INT, SOME_INT_VAL, SOME_KEY)
                .valAsKey(INT, SOME_INT_VAL, SOME_OTHER_KEY)
                .build();

        assertQueryPartsList(actualQueryParts);

        String actualQuery = prepareQuery(actualQueryParts);

        Assertions.assertEquals(expectedQuery, actualQuery);

        verify(mockPreparedStatement).setObject(1, SOME_INT_VAL, INT);
        verify(mockPreparedStatement).setObject(2, SOME_INT_VAL, INT);
    }

    @Test
    void buildUpdateQuery() throws SQLException {
        String expectedQuery = "UPDATE " + SOME_TABLE + " SET " + SOME_KEY + " = ?, " + SOME_OTHER_KEY + " = ? WHERE " + SOME_WHERE_KEY + " = ? AND " + SOME_AND_KEY + " = ?";

        List<QueryPart> actualQueryParts = new QueryBuilder()
                .update(SOME_TABLE)
                .set()
                .keyIsVal(INT, SOME_KEY, SOME_INT_VAL)
                .keyIsVal(VARCHAR, SOME_OTHER_KEY, SOME_STRING_VAL)
                .where()
                .keyIsVal(INT, SOME_WHERE_KEY, SOME_INT_VAL)
                .and()
                .keyIsVal(VARCHAR, SOME_AND_KEY, SOME_STRING_VAL)
                .build();

        assertQueryPartsList(actualQueryParts);

        String actualQuery = prepareQuery(actualQueryParts);

        Assertions.assertEquals(expectedQuery, actualQuery);

        verify(mockPreparedStatement).setObject(1, SOME_INT_VAL, INT);
        verify(mockPreparedStatement).setObject(2, SOME_STRING_VAL, VARCHAR);
        verify(mockPreparedStatement).setObject(3, SOME_INT_VAL, INT);
        verify(mockPreparedStatement).setObject(4, SOME_STRING_VAL, VARCHAR);
    }

    @Test
    void buildDeleteQuery() throws SQLException {
        String expectedQuery = "DELETE FROM " + SOME_TABLE + " WHERE " + SOME_WHERE_KEY + " = ? AND " + SOME_AND_KEY + " = ?";

        List<QueryPart> actualQueryParts = new QueryBuilder()
                .delete()
                .from(SOME_TABLE)
                .where()
                .keyIsVal(INT, SOME_WHERE_KEY, SOME_INT_VAL)
                .and()
                .keyIsVal(VARCHAR, SOME_AND_KEY, SOME_STRING_VAL)
                .build();

        assertQueryPartsList(actualQueryParts);

        String actualQuery = prepareQuery(actualQueryParts);

        Assertions.assertEquals(expectedQuery, actualQuery);

        verify(mockPreparedStatement).setObject(1, SOME_INT_VAL, INT);
        verify(mockPreparedStatement).setObject(2, SOME_STRING_VAL, VARCHAR);
    }

    private String prepareQuery(List<QueryPart> actualQueryParts) throws SQLException {
        String actualQuery = "";

        for (QueryPart actualQueryPart : actualQueryParts) {
            actualQuery = actualQueryPart.apply(actualQuery);
            actualQueryPart.apply(mockPreparedStatement);
        }
        return actualQuery;
    }

    private void assertQueryPartsList(List<QueryPart> actualQueryParts) {
        Assertions.assertNotNull(actualQueryParts);
        Assertions.assertEquals(LinkedList.class, actualQueryParts.getClass());
    }
}