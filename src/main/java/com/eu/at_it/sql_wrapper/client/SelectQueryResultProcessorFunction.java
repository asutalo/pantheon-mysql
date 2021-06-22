package com.eu.at_it.sql_wrapper.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

class SelectQueryResultProcessorFunction implements Function<PreparedStatement, ResultSet> {
    private final CachedRowSetConversionFunction cachedRowSetConversionFunction;

    SelectQueryResultProcessorFunction(CachedRowSetConversionFunction cachedRowSetConversionFunction) {
        this.cachedRowSetConversionFunction = cachedRowSetConversionFunction;
    }

    @Override
    public ResultSet apply(PreparedStatement preparedStatement) {
        try {
            return cachedRowSetConversionFunction.apply(preparedStatement.executeQuery());
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }
}