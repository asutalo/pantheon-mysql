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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MySQLServiceProviderTest {
    private MySQLServiceProvider mySQLServiceProvider;
    @Mock
    private MySQLService mockMySQLService;

    @Mock
    private MySqlClient mockMySqlClient;

    @BeforeEach
    void setUp() {
        mySQLServiceProvider = new MySQLServiceProvider(mockMySqlClient);
    }

    @Test
    void provide_shouldReturnInitialisedInstanceOfMySQLService() {
        MySQLServiceProvider spy = spy(mySQLServiceProvider);
        TypeLiteral<Object> servingType = TypeLiteral.get(Object.class);

        doReturn(mockMySQLService).when(spy).mySQLService(servingType);

        Service service = spy.provide(servingType);

        Assertions.assertNotNull(service);
        verify(mockMySQLService).init(any());
    }

    @Test
    void mySQLService_shouldReturnInstanceOfMySQLService() {
        Service service = mySQLServiceProvider.mySQLService(TypeLiteral.get(Object.class));
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
