package com.eu.at_it.sql_wrapper.query;

import com.mysql.cj.MysqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class QueryBuilder {
    private static final String NONE = "";
    private static final String COMMA = ", ";
    private final List<QueryPart> queryParts = new LinkedList<>();
    private int paramIndex = 0;

    public QueryBuilder select() {
        queryParts.add(new Select());
        return this;
    }

    public QueryBuilder selectAll() {
        queryParts.add(new Select());
        queryParts.add(new All());
        return this;
    }

    public QueryBuilder insert(String tableName) {
        queryParts.add(new Insert(tableName));
        return this;
    }

    public QueryBuilder delete() {
        queryParts.add(new Delete());
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

    public QueryBuilder where() {
        queryParts.add(new Where());
        return this;
    }

    public QueryBuilder and() {
        queryParts.add(new And());
        return this;
    }

    public QueryBuilder set() {
        queryParts.add(new Set());
        return this;
    }

    public QueryBuilder valAsKey(MysqlType valType, Object value, String key) {
        queryParts.add(new ValKey(valType, value, key, getSeparator(), getCurrentIndex()));
        return this;
    }

    public QueryBuilder keyIsVal(MysqlType valueType, String key, Object value) {
        queryParts.add(new KeyVal(valueType, key, value, getSeparator(), getCurrentIndex()));
        return this;
    }

    public List<QueryPart> queryParts() {
        return queryParts;
    }

    void setQueryParts(List<QueryPart> queryParts) {
        this.queryParts.clear();
        this.queryParts.addAll(queryParts);
    }

    private String getSeparator() {
        String separator = COMMA;

        if (queryParts.get(queryParts.size() - 1) instanceof KeyWord) {
            separator = NONE;
        }

        return separator;
    }

    public String buildQueryString() {
        String query = "";
        for (QueryPart queryPart : queryParts) {
            query = queryPart.apply(query);
        }

        return query;
    }

    public void prepareStatement(PreparedStatement preparedStatement) throws SQLException {
        for (QueryPart queryPart : queryParts) {
            queryPart.apply(preparedStatement);
        }
    }

    private int getCurrentIndex() {
        paramIndex++;
        return paramIndex;
    }
}
