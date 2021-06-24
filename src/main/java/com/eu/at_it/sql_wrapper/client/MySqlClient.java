package com.eu.at_it.sql_wrapper.client;

import com.eu.at_it.sql_wrapper.query.QueryBuilder;

import javax.sql.rowset.RowSetFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class MySqlClient {
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
        PreparedStatement preparedStatement = queryBuilder.prepareStatement(connection);
        T apply = preparedStatementExecutor.apply(preparedStatement);
        preparedStatement.close();
        connector.close(connection);

        return apply;
    }
}
