package com.eu.at_it.sql_wrapper.query;

public class Select extends KeyWord implements QueryPart {
    static final String SELECT = "SELECT ";

    @Override
    public String apply(String query) {
        return query.concat(SELECT);
    }
}