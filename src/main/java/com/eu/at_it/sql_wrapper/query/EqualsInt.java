package com.eu.at_it.sql_wrapper.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EqualsInt extends Equals implements QueryPart {
    private final int value;
    private final int index;

    public EqualsInt(int value, int index) {
        this.value = value;
        this.index = index;
    }

    @Override
    public void apply(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(index, value);
    }

    public int getValue() {
        return value;
    }
}
