package com.eu.at_it.sql_wrapper.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsertQueryResultProcessorFunctionTest {
    private final InsertQueryResultProcessorFunction insertQueryResultProcessorFunction = new InsertQueryResultProcessorFunction();

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Test
    void apply_shouldExecuteUpdateAndReturnInsertedId() throws SQLException {
        ResultSet mockResultSet = mock(ResultSet.class);
        int insertedRows = 1;
        when(mockPreparedStatement.executeUpdate()).thenReturn(insertedRows);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);

        insertQueryResultProcessorFunction.apply(mockPreparedStatement);

        verify(mockPreparedStatement).executeUpdate();
        verify(mockResultSet).next();
        verify(mockResultSet).getInt(Statement.RETURN_GENERATED_KEYS);
    }

    @Test
    void apply_shouldExecuteUpdateAndThrowExceptionWhenNoRowsInserted() throws SQLException {
        int insertedRows = 0;
        when(mockPreparedStatement.executeUpdate()).thenReturn(insertedRows);

        Assertions.assertThrows(RuntimeException.class, () -> insertQueryResultProcessorFunction.apply(mockPreparedStatement));

        verifyNoMoreInteractions(mockPreparedStatement);
    }

    @Test
    void apply_shouldThrowRuntimeException() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenThrow(SQLException.class);
        Assertions.assertThrows(RuntimeException.class, () -> insertQueryResultProcessorFunction.apply(mockPreparedStatement));
    }
}