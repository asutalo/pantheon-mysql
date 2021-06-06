package com.eu.at_it.sql_wrapper.query;

public class Set implements QueryPart {
    static final String SET = " SET";

    @Override
    public String apply(String query) {
        return query.concat(SET);
    }
}
