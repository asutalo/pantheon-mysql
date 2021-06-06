package com.eu.at_it.sql_wrapper.client;

import com.eu.at_it.sql_wrapper.query.QueryPart;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MySqlClient {
    static final String EMPTY_QUERY = "";
    private final Connector connector;
    private final RowSetFactory rowSetFactory;

    public MySqlClient(Connector connector, RowSetFactory rowSetFactory) {
        this.connector = connector;
        this.rowSetFactory = rowSetFactory;
    }

    public ResultSet prepAndExecute(List<QueryPart> queryParts) throws SQLException {
        Connection connection = connector.connect();
        CachedRowSet cachedRowSet = rowSetFactory.createCachedRowSet();
        String query = getQuery(queryParts);
        PreparedStatement preparedStatement = prepareStatement(query, queryParts, connection);

        cachedRowSet.populate(preparedStatement.executeQuery());

        preparedStatement.close();
        connector.close(connection);

        return cachedRowSet;
    }

    String getQuery(List<QueryPart> queryParts) {
        String baseQuery = EMPTY_QUERY;

        for (QueryPart queryPart : queryParts) {
            baseQuery = queryPart.apply(baseQuery);
        }
        return baseQuery;
    }

    PreparedStatement prepareStatement(String preparedQuery, List<QueryPart> queryParts, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(preparedQuery);

        for (QueryPart queryPart : queryParts) {
            queryPart.apply(preparedStatement);
        }

        return preparedStatement;
    }
}
