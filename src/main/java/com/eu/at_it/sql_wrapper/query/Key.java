package com.eu.at_it.sql_wrapper.query;

public class Key implements QueryPart {
    public static final String COMMA = ", ";
    private static final String SPACE = " ";
    private final String separator;
    private final String key;

    public Key(String separator, String key) {
        this.separator = separator;
        this.key = key;
    }

    public Key(String key) {
        this.separator = SPACE;
        this.key = key;
    }

    @Override
    public String apply(String query) {
        return query.concat(separator).concat(key).concat(SPACE);
    }

    public String getKey() {
        return key;
    }
}
