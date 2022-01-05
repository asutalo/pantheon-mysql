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

    /**
     * TODO add support for more complicated transactions by adding method that returns an open connection with
     *  connection.setAutoCommit(false);
     *  not sure if while autocommit is false a ResultSet can be created... might need to play with Savepoints as well...
     *  in any case, should be doable to open a connection, execute multiple queries, and then close connection to persist
     *  https://www.mysqltutorial.org/mysql-jdbc-transaction/
     */
    <T> T execute(QueryBuilder queryBuilder, Function<PreparedStatement, T> preparedStatementExecutor) throws SQLException {
        Connection connection = connector.connect();
        PreparedStatement preparedStatement = queryBuilder.prepareStatement(connection);
        T queryResults = preparedStatementExecutor.apply(preparedStatement);
        preparedStatement.close();
        connector.close(connection);
        return queryResults;
    }
}
