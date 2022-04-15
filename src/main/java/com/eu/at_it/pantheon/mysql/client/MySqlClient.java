package com.eu.at_it.pantheon.mysql.client;

import com.eu.at_it.pantheon.client.data.DataClient;
import com.eu.at_it.pantheon.mysql.query.QueryBuilder;

import javax.sql.rowset.RowSetFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class MySqlClient implements DataClient {
    private final Connector connector;
    private final SelectQueryResultProcessorFunction selectQueryResultProcessorFunction;
    private final InsertQueryResultProcessorFunction insertQueryResultProcessorFunction;
    private final OtherDmlQueryResultProcessorFunction otherDmlQueryResultProcessorFunction;

    public MySqlClient(Connector connector, RowSetFactory rowSetFactory) {
        this.connector = connector;

        CachedRowSetConversionFunction cachedRowSetConversionFunction = new CachedRowSetConversionFunction(rowSetFactory);

        selectQueryResultProcessorFunction = new SelectQueryResultProcessorFunction(cachedRowSetConversionFunction);
        insertQueryResultProcessorFunction = new InsertQueryResultProcessorFunction();
        otherDmlQueryResultProcessorFunction = new OtherDmlQueryResultProcessorFunction();

    }

    public ResultSet prepAndExecuteSelectQuery(QueryBuilder queryBuilder) throws SQLException {
        return execute(queryBuilder, selectQueryResultProcessorFunction);
    }

    public int prepAndExecuteInsertQuery(QueryBuilder queryBuilder) throws SQLException {
        return execute(queryBuilder, insertQueryResultProcessorFunction);
    }

    public int prepAndExecuteOtherDmlQuery(QueryBuilder queryBuilder) throws SQLException {
        return execute(queryBuilder, otherDmlQueryResultProcessorFunction);
    }

    <T> T execute(QueryBuilder queryBuilder, Function<PreparedStatement, T> preparedStatementExecutor) throws SQLException {
        Connection connection = connector.connect();
        T queryResults = execute(queryBuilder, preparedStatementExecutor, connection);
        connector.close(connection);
        return queryResults;
    }

    <T> T execute(QueryBuilder queryBuilder, Function<PreparedStatement, T> preparedStatementExecutor, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = queryBuilder.prepareStatement(connection);
        T queryResults = preparedStatementExecutor.apply(preparedStatement);
        preparedStatement.close();
        return queryResults;
    }

    public Connection startTransaction() throws SQLException {
        Connection connection = connector.connect();
        connection.setAutoCommit(false);
        return connection;
    }

    public void endTransaction(Connection connection) throws SQLException {
        connection.commit();
        connector.close(connection);
    }

    public void rollbackTransaction(Connection connection) throws SQLException {
        connection.rollback();
    }
}
