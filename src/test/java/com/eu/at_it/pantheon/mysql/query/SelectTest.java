package com.eu.at_it.pantheon.mysql.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class SelectTest {
    private static final String SOME_QUERY = "SOME_QUERY";

    @Test
    void apply() {
        String expectedQuery = SOME_QUERY + Select.SELECT;

        assertEquals(expectedQuery, new Select().apply(SOME_QUERY));
    }

    @Test
    void applyOnPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        new Select().apply(mockPreparedStatement);

        verifyNoInteractions(mockPreparedStatement);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void isKeyWord() {
        Select select = new Select();
        assertTrue(select instanceof KeyWord);
    }

    @Test
    void equals() {
        Select select1 = new Select();
        Select select2 = new Select();

        Assertions.assertEquals(select1, select2);
    }

    @Test
    void hashcode() {
        Select select = new Select();

        Assertions.assertEquals(select.hashCode(), select.hashCode());
    }
}