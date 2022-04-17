package com.eu.at_it.pantheon.mysql.query;

import java.util.Objects;

public class From extends KeyWord implements QueryPart {
    static final String FROM = " FROM ";
    static final String SPACE = " ";
    private final String tableName;

    public From(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String apply(String query) {
        return query.concat(FROM).concat(tableName).concat(SPACE).concat(tableName);
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        From from = (From) o;
        return Objects.equals(tableName, from.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName);
    }
}
