package com.eu.at_it.pantheon.mysql.query;

public class Select extends KeyWord implements QueryPart {
    static final String SELECT = "SELECT *";

    @Override
    public String apply(String query) {
        return query.concat(SELECT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
