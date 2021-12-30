package com.eu.at_it.pantheon.mysql.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Insert implements QueryPart {
    private static final String DELIMITER = ", ";

    private final String tableName;
    private final List<MySqlValue> values;

    public Insert(String tableName, LinkedList<MySqlValue> values) {
        this.tableName = tableName;
        this.values = values;
    }

    public Insert(String tableName) {
        this.tableName = tableName;
        values = List.of();
    }

    @Override
    public String apply(String query) {
        List<String> keys = values.stream().map(MySqlValue::getKey).collect(Collectors.toList());
        List<String> marks = values.stream().map(mySqlValue -> "?").collect(Collectors.toList());

        return query.concat("INSERT INTO ").concat(tableName).concat(" (").concat(String.join(DELIMITER, keys)).concat(") VALUES (").concat(String.join(DELIMITER, marks)).concat(")");
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
