package com.eu.at_it.sql_wrapper.query;

public class And extends KeyWord implements QueryPart {
    static final String AND = " AND ";

    public And() {
    }

    @Override
    public String apply(String query) {
        return query.concat(AND);
    }
}
