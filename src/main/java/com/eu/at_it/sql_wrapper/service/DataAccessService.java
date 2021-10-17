package com.eu.at_it.sql_wrapper.service;

import com.eu.at_it.sql_wrapper.query.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public interface DataAccessService<T> {
    T update(T toUpdate) throws SQLException;

    T save(T toSave) throws SQLException;

    void delete(T toDelete) throws SQLException;

    QueryBuilder filteredSelect();

    T get(QueryBuilder filteredSelect) throws SQLException, IllegalStateException;

    List<T> getAll() throws SQLException;

    List<T> getAll(QueryBuilder filteredSelect) throws SQLException;
}
