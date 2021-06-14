package com.eu.at_it.sql_wrapper.query;

public class All implements QueryPart {
    static final String ALL = "* ";

    @Override
    public String apply(String query) {
        return query.concat(ALL);
    }
}
