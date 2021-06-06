package com.eu.at_it.sql_wrapper.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EqualsString extends Equals implements QueryPart {
    private final String value;
    private final int index;

    public EqualsString(String value, int index) {
        this.value = value;
        this.index = index;
    }

    @Override
    public void apply(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(index, value);
    }

    public String getValue() {
        return value;
    }
}
