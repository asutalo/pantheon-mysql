package com.eu.at_it.pantheon.mysql.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Insert implements QueryPart {
    private static final String DELIMITER = ", ";
    private static final String PLACEHOLDER = "?";


    private final String tableName;
    private final List<MySqlValue> values;

    public Insert(String tableName, LinkedList<MySqlValue> values) {
        this.tableName = tableName;
        this.values = values;
    }

    @Override
    public String apply(String query) {
        MySqlValue mySqlValue = values.get(0);
        String mySqlValueKey = mySqlValue.getKey();

        StringBuilder keysBuilder = new StringBuilder(mySqlValueKey);
        StringBuilder placeholdersBuilder = new StringBuilder(PLACEHOLDER);

        values.remove(0);

        for (MySqlValue value : values) {
            keysBuilder.append(DELIMITER);
            placeholdersBuilder.append(DELIMITER);

            keysBuilder.append(value.getKey());
            placeholdersBuilder.append(PLACEHOLDER);
        }

        return query.concat("INSERT INTO ").concat(tableName).concat(" (").concat(keysBuilder.toString()).concat(") VALUES (").concat(placeholdersBuilder.toString()).concat(")");
    }

    @Override
    public void apply(PreparedStatement preparedStatement) throws SQLException {
        for (MySqlValue mySqlValue : values) {
            preparedStatement.setObject(mySqlValue.getParamIndex(), mySqlValue.getValue(), mySqlValue.getMysqlType());
        }
    }

    public String getTableName() {
        return tableName;
    }

    public List<MySqlValue> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Insert insert = (Insert) o;
        return Objects.equals(tableName, insert.tableName) && Objects.equals(values, insert.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, values);
    }
}
