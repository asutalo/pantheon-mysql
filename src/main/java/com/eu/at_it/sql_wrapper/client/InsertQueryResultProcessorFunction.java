package com.eu.at_it.sql_wrapper.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

class InsertQueryResultProcessorFunction implements Function<PreparedStatement, ResultSet> {
    private final CachedRowSetConversionFunction cachedRowSetConversionFunction;

    InsertQueryResultProcessorFunction(CachedRowSetConversionFunction cachedRowSetConversionFunction) {
        this.cachedRowSetConversionFunction = cachedRowSetConversionFunction;
    }

    @Override
    public ResultSet apply(PreparedStatement preparedStatement) {
        try {
            preparedStatement.executeUpdate();
            return cachedRowSetConversionFunction.apply(preparedStatement.getGeneratedKeys());
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }
}