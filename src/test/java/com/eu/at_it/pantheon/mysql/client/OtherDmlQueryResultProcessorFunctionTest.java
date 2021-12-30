package com.eu.at_it.pantheon.mysql.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtherDmlQueryResultProcessorFunctionTest {
    @Mock
    private PreparedStatement mockPreparedStatement;

    @Test
    void apply_shouldReturnExecuteUpdateResult() throws SQLException {
        int expected = 1;
        when(mockPreparedStatement.executeUpdate()).thenReturn(expected);

        Integer actual = new OtherDmlQueryResultProcessorFunction().apply(mockPreparedStatement);

        verify(mockPreparedStatement).executeUpdate();
        assertEquals(expected, actual);
    }

    @Test
    void apply_shouldThrowRuntimeException() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenThrow(SQLException.class);
        Assertions.assertThrows(RuntimeException.class, () -> new OtherDmlQueryResultProcessorFunction().apply(mockPreparedStatement));
    }
}