package com.eu.at_it.sql_wrapper.service;

import com.eu.at_it.sql_wrapper.client.MySqlClient;
import com.eu.at_it.sql_wrapper.query.MySqlValue;
import com.eu.at_it.sql_wrapper.query.QueryBuilder;
import com.google.inject.TypeLiteral;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class GenericDataAccessService<T> implements DataAccessService<T> {
    private final Instantiator<T> instantiator;
    private final FieldMySqlValue<T> primaryKeyFieldMySqlValue;
    private final List<FieldMySqlValue<T>> nonPrimaryKeyFieldMySqlValues;
    private final Map<String, FieldMySqlValue<T>> fieldMySqlValueMap = new HashMap<>(); //will include primary key
    private final List<ResultSetFieldValueSetter<T>> resultSetFieldValueSetters;
    private final Map<String, FieldValueSetter<T>> allExceptPrimaryFieldValueSetterMap; //no primary key included but will include not annotated fields as well
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
        nonPrimaryKeyFieldMySqlValues = genericDataAccessServiceProvider.getNonPrimaryKeyFieldMySqlValues(tClass);
        primaryKeyFieldMySqlValue = genericDataAccessServiceProvider.getPrimaryKeyFieldMySqlValue(tClass);
        primaryKeyFieldValueSetter = genericDataAccessServiceProvider.getPrimaryKeyFieldValueSetter(tClass);
        resultSetFieldValueSetters = genericDataAccessServiceProvider.getResultSetFieldValueSetters(tClass);

        fieldMySqlValueMap.put(primaryKeyFieldMySqlValue.getVariableName(), primaryKeyFieldMySqlValue);
        nonPrimaryKeyFieldMySqlValues.forEach(fieldMySqlValue -> fieldMySqlValueMap.put(fieldMySqlValue.getVariableName(), fieldMySqlValue));

        allExceptPrimaryFieldValueSetterMap = genericDataAccessServiceProvider.getNonPrimaryFieldValueSetterMap(tClass);
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

    public T get(Map<String, Object> filter) throws SQLException, IllegalStateException {
        List<MySqlValue> filterMySqlValues = new ArrayList<>();
        filter.forEach((key, val) -> {
            if (fieldMySqlValueMap.containsKey(key)) {
                filterMySqlValues.add(fieldMySqlValueMap.get(key).of(val));
            }
        });

        if (filterMySqlValues.isEmpty()) throw new IllegalStateException("Provided filters would produce no results");

        QueryBuilder queryBuilder = filteredSelect();
        queryBuilder.where();
        Iterator<MySqlValue> iterator = filterMySqlValues.iterator();

        while (iterator.hasNext()) {
            queryBuilder.keyIsVal(iterator.next());
            if (iterator.hasNext()) queryBuilder.and();
        }

        return get(queryBuilder);
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

    public T instanceOfT(Map<String, Object> values) {
        T instance = instantiator.get();

        values.forEach((key, val) -> {
            if (allExceptPrimaryFieldValueSetterMap.containsKey(key)) {
                allExceptPrimaryFieldValueSetterMap.get(key).accept(instance, val);
            }
        });

        return instance;
    }

    private T instanceOfT(ResultSet resultSet) {
        T instance = instantiator.get();

        resultSetFieldValueSetters.forEach(setter -> setter.accept(instance, resultSet));

        return instance;
    }

    Map<String, FieldMySqlValue<T>> getFieldMySqlValueMap() {
        return fieldMySqlValueMap;
    }

    private LinkedList<MySqlValue> mySqlValues(T user) {
        LinkedList<MySqlValue> mySqlValues = new LinkedList<>();
        nonPrimaryKeyFieldMySqlValues.forEach(getter -> mySqlValues.add(getter.apply(user)));
        return mySqlValues;
    }
}
