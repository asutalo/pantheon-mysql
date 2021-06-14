package com.eu.at_it.sql_wrapper.query;

import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class WhereTest {
    private static final String SOME_QUERY = "SOME_QUERY";

    @Test
    void apply() {
        String expectedQuery = SOME_QUERY + Where.WHERE;

        assertEquals(expectedQuery, new Where().apply(SOME_QUERY));
    }

    @Test
    void applyOnPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        new Where().apply(mockPreparedStatement);

        verifyNoInteractions(mockPreparedStatement);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void isKeyWord() {
        Where where = new Where();
        assertTrue(where instanceof KeyWord);
    }
}