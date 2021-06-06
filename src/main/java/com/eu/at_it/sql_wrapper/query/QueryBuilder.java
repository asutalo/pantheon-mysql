package com.eu.at_it.sql_wrapper.query;

import java.util.LinkedList;
import java.util.List;

public class QueryBuilder {
    private final List<QueryPart> queryParts = new LinkedList<>();
    private int paramIndex = 0;

    public QueryBuilder select() {
        queryParts.add(new Select());
        return this;
    }

    public QueryBuilder delete() {
        queryParts.add(new Delete());
        return this;
    }

    public QueryBuilder setString(String key, String value) {
        queryParts.add(new Set());
        queryParts.add(new Key(key));
        queryParts.add(new EqualsString(value, getCurrentIndex()));
        return this;
    }

    public QueryBuilder setInt(String key, int value) {
        queryParts.add(new Set());
        queryParts.add(new Key(key));
        queryParts.add(new EqualsInt(value, getCurrentIndex()));
        return this;
    }

    public QueryBuilder stringKeyValue(String key, String value) {
        queryParts.add(new Key(Key.COMMA, key));
        queryParts.add(new EqualsString(value, getCurrentIndex()));
        return this;
    }

    public QueryBuilder intKeyValue(String key, int value) {
        queryParts.add(new Key(Key.COMMA, key));
        queryParts.add(new EqualsInt(value, getCurrentIndex()));
        return this;
    }

    public QueryBuilder update(String tableName) {
        queryParts.add(new Update(tableName));
        return this;
    }

    public QueryBuilder from(String tableName) {
        queryParts.add(new From(tableName));
        return this;
    }

    public QueryBuilder where(String key) {
        queryParts.add(new Where(key));
        return this;
    }

    public QueryBuilder and(String key) {
        queryParts.add(new And(key));
        return this;
    }

    public QueryBuilder equalsInt(Integer value) {
        queryParts.add(new EqualsInt(value, getCurrentIndex()));
        return this;
    }

    public QueryBuilder equalsString(String value) {
        queryParts.add(new EqualsString(value, getCurrentIndex()));
        return this;
    }

    private int getCurrentIndex() {
        paramIndex++;
        return paramIndex;
    }

    public List<QueryPart> build() {
        return queryParts;
    }
}
