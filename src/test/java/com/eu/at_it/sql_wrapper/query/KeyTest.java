package com.eu.at_it.sql_wrapper.query;

import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class KeyTest {
    private static final String SOME_KEY = "SOME_KEY";
    private static final String SOME_QUERY = "SOME_QUERY";

    @Test
    void apply() {
        String expectedQuery = SOME_QUERY + " " + SOME_KEY + " ";

        assertEquals(expectedQuery, new Key(SOME_KEY).apply(SOME_QUERY));
    }

    @Test
    void applyComma() {
        String expectedQuery = SOME_QUERY + ", " + SOME_KEY + " ";

        assertEquals(expectedQuery, new Key(Key.COMMA, SOME_KEY).apply(SOME_QUERY));
    }

    @Test
    void applyOnPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        new Key(SOME_KEY).apply(mockPreparedStatement);

        verifyNoInteractions(mockPreparedStatement);
    }

    @Test
    void getKey() {
        assertEquals(SOME_KEY, new Key(SOME_KEY).getKey());
    }
}