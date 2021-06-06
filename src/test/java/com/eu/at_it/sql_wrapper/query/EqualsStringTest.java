package com.eu.at_it.sql_wrapper.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.eu.at_it.sql_wrapper.query.Equals.EQUALS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EqualsStringTest {
    private static final int SOME_INDEX = 1;
    private static final String SOME_VALUE = "SOME_VALUE";
    private EqualsString equalsString;

    @BeforeEach
    void setUp() {
        equalsString = new EqualsString(SOME_VALUE, SOME_INDEX);
    }

    @Test
    void applyToString() {
        String query = "query";
        String expectedQuery = query + EQUALS;

        assertEquals(expectedQuery, equalsString.apply(query));
    }

    @Test
    void applyToPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        equalsString.apply(mockPreparedStatement);

        verify(mockPreparedStatement).setString(SOME_INDEX, SOME_VALUE);
    }

    @Test
    void getValue() {
        assertEquals(SOME_VALUE, equalsString.getValue());

    }
}