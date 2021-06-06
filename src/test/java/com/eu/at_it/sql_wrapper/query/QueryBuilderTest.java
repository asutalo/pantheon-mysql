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
                .select()
                .from(SOME_TABLE)
                .where(SOME_WHERE_KEY)
                .equalsInt(SOME_INT_VAL)
                .and(SOME_AND_KEY)
                .equalsString(SOME_STRING_VAL)
                .build();

        assertQueryPartsList(actualQueryParts);

        String actualQuery = prepareQuery(actualQueryParts);

        Assertions.assertEquals(expectedQuery, actualQuery);

        verify(mockPreparedStatement).setInt(1, SOME_INT_VAL);
        verify(mockPreparedStatement).setString(2, SOME_STRING_VAL);
    }

    @Test
    void buildUpdateQuerySetInt() throws SQLException {
        String expectedQuery = "UPDATE " + SOME_TABLE + " SET " + SOME_KEY + " = ?, " + SOME_OTHER_KEY + " = ? WHERE " + SOME_WHERE_KEY + " = ? AND " + SOME_AND_KEY + " = ?";

        List<QueryPart> actualQueryParts = new QueryBuilder()
                .update(SOME_TABLE)
                .setInt(SOME_KEY, SOME_INT_VAL)
                .stringKeyValue(SOME_OTHER_KEY, SOME_STRING_VAL)
                .where(SOME_WHERE_KEY)
                .equalsInt(SOME_INT_VAL)
                .and(SOME_AND_KEY)
                .equalsString(SOME_STRING_VAL)
                .build();

        assertQueryPartsList(actualQueryParts);

        String actualQuery = prepareQuery(actualQueryParts);

        Assertions.assertEquals(expectedQuery, actualQuery);

        verify(mockPreparedStatement).setInt(1, SOME_INT_VAL);
        verify(mockPreparedStatement).setString(2, SOME_STRING_VAL);
        verify(mockPreparedStatement).setInt(3, SOME_INT_VAL);
        verify(mockPreparedStatement).setString(4, SOME_STRING_VAL);
    }

    @Test
    void buildUpdateQuerySetString() throws SQLException {
        String expectedQuery = "UPDATE " + SOME_TABLE + " SET " + SOME_KEY + " = ?, " + SOME_OTHER_KEY + " = ? WHERE " + SOME_WHERE_KEY + " = ? AND " + SOME_AND_KEY + " = ?";

        List<QueryPart> actualQueryParts = new QueryBuilder()
                .update(SOME_TABLE)
                .setString(SOME_KEY, SOME_STRING_VAL)
                .intKeyValue(SOME_OTHER_KEY, SOME_INT_VAL)
                .where(SOME_WHERE_KEY)
                .equalsInt(SOME_INT_VAL)
                .and(SOME_AND_KEY)
                .equalsString(SOME_STRING_VAL)
                .build();

        assertQueryPartsList(actualQueryParts);

        String actualQuery = prepareQuery(actualQueryParts);

        Assertions.assertEquals(expectedQuery, actualQuery);

        verify(mockPreparedStatement).setString(1, SOME_STRING_VAL);
        verify(mockPreparedStatement).setInt(2, SOME_INT_VAL);
        verify(mockPreparedStatement).setInt(3, SOME_INT_VAL);
        verify(mockPreparedStatement).setString(4, SOME_STRING_VAL);
    }

    @Test
    void buildDelete() throws SQLException {
        String expectedQuery = "DELETE FROM " + SOME_TABLE + " WHERE " + SOME_WHERE_KEY + " = ? AND " + SOME_AND_KEY + " = ?";

        List<QueryPart> actualQueryParts = new QueryBuilder()
                .delete()
                .from(SOME_TABLE)
                .where(SOME_WHERE_KEY)
                .equalsInt(SOME_INT_VAL)
                .and(SOME_AND_KEY)
                .equalsString(SOME_STRING_VAL)
                .build();

        assertQueryPartsList(actualQueryParts);

        String actualQuery = prepareQuery(actualQueryParts);

        Assertions.assertEquals(expectedQuery, actualQuery);

        verify(mockPreparedStatement).setInt(1, SOME_INT_VAL);
        verify(mockPreparedStatement).setString(2, SOME_STRING_VAL);
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