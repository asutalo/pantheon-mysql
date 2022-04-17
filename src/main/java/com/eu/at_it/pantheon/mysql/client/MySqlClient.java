package com.eu.at_it.pantheon.mysql.client;

import com.eu.at_it.pantheon.client.data.DataClient;
import com.eu.at_it.pantheon.mysql.query.QueryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MySqlClient implements DataClient {
    private final Connector connector;
    private final SelectQueryResultProcessor selectQueryResultProcessor;
    private final InsertQueryResultProcessorFunction insertQueryResultProcessorFunction;
    private final OtherDmlQueryResultProcessorFunction otherDmlQueryResultProcessorFunction;


    //todo provide a factory for easier instantiation by user
    public MySqlClient(Connector connector) {
        this.connector = connector;

        selectQueryResultProcessor = new SelectQueryResultProcessor();
        insertQueryResultProcessorFunction = new InsertQueryResultProcessorFunction();
        otherDmlQueryResultProcessorFunction = new OtherDmlQueryResultProcessorFunction();

    }

    public List<Map<String, Object>> prepAndExecuteSelectQuery(QueryBuilder queryBuilder) throws SQLException {
        return execute(queryBuilder, selectQueryResultProcessor);
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
