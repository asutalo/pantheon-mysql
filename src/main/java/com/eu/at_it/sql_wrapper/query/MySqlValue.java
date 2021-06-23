package com.eu.at_it.sql_wrapper.query;

import com.mysql.cj.MysqlType;

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
}
