package com.eu.at_it.sql_wrapper.client;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

class CachedRowSetConversionFunction implements Function<ResultSet, CachedRowSet> {
    private final RowSetFactory rowSetFactory;

    CachedRowSetConversionFunction(RowSetFactory rowSetFactory) {
        this.rowSetFactory = rowSetFactory;
    }

    @Override
    public CachedRowSet apply(ResultSet resultSet) {
        try {
            CachedRowSet cachedRowSet = rowSetFactory.createCachedRowSet();

            cachedRowSet.populate(resultSet);

            return cachedRowSet;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

    }
}