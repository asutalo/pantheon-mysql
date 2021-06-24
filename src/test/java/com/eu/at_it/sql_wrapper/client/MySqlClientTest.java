package com.eu.at_it.sql_wrapper.client;

import com.eu.at_it.sql_wrapper.query.QueryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MySqlClientTest {
    @Mock
    private Connector mockConnector;

    @Mock
    private Function<PreparedStatement, Object> mockFunction;

    @Mock
    private ResultSet mockResultSet;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private QueryBuilder mockQueryBuilder;

    @InjectMocks
    private MySqlClient mySqlClient;

    @Test
    void prepAndExecuteSelectQuery_ExecuteWithSelectQueryResultProcessorFunction() throws SQLException {
        MySqlClient spy = spy(mySqlClient);

        doReturn(mockResultSet).when(spy).execute(any(), any());

        ResultSet actual = spy.prepAndExecuteSelectQuery(mockQueryBuilder);

        verify(spy).execute(eq(mockQueryBuilder), any(SelectQueryResultProcessorFunction.class));
        assertEquals(mockResultSet, actual);
    }

    @Test
    void prepAndExecuteInsertQuery_InsertQueryResultProcessorFunction() throws SQLException {
        MySqlClient spy = spy(mySqlClient);

        int expected = 1;
        doReturn(expected).when(spy).execute(any(), any());

        int actual = spy.prepAndExecuteInsertQuery(mockQueryBuilder);

        verify(spy).execute(eq(mockQueryBuilder), any(InsertQueryResultProcessorFunction.class));
        assertEquals(expected, actual);
    }

    @Test
    void prepAndExecuteOtherDmlQuery_OtherDmlQueryResultProcessorFunction() throws SQLException {
        MySqlClient spy = spy(mySqlClient);

        int expected = 1;

        doReturn(expected).when(spy).execute(any(), any());

        int actual = spy.prepAndExecuteOtherDmlQuery(mockQueryBuilder);

        verify(spy).execute(eq(mockQueryBuilder), any(OtherDmlQueryResultProcessorFunction.class));
        assertEquals(expected, actual);
    }

    @Test
    void execute_shouldPrepQueryAndExecuteWithProvidedFunction() throws SQLException {
        when(mockConnector.connect()).thenReturn(mockConnection);
        when(mockQueryBuilder.prepareStatement(mockConnection)).thenReturn(mockPreparedStatement);

        mySqlClient.execute(mockQueryBuilder, mockFunction);

        verify(mockFunction).apply(mockPreparedStatement);
        verify(mockPreparedStatement).close();
        verify(mockConnector).connect();
        verify(mockConnector).close(mockConnection);
    }
}