package com.eu.at_it.sql_wrapper.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.eu.at_it.sql_wrapper.query.Equals.EQUALS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EqualsIntTest {
    private static final int SOME_INDEX = 1;
    private static final int SOME_VALUE = 2;
    private EqualsInt equalsInt;

    @BeforeEach
    void setUp() {
        equalsInt = new EqualsInt(SOME_VALUE, SOME_INDEX);
    }

    @Test
    void applyToString() {
        String query = "query";
        String expectedQuery = query + EQUALS;

        assertEquals(expectedQuery, equalsInt.apply(query));
    }

    @Test
    void applyToPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        equalsInt.apply(mockPreparedStatement);

        verify(mockPreparedStatement).setInt(SOME_INDEX, SOME_VALUE);
    }

    @Test
    void getValue() {
        assertEquals(SOME_VALUE, equalsInt.getValue());
    }
}