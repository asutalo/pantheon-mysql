package com.eu.at_it.pantheon.mysql.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelectQueryResultProcessorTest {
    private final SelectQueryResultProcessor selectQueryResultProcessor = new SelectQueryResultProcessor();

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Test
    void apply_shouldExecuteQueryAndConvertGeneratedRows() throws SQLException {
        String someLabel = "someLabel";
        String someOtherLabel = "someOtherLabel";
        String someVal = "someVal";
        String someOtherVal = "someOtherVal";

        ResultSet mockResultSet = mock(ResultSet.class);
        ResultSetMetaData mockResultSetMetaData = mock(ResultSetMetaData.class);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(mockResultSet.getMetaData()).thenReturn(mockResultSetMetaData);
        when(mockResultSetMetaData.getColumnCount()).thenReturn(2);
        when(mockResultSetMetaData.getColumnLabel(1)).thenReturn(someLabel);
        when(mockResultSetMetaData.getColumnLabel(2)).thenReturn(someOtherLabel);

        when(mockResultSet.getObject(1)).thenReturn(someVal).thenReturn(someVal);
        when(mockResultSet.getObject(2)).thenReturn(someOtherVal).thenReturn(someOtherVal);

        Assertions.assertEquals(List.of(Map.of(someLabel, someVal, someOtherLabel, someOtherVal), Map.of(someLabel, someVal, someOtherLabel, someOtherVal)), selectQueryResultProcessor.apply(mockPreparedStatement));
    }

    @Test
    void apply_shouldThrowRuntimeException() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(SQLException.class);
        Assertions.assertThrows(RuntimeException.class, () -> selectQueryResultProcessor.apply(mockPreparedStatement));
    }
}