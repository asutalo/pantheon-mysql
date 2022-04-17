package com.eu.at_it.pantheon.mysql.service;

import com.eu.at_it.pantheon.client.data.DataClient;
import com.eu.at_it.pantheon.helper.Pair;
import com.eu.at_it.pantheon.mysql.client.MySqlClient;
import com.eu.at_it.pantheon.mysql.query.MySqlValue;
import com.eu.at_it.pantheon.mysql.query.QueryBuilder;
import com.eu.at_it.pantheon.service.data.DataService;
import com.google.inject.TypeLiteral;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MySQLService<T> implements DataService<T, QueryBuilder> {
    private final Class<T> servingType;
    private Instantiator<T> instantiator;
    private FieldMySqlValue<T> primaryKeyFieldMySqlValue;
    private final Map<String, FieldMySqlValue<T>> fieldMySqlValueMap = new HashMap<>(); //will include primary key
    private List<FieldMySqlValue<T>> nonPrimaryKeyFieldMySqlValues;
    private List<SpecificFieldValueSetter<T>> specificFieldValueSetters;
    private ArrayList<Pair<String, String>> columnsAndAliases;
    private Map<String, FieldValueSetter<T>> allExceptPrimaryFieldValueSetterMap; //no primary key included but will include not annotated fields as well
    private final MySqlClient mySqlClient;
    private FieldValueSetter<T> primaryKeyFieldValueSetter;
    private String tableName;

    public MySQLService(DataClient mySqlClient, TypeLiteral<T> typeLiteral) {
        this.mySqlClient = (MySqlClient) mySqlClient;
        this.servingType = (Class<T>) typeLiteral.getType();
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
        queryBuilder.select(columnsAndAliases);
        queryBuilder.from(tableName);

        return queryBuilder;
    }

    @Override
    public T get(QueryBuilder filteredSelect) throws SQLException, IllegalStateException {
        List<Map<String, Object>> resultSet = mySqlClient.prepAndExecuteSelectQuery(filteredSelect);

        if (resultSet.size() == 1) {
            return fullInstanceOfT(resultSet.get(0));
        }

        throw new IllegalStateException();
    }

    @Override
    public T get(Map<String, Object> filter) throws SQLException, IllegalStateException {
        QueryBuilder queryBuilder = filteredSelect(filter);

        return get(queryBuilder);
    }

    @Override
    public List<T> getAll() throws SQLException {
        return getAll(filteredSelect());
    }

    @Override
    public List<T> getAll(QueryBuilder filteredSelect) throws SQLException {
        List<Map<String, Object>> resultSet = mySqlClient.prepAndExecuteSelectQuery(filteredSelect);
        List<T> elements = new LinkedList<>();

        for (Map<String, Object> row : resultSet) {
            elements.add(fullInstanceOfT(row));
        }

        return elements;
    }

    @Override
    public List<T> getAll(Map<String, Object> filter) throws SQLException, IllegalStateException {
        QueryBuilder queryBuilder = filteredSelect(filter);

        return getAll(queryBuilder);
    }

    @Override
    public T instanceOfT(Map<String, Object> values) {
        T instance = instantiator.get();

        values.forEach((key, val) -> {
            if (allExceptPrimaryFieldValueSetterMap.containsKey(key)) {
                allExceptPrimaryFieldValueSetterMap.get(key).accept(instance, val);
            }
        });

        return instance;
    }

    private QueryBuilder filteredSelect(Map<String, Object> filter) {
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
        return queryBuilder;
    }

    private T fullInstanceOfT(Map<String, Object> row) {
        T instance = instantiator.get();

        specificFieldValueSetters.forEach(setter -> setter.accept(instance, row));

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

    public void init(MySQLServiceFieldsProvider mySQLServiceFieldsProvider) {
        mySQLServiceFieldsProvider.validateClass(servingType);
        tableName = mySQLServiceFieldsProvider.getTableName(servingType);
        instantiator = mySQLServiceFieldsProvider.getInstantiator(servingType);
        nonPrimaryKeyFieldMySqlValues = mySQLServiceFieldsProvider.getNonPrimaryKeyFieldMySqlValues(servingType);
        primaryKeyFieldMySqlValue = mySQLServiceFieldsProvider.getPrimaryKeyFieldMySqlValue(servingType);
        primaryKeyFieldValueSetter = mySQLServiceFieldsProvider.getPrimaryKeyFieldValueSetter(servingType);
        specificFieldValueSetters = mySQLServiceFieldsProvider.getSpecificFieldValueSetters(servingType);

        columnsAndAliases = mySQLServiceFieldsProvider.getColumnsAndAliases(specificFieldValueSetters);

        fieldMySqlValueMap.put(primaryKeyFieldMySqlValue.getVariableName(), primaryKeyFieldMySqlValue);
        nonPrimaryKeyFieldMySqlValues.forEach(fieldMySqlValue -> fieldMySqlValueMap.put(fieldMySqlValue.getVariableName(), fieldMySqlValue));

        allExceptPrimaryFieldValueSetterMap = mySQLServiceFieldsProvider.getNonPrimaryFieldValueSetterMap(servingType);
    }

    ArrayList<Pair<String, String>> columnsAndAliases() {
        return columnsAndAliases;
    }
}
