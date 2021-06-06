package com.eu.at_it.sql_wrapper.query;

import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class DeleteTest {
    @Test
    void apply() {
        String expectedQuery = Delete.DELETE;

        assertEquals(expectedQuery, new Delete().apply(""));
    }

    @Test
    void applyOnPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        new Delete().apply(mockPreparedStatement);

        verifyNoInteractions(mockPreparedStatement);
    }
}