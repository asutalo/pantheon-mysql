package com.eu.at_it.sql_wrapper.client;

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
import java.util.List;

import static com.eu.at_it.sql_wrapper.client.MySqlClient.EMPTY_QUERY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MySqlClientTest {
    static final String SOME_QUERY = "SOME_QUERY";

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

    @InjectMocks
    private MySqlClient mySqlClient;


    @Test
    void prepAndExecute() throws SQLException {
        List<QueryPart> mockQueryParts = mockQueryPartList();
        MySqlClient spy = spy(mySqlClient);

        RowSet mockRowSet = mock(RowSet.class);
        CachedRowSet mockCachedRowSet = mock(CachedRowSet.class);

        when(mockConnector.connect()).thenReturn(mockConnection);
        when(mockRowSetFactory.createCachedRowSet()).thenReturn(mockCachedRowSet);
        doReturn(SOME_QUERY).when(spy).getQuery(mockQueryParts);
        doReturn(mockPreparedStatement).when(spy).prepareStatement(SOME_QUERY, mockQueryParts, mockConnection);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockRowSet);

        ResultSet actualResultSet = spy.prepAndExecute(mockQueryParts);

        verify(mockCachedRowSet).populate(mockRowSet);
        verify(mockPreparedStatement).close();
        verify(mockConnector).close(mockConnection);

        assertEquals(mockCachedRowSet, actualResultSet);

    }

    @Test
    void prepareStatement() throws SQLException {
        List<QueryPart> mockQueryParts = mockQueryPartList();
        when(mockConnection.prepareStatement(SOME_QUERY)).thenReturn(mockPreparedStatement);

        PreparedStatement actualPreparedStatement = mySqlClient.prepareStatement(SOME_QUERY, mockQueryParts, mockConnection);

        verify(mockQueryPart, times(mockQueryParts.size())).apply(mockPreparedStatement);
        assertEquals(mockPreparedStatement, actualPreparedStatement);
    }

    @Test
    void getQuery_shouldApplyQueryParts() {
        List<QueryPart> queryParts = mockQueryPartList();

        when(mockQueryPart.apply(anyString())).thenReturn(SOME_QUERY);

        String actualQuery = mySqlClient.getQuery(queryParts);

        verify(mockQueryPart, times(queryParts.size())).apply(anyString());
        verify(mockQueryPart).apply(EMPTY_QUERY);
        assertEquals(SOME_QUERY, actualQuery);
    }

    private List<QueryPart> mockQueryPartList() {
        return List.of(mockQueryPart, mockQueryPart, mockQueryPart);
    }
}