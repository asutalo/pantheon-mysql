package com.eu.at_it.sql_wrapper.query;

public class From implements QueryPart {
    static final String FROM = "FROM ";
    private final String tableName;

    public From(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String apply(String query) {
        return query.concat(FROM).concat(tableName);
    }

    public String getTableName() {
        return tableName;
    }
}
