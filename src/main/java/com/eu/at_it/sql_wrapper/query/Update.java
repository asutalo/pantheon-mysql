package com.eu.at_it.sql_wrapper.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class Update implements QueryPart {
    static final String UPDATE = "UPDATE ";
    private final String tableName;
    private final List<MySqlValue> values;

    public Update(String tableName, List<MySqlValue> values) {
        this.tableName = tableName;
        this.values = values;
    }

    @Override
    public String apply(String query) {
        List<String> keysPlaceholders = values.stream().map(mySqlValue1 -> mySqlValue1.getKey().concat(" = ?")).collect(Collectors.toList());

        return query.concat(UPDATE).concat(tableName).concat(" SET ").concat(String.join(", ", keysPlaceholders));
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
}
