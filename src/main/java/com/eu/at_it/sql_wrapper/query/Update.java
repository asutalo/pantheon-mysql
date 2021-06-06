package com.eu.at_it.sql_wrapper.query;

public class Update implements QueryPart {
    static final String UPDATE = "UPDATE ";
    private final String tableName;

    public Update(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String apply(String query) {
        return query.concat(UPDATE).concat(tableName);
    }

    public String getTableName() {
        return tableName;
    }
}
