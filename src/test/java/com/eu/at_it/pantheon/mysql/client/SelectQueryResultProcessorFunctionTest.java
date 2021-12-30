package com.eu.at_it.pantheon.mysql.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelectQueryResultProcessorFunctionTest {
    @Mock
    private CachedRowSetConversionFunction mockCachedRowSetConversionFunction;

    @InjectMocks
    private SelectQueryResultProcessorFunction selectQueryResultProcessorFunction;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Test
    void apply_shouldExecuteQueryAndConvertGeneratedRowsToCachedResultSet() throws SQLException {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        selectQueryResultProcessorFunction.apply(mockPreparedStatement);

        verify(mockPreparedStatement).executeQuery();
        verify(mockCachedRowSetConversionFunction).apply(mockResultSet);
    }

    @Test
    void apply_shouldThrowRuntimeException() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(SQLException.class);
        Assertions.assertThrows(RuntimeException.class, () -> selectQueryResultProcessorFunction.apply(mockPreparedStatement));
    }
}