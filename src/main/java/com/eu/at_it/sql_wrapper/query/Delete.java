package com.eu.at_it.sql_wrapper.query;

public class Delete implements QueryPart {
    static final String DELETE = "DELETE ";

    @Override
    public String apply(String query) {
        return query.concat(DELETE);
    }
}
