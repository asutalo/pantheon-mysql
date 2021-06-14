package com.eu.at_it.sql_wrapper.query;

public class Insert extends KeyWord implements QueryPart {
    static final String INSERT_INTO = "INSERT INTO ";

    private final String tableName;

    public Insert(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String apply(String query) {
        return query.concat(INSERT_INTO).concat(tableName).concat(" ");
    }

    public String getTableName() {
        return tableName;
    }
}
