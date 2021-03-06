package com.eu.at_it.pantheon.mysql.client;

import com.eu.at_it.pantheon.mysql.query.QueryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MySqlClientTest {
    @Mock
    private Connector mockConnector;

    @Mock
    private Function<PreparedStatement, Object> mockFunction;

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

        List<Map<String, Object>> expected = List.of();
        doReturn(expected).when(spy).execute(any(), any());

        List<Map<String, Object>> actual = spy.prepAndExecuteSelectQuery(mockQueryBuilder);

        verify(spy).execute(eq(mockQueryBuilder), any(SelectQueryResultProcessor.class));
        assertEquals(expected, actual);
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

    @Test
    void execute_shouldPrepQueryAndExecuteWithProvidedFunctionOnProvidedConnection() throws SQLException {
        when(mockQueryBuilder.prepareStatement(mockConnection)).thenReturn(mockPreparedStatement);

        mySqlClient.execute(mockQueryBuilder, mockFunction, mockConnection);

        verify(mockFunction).apply(mockPreparedStatement);
        verify(mockPreparedStatement).close();
        verifyNoInteractions(mockConnector);
    }

    @Test
    void startTransaction_shouldOpenConnectionWithoutAutoCommit() throws SQLException {
        when(mockConnector.connect()).thenReturn(mockConnection);

        Connection actualConnection = mySqlClient.startTransaction();

        assertNotNull(actualConnection);
        verify(mockConnector).connect();
        verify(mockConnection).setAutoCommit(false);
    }

    @Test
    void endTransaction_shouldCommitChanges() throws SQLException {
        mySqlClient.endTransaction(mockConnection);

        verify(mockConnection).commit();
        verify(mockConnector).close(mockConnection);
    }

    @Test
    void rollbackTransaction_shouldRollback() throws SQLException {
        mySqlClient.rollbackTransaction(mockConnection);

        verify(mockConnection).rollback();
    }
}