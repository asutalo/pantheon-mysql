package com.eu.at_it.pantheon.mysql.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class QueryPartTest {
    @Test
    void defaultApply() throws SQLException {
        QueryPart queryPart = mock(QueryPart.class, Mockito.CALLS_REAL_METHODS);
        String expectedQuery = "query";

        Assertions.assertEquals(expectedQuery, queryPart.apply(expectedQuery));

        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        queryPart.apply(mockPreparedStatement);

        verifyNoInteractions(mockPreparedStatement);
    }
}
