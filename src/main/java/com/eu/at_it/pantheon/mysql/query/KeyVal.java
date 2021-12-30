package com.eu.at_it.pantheon.mysql.query;

import com.mysql.cj.MysqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class KeyVal implements QueryPart {
    static final String VAL = " = ?";

    private final Object value;
    private final String key;
    private final String separator;
    private final int index;
    private final MysqlType targetType;

    public KeyVal(MysqlType targetType, String key, Object value, String separator, int index) {
        this.targetType = targetType;
        this.value = value;
        this.key = key;
        this.separator = separator;
        this.index = index;
    }

    @Override
    public String apply(String query) {
        return query.concat(separator).concat(key).concat(VAL);
    }

    @Override
    public void apply(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setObject(index, value, targetType);
    }

    public Object getValue() {
        return value;
    }

    public MysqlType getValueType() {
        return targetType;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyVal keyVal = (KeyVal) o;
        return index == keyVal.index && Objects.equals(value, keyVal.value) && Objects.equals(key, keyVal.key) && Objects.equals(separator, keyVal.separator) && targetType == keyVal.targetType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, key, separator, index, targetType);
    }
}
