package com.eu.at_it.pantheon.mysql.service;

import com.eu.at_it.pantheon.mysql.client.MySqlClient;
import com.eu.at_it.pantheon.mysql.query.MySqlValue;
import com.eu.at_it.pantheon.mysql.query.QueryBuilder;
import com.google.inject.TypeLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenericDataAccessServiceTest {
    private static final Class<Object> SOME_CLASS = Object.class;
    private static final String SOME_TABLE = "someTable";
    private static final String SOME_VAR = "someVar";
    private static final String SOME_OTHER_VAR = "someOtherVar";

    @Mock
    private GenericDataAccessServiceProvider mockGenericDataAccessServiceProvider;

    @Mock
    private MySqlClient mockMySqlClient;

    @Mock
    private ResultSet mockResultSet;

    @Mock
    private QueryBuilder mockQueryBuilder;

    @Mock
    private Instantiator<Object> mockInstantiator;

    @Mock
    private FieldValueSetter<Object> mockFieldValueSetter;

    @Mock
    private ResultSetFieldValueSetter<Object> mockResultSetFieldValueSetter;

    @Mock
    private MySqlValue mockMySqlValue;

    @Mock
    private FieldMySqlValue<Object> mockFieldMySqlValue;

    @Mock
    private Object mockObject;

    private LinkedList<ResultSetFieldValueSetter<Object>> someResultSetFieldValueSetters;

    @BeforeEach
    void setUp() {
        someResultSetFieldValueSetters = new LinkedList<>(List.of(mockResultSetFieldValueSetter, mockResultSetFieldValueSetter));
        GenericDataAccessServiceProvider.setInstance(mockGenericDataAccessServiceProvider);

        when(mockGenericDataAccessServiceProvider.getTableName(SOME_CLASS)).thenReturn(SOME_TABLE);
        when(mockGenericDataAccessServiceProvider.getInstantiator(any())).thenReturn(mockInstantiator);
        when(mockGenericDataAccessServiceProvider.getPrimaryKeyFieldMySqlValue(any())).thenReturn(mockFieldMySqlValue);
        when(mockGenericDataAccessServiceProvider.getPrimaryKeyFieldValueSetter(any())).thenReturn(mockFieldValueSetter);
        when(mockGenericDataAccessServiceProvider.getNonPrimaryFieldValueSetterMap(any())).thenReturn(Map.of(SOME_VAR, mockFieldValueSetter));

        when(mockGenericDataAccessServiceProvider.getResultSetFieldValueSetters(any())).thenReturn(someResultSetFieldValueSetters);
        when(mockGenericDataAccessServiceProvider.getNonPrimaryKeyFieldMySqlValues(any())).thenReturn(new LinkedList<>(List.of(mockFieldMySqlValue, mockFieldMySqlValue)));
        when(mockFieldMySqlValue.getVariableName()).thenReturn(SOME_VAR).thenReturn(SOME_OTHER_VAR);
    }

    @Test
    void shouldInitializeViaTheProvider() {
        GenericDataAccessService<Object> genericDataAccessService = genericDataAccessService();

        verify(mockGenericDataAccessServiceProvider).validateClass(SOME_CLASS);
        verify(mockGenericDataAccessServiceProvider).getNonPrimaryKeyFieldMySqlValues(SOME_CLASS);
        verify(mockGenericDataAccessServiceProvider).getResultSetFieldValueSetters(SOME_CLASS);
        verify(mockGenericDataAccessServiceProvider).getInstantiator(SOME_CLASS);
        verify(mockGenericDataAccessServiceProvider).getPrimaryKeyFieldValueSetter(SOME_CLASS);
        verify(mockGenericDataAccessServiceProvider).getPrimaryKeyFieldMySqlValue(SOME_CLASS);
        verify(mockGenericDataAccessServiceProvider).getTableName(SOME_CLASS);
        verify(mockGenericDataAccessServiceProvider).getNonPrimaryFieldValueSetterMap(SOME_CLASS);
        verifyNoMoreInteractions(mockGenericDataAccessServiceProvider);

        Map<String, FieldMySqlValue<Object>> fieldMySqlValueMap = genericDataAccessService.getFieldMySqlValueMap();
        Map<String, FieldMySqlValue<Object>> expectedFieldMySqlValueMap = Map.of(SOME_VAR, mockFieldMySqlValue, SOME_OTHER_VAR, mockFieldMySqlValue);
        Assertions.assertEquals(expectedFieldMySqlValueMap, fieldMySqlValueMap);
    }

    @DisplayName("Filtered selection query builder")
    @Nested
    class FilteredSelect {
        @Test
        void shouldReturnQueryBuilderFilteredByTableName() {
            QueryBuilder expectedQueryBuilder = new QueryBuilder();
            expectedQueryBuilder.select();
            expectedQueryBuilder.from(SOME_TABLE);

            Assertions.assertEquals(expectedQueryBuilder, genericDataAccessService().filteredSelect());
        }
    }

    @DisplayName("Get single element")
    @Nested
    class Get {
        @Test
        void shouldReturnInstantiatedAndPopulatedObject() throws SQLException {
            when(mockInstantiator.get()).thenReturn(mockObject);
            when(mockMySqlClient.prepAndExecuteSelectQuery(mockQueryBuilder)).thenReturn(mockResultSet);
            when(mockResultSet.first()).thenReturn(true);
            when(mockResultSet.isLast()).thenReturn(true);

            genericDataAccessService().get(mockQueryBuilder);

            verify(mockInstantiator).get();
            verify(mockResultSetFieldValueSetter, times(someResultSetFieldValueSetters.size())).accept(mockObject, mockResultSet);
        }

        @Test
        void shouldThrowExceptionWhenSelectReturnsNoElements() throws SQLException {
            when(mockMySqlClient.prepAndExecuteSelectQuery(mockQueryBuilder)).thenReturn(mockResultSet);
            when(mockResultSet.first()).thenReturn(false);

            Assertions.assertThrows(IllegalStateException.class, () -> genericDataAccessService().get(mockQueryBuilder));

            verifyNoInteractions(mockInstantiator);
            verifyNoInteractions(mockResultSetFieldValueSetter);
        }

        @Test
        void shouldThrowExceptionWhenSelectReturnsMoreThanOneElement() throws SQLException {
            when(mockMySqlClient.prepAndExecuteSelectQuery(mockQueryBuilder)).thenReturn(mockResultSet);
            when(mockResultSet.first()).thenReturn(true);
            when(mockResultSet.isLast()).thenReturn(false);

            Assertions.assertThrows(IllegalStateException.class, () -> genericDataAccessService().get(mockQueryBuilder));

            verifyNoInteractions(mockInstantiator);
            verifyNoInteractions(mockResultSetFieldValueSetter);
        }
    }

    private GenericDataAccessService<Object> genericDataAccessService() {
        return new GenericDataAccessService<>(mockMySqlClient, TypeLiteral.get(SOME_CLASS));
    }

    @DisplayName("Get one or all elements using a filter")
    @Nested
    class FilteredGet {
        @Test
        void get_shouldUseProvidedFilterToMapToMySqlValuesAndIgnoreOnesThatDoNotMap() throws SQLException {
            GenericDataAccessService<Object> spy = spy(genericDataAccessService());
            int intVal = 1;
            String stringVal = "2";
            Map<String, Object> filter = Map.of(SOME_VAR, intVal, SOME_OTHER_VAR, stringVal, "someNotExisting", 1);

            when(mockFieldMySqlValue.of(intVal)).thenReturn(mockMySqlValue);
            when(mockFieldMySqlValue.of(stringVal)).thenReturn(mockMySqlValue);
            doReturn(mockObject).when(spy).get(any(QueryBuilder.class));

            QueryBuilder expected = spy.filteredSelect();
            expected.where();
            expected.keyIsVal(mockMySqlValue);
            expected.and();
            expected.keyIsVal(mockMySqlValue);

            spy.get(filter);

            verify(spy).get(expected);
        }

        @Test
        void get_shouldThrowExceptionWhenAllFiltersDoNotMatchMySqlValues() {
            Map<String, Object> filter = Map.of("someNotExisting", 1);

            Assertions.assertThrows(IllegalStateException.class, () -> genericDataAccessService().get(filter));
        }

        @Test
        void getAll_shouldUseProvidedFilterToMapToMySqlValuesAndIgnoreOnesThatDoNotMap() throws SQLException {
            GenericDataAccessService<Object> spy = spy(genericDataAccessService());
            int intVal = 1;
            String stringVal = "2";
            Map<String, Object> filter = Map.of(SOME_VAR, intVal, SOME_OTHER_VAR, stringVal, "someNotExisting", 1);

            when(mockFieldMySqlValue.of(intVal)).thenReturn(mockMySqlValue);
            when(mockFieldMySqlValue.of(stringVal)).thenReturn(mockMySqlValue);
            doReturn(List.of(mockObject)).when(spy).getAll(any(QueryBuilder.class));

            QueryBuilder expected = spy.filteredSelect();
            expected.where();
            expected.keyIsVal(mockMySqlValue);
            expected.and();
            expected.keyIsVal(mockMySqlValue);

            spy.getAll(filter);

            verify(spy).getAll(expected);
        }

        @Test
        void getAll_shouldThrowExceptionWhenAllFiltersDoNotMatchMySqlValues() {
            Map<String, Object> filter = Map.of("someNotExisting", 1);

            Assertions.assertThrows(IllegalStateException.class, () -> genericDataAccessService().getAll(filter));
        }
    }

    @DisplayName("Get all elements")
    @Nested
    class GetAll {
        @Test
        void shouldUseFilteredSelectToFetchAllElements() throws SQLException {
            int expectedNumberOfElements = 2;
            int expectedNumberOfSetterOperations = expectedNumberOfElements * someResultSetFieldValueSetters.size();

            when(mockInstantiator.get()).thenReturn(mockObject).thenReturn(mockObject);
            when(mockMySqlClient.prepAndExecuteSelectQuery(any())).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

            GenericDataAccessService<Object> genericDataAccessService = genericDataAccessService();

            genericDataAccessService.getAll();

            verify(mockMySqlClient).prepAndExecuteSelectQuery(genericDataAccessService.filteredSelect());
            verify(mockInstantiator, times(expectedNumberOfElements)).get();
            verify(mockResultSetFieldValueSetter, times(expectedNumberOfSetterOperations)).accept(mockObject, mockResultSet);
        }

        @Test
        void shouldFetchAllElements() throws SQLException {
            int expectedNumberOfElements = 2;
            int expectedNumberOfSetterOperations = expectedNumberOfElements * someResultSetFieldValueSetters.size();

            when(mockInstantiator.get()).thenReturn(mockObject).thenReturn(mockObject);
            when(mockMySqlClient.prepAndExecuteSelectQuery(mockQueryBuilder)).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

            genericDataAccessService().getAll(mockQueryBuilder);

            verify(mockMySqlClient).prepAndExecuteSelectQuery(mockQueryBuilder);
            verify(mockInstantiator, times(expectedNumberOfElements)).get();
            verify(mockResultSetFieldValueSetter, times(expectedNumberOfSetterOperations)).accept(mockObject, mockResultSet);
        }

    }

    @DisplayName("Delete an element")
    @Nested
    class Delete {
        @Test
        void shouldDeleteSpecifiedElement() throws SQLException {
            when(mockFieldMySqlValue.apply(mockObject)).thenReturn(mockMySqlValue);
            when(mockMySqlClient.prepAndExecuteOtherDmlQuery(any())).thenReturn(1);

            QueryBuilder expectedQueryBuilder = new QueryBuilder();
            expectedQueryBuilder.delete();
            expectedQueryBuilder.from(SOME_TABLE);
            expectedQueryBuilder.where();
            expectedQueryBuilder.keyIsVal(mockMySqlValue);

            genericDataAccessService().delete(mockObject);

            verify(mockMySqlClient).prepAndExecuteOtherDmlQuery(expectedQueryBuilder);
        }

        @Test
        void shouldThrowExceptionWhenNothingIsDeleted() throws SQLException {
            when(mockFieldMySqlValue.apply(mockObject)).thenReturn(mockMySqlValue);
            when(mockMySqlClient.prepAndExecuteOtherDmlQuery(any())).thenReturn(0);

            Assertions.assertThrows(RuntimeException.class, () -> genericDataAccessService().delete(mockObject));
        }

    }

    @DisplayName("Save an element")
    @Nested
    class Save {
        @Test
        void shouldSaveAnElement() throws SQLException {
            int someInsertId = 1;
            when(mockMySqlClient.prepAndExecuteInsertQuery(any())).thenReturn(someInsertId);
            when(mockFieldMySqlValue.apply(mockObject)).thenReturn(mockMySqlValue).thenReturn(mockMySqlValue);

            LinkedList<MySqlValue> expectedMySqlValues = new LinkedList<>(List.of(mockMySqlValue, mockMySqlValue));
            QueryBuilder expectedQueryBuilder = new QueryBuilder();
            expectedQueryBuilder.insert(SOME_TABLE, expectedMySqlValues);

            Assertions.assertNotNull(genericDataAccessService().save(mockObject));
            verify(mockMySqlClient).prepAndExecuteInsertQuery(expectedQueryBuilder);
        }
    }

    @DisplayName("Update an element")
    @Nested
    class Update {
        @Test
        void shouldUpdateAnElement() throws SQLException {
            when(mockFieldMySqlValue.apply(mockObject)).thenReturn(mockMySqlValue).thenReturn(mockMySqlValue).thenReturn(mockMySqlValue);
            LinkedList<MySqlValue> expectedMySqlValues = new LinkedList<>(List.of(mockMySqlValue, mockMySqlValue));
            QueryBuilder expectedQueryBuilder = new QueryBuilder();
            expectedQueryBuilder.update(SOME_TABLE, expectedMySqlValues);
            expectedQueryBuilder.where();
            expectedQueryBuilder.keyIsVal(mockMySqlValue);

            when(mockMySqlClient.prepAndExecuteOtherDmlQuery(any())).thenReturn(1);

            Assertions.assertNotNull(genericDataAccessService().update(mockObject));
            verify(mockMySqlClient).prepAndExecuteOtherDmlQuery(expectedQueryBuilder);
        }

        @Test
        void shouldThrowExceptionWhenNoElementUpdated() throws SQLException {
            when(mockFieldMySqlValue.apply(mockObject)).thenReturn(mockMySqlValue).thenReturn(mockMySqlValue).thenReturn(mockMySqlValue);
            when(mockMySqlClient.prepAndExecuteOtherDmlQuery(any())).thenReturn(0);

            Assertions.assertThrows(RuntimeException.class, () -> genericDataAccessService().update(mockObject));
        }

    }

    @DisplayName("Instantiation of an element")
    @Nested
    class Instantiate {
        @Test
        void shouldInstantiateAnObjectWithMappedValuesAndIgnoringUnmappedValues() {
            when(mockInstantiator.get()).thenReturn(mockObject);
            int expectedValue = 1;
            genericDataAccessService().instanceOfT(Map.of(SOME_VAR, expectedValue, "unmapped", "2"));

            verify(mockFieldValueSetter).accept(mockObject, expectedValue);
            verifyNoMoreInteractions(mockFieldValueSetter);
        }
    }
}