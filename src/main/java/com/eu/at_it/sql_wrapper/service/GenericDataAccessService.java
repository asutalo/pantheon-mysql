package com.eu.at_it.sql_wrapper.service;

import com.eu.at_it.sql_wrapper.client.MySqlClient;
import com.eu.at_it.sql_wrapper.query.MySqlValue;
import com.eu.at_it.sql_wrapper.query.QueryBuilder;
import com.google.inject.TypeLiteral;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


public class GenericDataAccessService<T> implements DataAccessService<T> {
    private final Instantiator<T> instantiator;
    private final LinkedList<FieldMySqlValue<T>> fieldMySqlValues;
    private final LinkedList<ResultSetFieldValueSetter<T>> resultSetFieldValueSetters;
    private final FieldMySqlValue<T> primaryKeyFieldMySqlValue;
    private final FieldValueSetter<T> primaryKeyFieldValueSetter;
    private final MySqlClient mySqlClient;
    private final String tableName;

    @Inject
    public GenericDataAccessService(MySqlClient mySqlClient, TypeLiteral<T> typeLiteral) {
        this.mySqlClient = mySqlClient;
        Class<T> tClass = (Class<T>) typeLiteral.getType();

        GenericDataAccessServiceProvider genericDataAccessServiceProvider = GenericDataAccessServiceProvider.getInstance();

        genericDataAccessServiceProvider.validateClass(tClass);
        tableName = genericDataAccessServiceProvider.getTableName(tClass);
        instantiator = genericDataAccessServiceProvider.getInstantiator(tClass);
        fieldMySqlValues = genericDataAccessServiceProvider.getFieldMySqlValues(tClass);
        resultSetFieldValueSetters = genericDataAccessServiceProvider.getResultSetFieldValueSetters(tClass);
        primaryKeyFieldMySqlValue = genericDataAccessServiceProvider.getPrimaryKeyFieldMySqlValue(tClass);
        primaryKeyFieldValueSetter = genericDataAccessServiceProvider.getPrimaryKeyFieldValueSetter(tClass);
    }

    private LinkedList<MySqlValue> mySqlValues(T user) {
        LinkedList<MySqlValue> mySqlValues = new LinkedList<>();
        fieldMySqlValues.forEach(getter -> mySqlValues.add(getter.apply(user)));
        return mySqlValues;
    }

    @Override
    public T update(T toUpdate) throws SQLException {
        LinkedList<MySqlValue> mySqlValues = mySqlValues(toUpdate);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.update(tableName, mySqlValues);
        queryBuilder.where();
        queryBuilder.keyIsVal(primaryKeyFieldMySqlValue.apply(toUpdate));

        if (mySqlClient.prepAndExecuteOtherDmlQuery(queryBuilder) > 0) {
            return toUpdate;
        } else {
            throw new RuntimeException("Update failed");
        }
    }

    @Override
    public T save(T toSave) throws SQLException {
        LinkedList<MySqlValue> mySqlValues = mySqlValues(toSave);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.insert(tableName, mySqlValues);

        int insertId = mySqlClient.prepAndExecuteInsertQuery(queryBuilder);

        primaryKeyFieldValueSetter.accept(toSave, insertId);
        return toSave;
    }

    @Override
    public void delete(T toDelete) throws SQLException {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.delete();
        queryBuilder.from(tableName);
        queryBuilder.where();
        queryBuilder.keyIsVal(primaryKeyFieldMySqlValue.apply(toDelete));

        if (mySqlClient.prepAndExecuteOtherDmlQuery(queryBuilder) == 0) {
            throw new RuntimeException("Deletion failed");
        }
    }

    @Override
    public QueryBuilder filteredSelect() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.select();
        queryBuilder.from(tableName);

        return queryBuilder;
    }

    @Override
    public T get(QueryBuilder filteredSelect) throws SQLException, IllegalStateException {
        ResultSet resultSet = mySqlClient.prepAndExecuteSelectQuery(filteredSelect);

        if (resultSet.first() && resultSet.isLast()) {
            return instanceOfT(resultSet);
        }

        throw new IllegalStateException();
    }

    @Override
    public List<T> getAll() throws SQLException {
        return getAll(filteredSelect());
    }

    @Override
    public List<T> getAll(QueryBuilder filteredSelect) throws SQLException {
        ResultSet resultSet = mySqlClient.prepAndExecuteSelectQuery(filteredSelect);

        List<T> elements = new LinkedList<>();
        while (resultSet.next()) {
            elements.add(instanceOfT(resultSet));
        }

        return elements;
    }

    private T instanceOfT(ResultSet resultSet) {
        T instance = instantiator.get();

        resultSetFieldValueSetters.forEach(setter -> setter.accept(instance, resultSet));

        return instance;
    }
}
