package com.eu.at_it.pantheon.mysql.query;

import com.eu.at_it.pantheon.helper.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class QueryBuilder {
    private static final String NONE = "";
    private static final String COMMA = ", ";
    private final List<QueryPart> queryParts = new LinkedList<>();
    private int paramIndex = 0;

    public void select() {
        queryParts.add(new Select());
    }

    public void select(ArrayList<Pair<String, String>> columnsAndAliases) {
        queryParts.add(new SelectWithAliases(columnsAndAliases));
    }

    public void insert(String tableName, LinkedList<MySqlValue> values) {
        injectIndexes(values);
        queryParts.add(new Insert(tableName, values));
    }

    public void delete() {
        queryParts.add(new Delete());
    }

    public void update(String tableName, LinkedList<MySqlValue> values) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryBuilder that = (QueryBuilder) o;
        return paramIndex == that.paramIndex && Objects.equals(queryParts, that.queryParts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryParts, paramIndex);
    }
}
