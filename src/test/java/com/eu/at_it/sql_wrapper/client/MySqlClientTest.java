package com.eu.at_it.sql_wrapper.client;

import com.eu.at_it.sql_wrapper.query.QueryBuilder;
import com.eu.at_it.sql_wrapper.query.QueryPart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MySqlClientTest {
    @Mock
    private Connector mockConnector;

    @Mock
    private RowSetFactory mockRowSetFactory;

    @Mock
    private Connection mockConnection;

    @Mock
    private QueryPart mockQueryPart;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private QueryBuilder mockQueryBuilder;

    @InjectMocks
    private MySqlClient mySqlClient;


    @Test
    void prepAndExecute() throws SQLException {
        RowSet mockRowSet = mock(RowSet.class);
        CachedRowSet mockCachedRowSet = mock(CachedRowSet.class);

        when(mockConnector.connect()).thenReturn(mockConnection);
        when(mockRowSetFactory.createCachedRowSet()).thenReturn(mockCachedRowSet);
        when(mockQueryBuilder.prepareStatement(mockConnection)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockRowSet);

        ResultSet actualResultSet = mySqlClient.prepAndExecute(mockQueryBuilder);

        verify(mockCachedRowSet).populate(mockRowSet);
        verify(mockPreparedStatement).close();
        verify(mockConnector).close(mockConnection);

        assertEquals(mockCachedRowSet, actualResultSet);
    }
}