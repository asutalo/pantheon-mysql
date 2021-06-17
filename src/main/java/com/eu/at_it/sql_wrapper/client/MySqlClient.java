package com.eu.at_it.sql_wrapper.client;

import com.eu.at_it.sql_wrapper.query.QueryBuilder;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlClient {
    private final Connector connector;
    private final RowSetFactory rowSetFactory;

    public MySqlClient(Connector connector, RowSetFactory rowSetFactory) {
        this.connector = connector;
        this.rowSetFactory = rowSetFactory;
    }

    public ResultSet prepAndExecute(QueryBuilder queryBuilder) throws SQLException {
        Connection connection = connector.connect();
        CachedRowSet cachedRowSet = rowSetFactory.createCachedRowSet();

        PreparedStatement preparedStatement = queryBuilder.prepareStatement(connection);

        cachedRowSet.populate(preparedStatement.executeQuery());

        preparedStatement.close();
        connector.close(connection);

        return cachedRowSet;
    }
}
