package com.eu.at_it.pantheon.mysql.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class FromTest {
    private static final String SOME_TABLE = "SOME_TABLE";
    private static final String SOME_QUERY = "SOME_QUERY";

    @Test
    void apply() {
        String expectedQuery = SOME_QUERY + From.FROM + SOME_TABLE;

        assertEquals(expectedQuery, from().apply(SOME_QUERY));
    }

    @Test
    void applyOnPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        from().apply(mockPreparedStatement);

        verifyNoInteractions(mockPreparedStatement);
    }

    @Test
    void getTableName() {
        assertEquals(SOME_TABLE, from().getTableName());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void isKeyWord() {
        From from = from();
        assertTrue(from instanceof KeyWord);
    }

    @Test
    void equals() {
        From from1 = from();
        From from2 = from();

        Assertions.assertEquals(from1, from2);
    }

    @Test
    void hashcode() {
        From From = from();

        Assertions.assertEquals(From.hashCode(), From.hashCode());
    }

    private From from() {
        return new From(SOME_TABLE);
    }
}