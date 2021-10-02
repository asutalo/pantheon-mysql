package com.eu.at_it.sql_wrapper.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @SuppressWarnings("ConstantConditions")
    @Test
    void isKeyWord() {
        Delete delete = new Delete();
        assertTrue(delete instanceof KeyWord);
    }

    @Test
    void equals() {
        Delete delete1 = new Delete();
        Delete delete2 = new Delete();

        Assertions.assertEquals(delete1, delete2);
    }

    @Test
    void hashcode() {
        Delete Delete = new Delete();

        Assertions.assertEquals(Delete.hashCode(), Delete.hashCode());
    }
}