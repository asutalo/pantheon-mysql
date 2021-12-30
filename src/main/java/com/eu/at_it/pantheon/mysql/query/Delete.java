package com.eu.at_it.pantheon.mysql.query;

public class Delete extends KeyWord implements QueryPart {
    static final String DELETE = "DELETE ";

    @Override
    public String apply(String query) {
        return query.concat(DELETE);
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
