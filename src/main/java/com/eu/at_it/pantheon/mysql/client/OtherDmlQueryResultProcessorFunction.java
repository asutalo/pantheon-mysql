package com.eu.at_it.pantheon.mysql.client;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Function;

class OtherDmlQueryResultProcessorFunction implements Function<PreparedStatement, Integer> {
    @Override
    public Integer apply(PreparedStatement preparedStatement) {
        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }
}