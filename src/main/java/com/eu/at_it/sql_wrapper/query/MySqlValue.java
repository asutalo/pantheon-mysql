package com.eu.at_it.sql_wrapper.query;

import com.mysql.cj.MysqlType;

import java.util.Objects;

public class MySqlValue {
    private final MysqlType mysqlType;
    private final String key;
    private final Object value;
    private int paramIndex;

    public MySqlValue(MysqlType mysqlType, String key, Object value) {
        this.mysqlType = mysqlType;
        this.key = key;
        this.value = value;
    }

    public MysqlType getMysqlType() {
        return mysqlType;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    int getParamIndex() {
        return paramIndex;
    }

    void setParamIndex(int paramIndex) {
        this.paramIndex = paramIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MySqlValue that = (MySqlValue) o;
        return paramIndex == that.paramIndex && mysqlType == that.mysqlType && Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mysqlType, key, value, paramIndex);
    }
}
