package com.eu.at_it.sql_wrapper.query;

public abstract class Equals {
    static final String EQUALS = "= ?";

    public String apply(String query) {
        return query.concat(EQUALS);
    }
}
