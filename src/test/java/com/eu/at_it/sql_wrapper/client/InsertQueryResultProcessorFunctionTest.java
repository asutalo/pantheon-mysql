package com.eu.at_it.sql_wrapper.client;

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
class InsertQueryResultProcessorFunctionTest {
    @Mock
    private CachedRowSetConversionFunction mockCachedRowSetConversionFunction;

    @InjectMocks
    private InsertQueryResultProcessorFunction insertQueryResultProcessorFunction;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Test
    void apply_shouldExecuteUpdateAndConvertGeneratedRowsToCachedResultSet() throws SQLException {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);

        insertQueryResultProcessorFunction.apply(mockPreparedStatement);

        verify(mockPreparedStatement).executeUpdate();
        verify(mockCachedRowSetConversionFunction).apply(mockResultSet);
    }

    @Test
    void apply_shouldThrowRuntimeException() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenThrow(SQLException.class);
        Assertions.assertThrows(RuntimeException.class, () -> insertQueryResultProcessorFunction.apply(mockPreparedStatement));
    }
}