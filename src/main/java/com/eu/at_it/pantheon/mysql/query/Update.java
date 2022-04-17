package com.eu.at_it.pantheon.mysql.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Update implements QueryPart {
    static final String UPDATE = "UPDATE ";
    private static final String DELIMITER = ", ";
    private final String tableName;
    private final List<MySqlValue> values;
    private static final String PLACEHOLDER = " = ?";


    public Update(String tableName, LinkedList<MySqlValue> values) {
        this.tableName = tableName;
        this.values = new LinkedList<>(values);
    }

    @Override
    public String apply(String query) {
        MySqlValue mySqlValue = values.get(0);

        StringBuilder keysPlaceholderBuilder = new StringBuilder(mySqlValue.getKey().concat(PLACEHOLDER));

        values.remove(0);

        for (MySqlValue value : values) {
            keysPlaceholderBuilder.append(DELIMITER);
            keysPlaceholderBuilder.append(value.getKey().concat(" = ?"));
        }

        return query.concat(UPDATE).concat(tableName).concat(" SET ").concat(keysPlaceholderBuilder.toString());
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
        Update update = (Update) o;
        return Objects.equals(tableName, update.tableName) && Objects.equals(values, update.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, values);
    }
}
