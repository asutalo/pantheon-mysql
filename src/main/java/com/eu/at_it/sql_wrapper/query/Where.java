package com.eu.at_it.sql_wrapper.query;

public class Where implements QueryPart {
    static final String WHERE = " WHERE ";
    private final String key;

    public Where(String key) {
        this.key = key;
    }

    @Override
    public String apply(String query) {
        return query.concat(WHERE).concat(key).concat(" ");
    }

    public String getKey() {
        return key;
    }
}
