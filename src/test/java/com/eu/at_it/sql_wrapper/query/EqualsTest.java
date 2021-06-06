package com.eu.at_it.sql_wrapper.query;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.eu.at_it.sql_wrapper.query.Equals.EQUALS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EqualsTest {
    @Test
    void apply() {
        Equals equals = Mockito.mock(Equals.class, Mockito.CALLS_REAL_METHODS);
        String query = "query";
        String expectedQuery = query + EQUALS;
        assertEquals(expectedQuery, equals.apply(query));
    }
}