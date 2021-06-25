package com.eu.at_it.sql_wrapper.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class QueryBuilder {
    private static final String NONE = "";
    private static final String COMMA = ", ";
    private final List<QueryPart> queryParts = new LinkedList<>();
    private int paramIndex = 0;

    public void select() {
        queryParts.add(new Select());
    }

    public void insert(String tableName, List<MySqlValue> values) {
        injectIndexes(values);
        queryParts.add(new Insert(tableName, values));
    }

    public void delete() {
        queryParts.add(new Delete());
    }

    public void update(String tableName, List<MySqlValue> values) {
        injectIndexes(values);
        queryParts.add(new Update(tableName, values));
    }

    public void from(String tableName) {
        queryParts.add(new From(tableName));
    }

    public void where() {
        queryParts.add(new Where());
    }

    public void and() {
        queryParts.add(new And());
    }

    public void keyIsVal(MySqlValue value) {
        queryParts.add(new KeyVal(value.getMysqlType(), value.getKey(), value.getValue(), getSeparator(), getCurrentIndex()));
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

        return query.concat(";");
    }

    public PreparedStatement prepareStatement(Connection connection) throws SQLException {
        String queryString = buildQueryString();

        PreparedStatement preparedStatement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);

        for (QueryPart queryPart : queryParts) {
            queryPart.apply(preparedStatement);
        }

        return preparedStatement;
    }

    private int getCurrentIndex() {
        paramIndex++;
        return paramIndex;
    }

    private void injectIndexes(List<MySqlValue> values) {
        values.forEach(value -> value.setParamIndex(getCurrentIndex()));
    }
}
