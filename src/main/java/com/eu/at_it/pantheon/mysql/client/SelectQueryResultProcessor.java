package com.eu.at_it.pantheon.mysql.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

class SelectQueryResultProcessor implements Function<PreparedStatement, List<Map<String, Object>>> {
    @Override
    public List<Map<String, Object>> apply(PreparedStatement preparedStatement) {
        try {
            List<Map<String, Object>> fetchedRows = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Map<String, Integer> columnLabelsAndIndexes = columnLabelsAndIndexes(resultSet);

                processRow(columnLabelsAndIndexes, fetchedRows, resultSet);

                while (resultSet.next()) {
                    processRow(columnLabelsAndIndexes, fetchedRows, resultSet);
                }
            }
            return fetchedRows;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    private Map<String, Integer> columnLabelsAndIndexes(ResultSet resultSet) throws SQLException {
        Map<String, Integer> columnLabelsAndIndexes = new HashMap<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 0; i < columnCount; i++) {
            int columnIndex = i + 1;
            columnLabelsAndIndexes.put(metaData.getColumnLabel(columnIndex), columnIndex);
        }

        return columnLabelsAndIndexes;
    }

    private void processRow(Map<String, Integer> columnsAndIndexes, List<Map<String, Object>> result, ResultSet resultSet) throws SQLException {
        Map<String, Object> row = new HashMap<>();

        for (Entry<String, Integer> entry : columnsAndIndexes.entrySet()) {
            row.put(entry.getKey(), resultSet.getObject(entry.getValue()));
        }
        result.add(row);
    }
}