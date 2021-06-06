package com.eu.at_it.sql_wrapper.query;

import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class AndTest {
    private static final String SOME_KEY = "someKey";

    @Test
    void apply() {
        String query = "query";
        String expectedQuery = query + And.AND + SOME_KEY + " ";

        assertEquals(expectedQuery, new And(SOME_KEY).apply(query));
    }

    @Test
    void applyOnPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        new And(SOME_KEY).apply(mockPreparedStatement);

        verifyNoInteractions(mockPreparedStatement);
    }
}