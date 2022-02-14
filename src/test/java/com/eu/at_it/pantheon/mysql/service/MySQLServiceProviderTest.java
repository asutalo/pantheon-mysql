package com.eu.at_it.pantheon.mysql.service;

import com.eu.at_it.pantheon.mysql.client.MySqlClient;
import com.eu.at_it.pantheon.service.Service;
import com.google.inject.TypeLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MySQLServiceProviderTest {
    private static final Class<Object> SOME_CLASS = Object.class;
    private static final String SOME_TABLE = "someTable";
    private static final String SOME_VAR = "someVar";
    private static final String SOME_OTHER_VAR = "someOtherVar";
    private MySQLServiceProvider mySQLServiceProvider;
    @Mock
    private MySQLServiceFieldsProvider mockMySQLServiceFieldsProvider;

    @Mock
    private MySqlClient mockMySqlClient;

    @Mock
    private Instantiator<Object> mockInstantiator;

    @Mock
    private FieldValueSetter<Object> mockFieldValueSetter;

    @Mock
    private ResultSetFieldValueSetter<Object> mockResultSetFieldValueSetter;

    @Mock
    private FieldMySqlValue<Object> mockFieldMySqlValue;

    private LinkedList<ResultSetFieldValueSetter<Object>> someResultSetFieldValueSetters;

    @BeforeEach
    void setUp() {
        mySQLServiceProvider = new MySQLServiceProvider(mockMySqlClient);
        someResultSetFieldValueSetters = new LinkedList<>(List.of(mockResultSetFieldValueSetter, mockResultSetFieldValueSetter));
        MySQLServiceFieldsProvider.setInstance(mockMySQLServiceFieldsProvider);
    }

    @Test
    void provide_shouldReturnInstanceOfMySQLService() {
        when(mockMySQLServiceFieldsProvider.getTableName(SOME_CLASS)).thenReturn(SOME_TABLE);
        when(mockMySQLServiceFieldsProvider.getInstantiator(any())).thenReturn(mockInstantiator);
        when(mockMySQLServiceFieldsProvider.getPrimaryKeyFieldMySqlValue(any())).thenReturn(mockFieldMySqlValue);
        when(mockMySQLServiceFieldsProvider.getPrimaryKeyFieldValueSetter(any())).thenReturn(mockFieldValueSetter);
        when(mockMySQLServiceFieldsProvider.getNonPrimaryFieldValueSetterMap(any())).thenReturn(Map.of(SOME_VAR, mockFieldValueSetter));

        when(mockMySQLServiceFieldsProvider.getResultSetFieldValueSetters(any())).thenReturn(someResultSetFieldValueSetters);
        when(mockMySQLServiceFieldsProvider.getNonPrimaryKeyFieldMySqlValues(any())).thenReturn(new LinkedList<>(List.of(mockFieldMySqlValue, mockFieldMySqlValue)));
        when(mockFieldMySqlValue.getVariableName()).thenReturn(SOME_VAR).thenReturn(SOME_OTHER_VAR);

        Service service = mySQLServiceProvider.provide(TypeLiteral.get(Object.class));

        Assertions.assertNotNull(service);
        Assertions.assertTrue(service instanceof MySQLService);
    }

    @Test
    void providerFor_shouldReturnTypeLiteralOfMySQLService() {
        TypeLiteral<? extends Service> typeLiteral = mySQLServiceProvider.providerFor();

        Assertions.assertNotNull(typeLiteral);
        Assertions.assertEquals(TypeLiteral.get(MySQLService.class), typeLiteral);
    }
}
