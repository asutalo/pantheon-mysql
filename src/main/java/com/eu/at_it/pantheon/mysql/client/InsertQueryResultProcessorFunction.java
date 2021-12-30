package com.eu.at_it.pantheon.mysql.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Function;

class InsertQueryResultProcessorFunction implements Function<PreparedStatement, Integer> {

    @Override
    public Integer apply(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement.executeUpdate() > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                generatedKeys.next();

                return generatedKeys.getInt(Statement.RETURN_GENERATED_KEYS);
            } else {
                throw new RuntimeException("Insert failed, no rows inserted");
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }
}