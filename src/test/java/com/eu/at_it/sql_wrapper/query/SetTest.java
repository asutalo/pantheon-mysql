package com.eu.at_it.sql_wrapper.query;

import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class SetTest {
    private static final String SOME_QUERY = "SOME_QUERY";

    @Test
    void apply() {
        String expectedQuery = SOME_QUERY + Set.SET;

        assertEquals(expectedQuery, new Set().apply(SOME_QUERY));
    }

    @Test
    void applyOnPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        new Set().apply(mockPreparedStatement);

        verifyNoInteractions(mockPreparedStatement);
    }
}