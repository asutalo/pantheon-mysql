package com.eu.at_it.sql_wrapper.query;

public class Where extends KeyWord implements QueryPart {
    static final String WHERE = " WHERE ";

    public Where() {
    }

    @Override
    public String apply(String query) {
        return query.concat(WHERE);
    }
}
