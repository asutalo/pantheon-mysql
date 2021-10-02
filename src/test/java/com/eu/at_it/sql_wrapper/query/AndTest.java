package com.eu.at_it.sql_wrapper.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class AndTest {
    @Test
    void apply() {
        String query = "query";
        String expectedQuery = query + And.AND;

        assertEquals(expectedQuery, new And().apply(query));
    }

    @Test
    void applyOnPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        new And().apply(mockPreparedStatement);

        verifyNoInteractions(mockPreparedStatement);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void isKeyWord() {
        And and = new And();
        assertTrue(and instanceof KeyWord);
    }

    @Test
    void equals() {
        And and1 = new And();
        And and2 = new And();

        Assertions.assertEquals(and1, and2);
    }

    @Test
    void hashcode() {
        And and = new And();

        Assertions.assertEquals(and.hashCode(), and.hashCode());
    }
}