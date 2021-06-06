package com.eu.at_it.sql_wrapper.query;

import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class FromTest {
    private static final String SOME_TABLE = "SOME_TABLE";
    private static final String SOME_QUERY = "SOME_QUERY";

    @Test
    void apply() {
        String expectedQuery = SOME_QUERY + From.FROM + SOME_TABLE;

        assertEquals(expectedQuery, new From(SOME_TABLE).apply(SOME_QUERY));
    }

    @Test
    void applyOnPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        new From(SOME_TABLE).apply(mockPreparedStatement);

        verifyNoInteractions(mockPreparedStatement);
    }

    @Test
    void getTableName() {
        assertEquals(SOME_TABLE, new From(SOME_TABLE).getTableName());
    }
}