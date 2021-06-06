package com.eu.at_it.sql_wrapper.query;

public class And implements QueryPart {
    static final String AND = " AND ";
    private final String key;

    public And(String key) {
        this.key = key;
    }

    @Override
    public String apply(String query) {
        return query.concat(AND).concat(key).concat(" ");
    }
}
