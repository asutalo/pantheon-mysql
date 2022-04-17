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
    private final List<MySqlValue> valuesForQuery;
    private final List<MySqlValue> valuesForParams;

    public Insert(String tableName, LinkedList<MySqlValue> valuesForQuery) {
        this.tableName = tableName;
        this.valuesForQuery = new LinkedList<>(valuesForQuery);
        this.valuesForParams = new LinkedList<>(valuesForQuery);
    }

    @Override
    public String apply(String query) {
        MySqlValue mySqlValue = valuesForQuery.get(0);
        String mySqlValueKey = mySqlValue.getKey();

        StringBuilder keysBuilder = new StringBuilder(mySqlValueKey);
        StringBuilder placeholdersBuilder = new StringBuilder(PLACEHOLDER);

        valuesForQuery.remove(0);

        for (MySqlValue value : valuesForQuery) {
            keysBuilder.append(DELIMITER);
            placeholdersBuilder.append(DELIMITER);

            keysBuilder.append(value.getKey());
            placeholdersBuilder.append(PLACEHOLDER);
        }

        return query.concat("INSERT INTO ").concat(tableName).concat(" (").concat(keysBuilder.toString()).concat(") VALUES (").concat(placeholdersBuilder.toString()).concat(")");
    }

    @Override
    public void apply(PreparedStatement preparedStatement) throws SQLException {
        for (MySqlValue mySqlValue : valuesForParams) {
            preparedStatement.setObject(mySqlValue.getParamIndex(), mySqlValue.getValue(), mySqlValue.getMysqlType());
        }
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Insert insert = (Insert) o;
        return Objects.equals(tableName, insert.tableName) && Objects.equals(valuesForQuery, insert.valuesForQuery) && Objects.equals(valuesForParams, insert.valuesForParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, valuesForQuery, valuesForParams);
    }
}
