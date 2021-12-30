package com.eu.at_it.pantheon.mysql.query;

import com.mysql.cj.MysqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValKey valKey = (ValKey) o;
        return index == valKey.index && Objects.equals(value, valKey.value) && Objects.equals(key, valKey.key) && Objects.equals(separator, valKey.separator) && targetType == valKey.targetType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, key, separator, index, targetType);
    }
}
