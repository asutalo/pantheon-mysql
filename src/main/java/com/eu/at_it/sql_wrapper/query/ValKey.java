package com.eu.at_it.sql_wrapper.query;

import com.mysql.cj.MysqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ValKey implements QueryPart {
    static final String VAL = "?";
    static final String AS = " AS ";

    private final Object value;
    private final String key;
    private final String separator;
    private final int index;
    private final MysqlType targetType;

    public ValKey(MysqlType targetType, Object value, String key, String separator, int index) {
        this.targetType = targetType;
        this.value = value;
        this.key = key;
        this.separator = separator;
        this.index = index;
    }

    @Override
    public String apply(String query) {
        return query.concat(separator).concat(VAL).concat(AS).concat(key);
    }

    @Override
    public void apply(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setObject(index, value, targetType);
    }

    public String getKey() {
        return key;
    }

    public MysqlType getValueType() {
        return targetType;
    }
}
