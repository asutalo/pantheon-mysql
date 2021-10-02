package com.eu.at_it.sql_wrapper.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static com.eu.at_it.sql_wrapper.client.Connector.PASSWORD_PROPERTY;
import static com.eu.at_it.sql_wrapper.client.Connector.USER_PROPERTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConnectorTest {
    private static final String SOME_ROOT_URL = "SOME_ROOT_URL";
    private static final String SOME_DB_NAME = "SOME_DB_NAME";
    private static final String SOME_USER = "SOME_USER";
    private static final String SOME_PASSWORD = "SOME_PASS";
    private static final LinkedList<String> DB_PARAMS = new LinkedList<>(List.of(SOME_DB_NAME, SOME_USER, SOME_PASSWORD));

    @Captor
    ArgumentCaptor<Properties> propertiesArgumentCaptor;

    @Mock
    private Driver mockDriver;

    @Mock
    private Connection mockConnection;

    private Connector connector;

    @BeforeEach
    void setUp() {
        connector = new Connector(mockDriver, SOME_ROOT_URL, DB_PARAMS);
    }

    @Test
    void connect() throws SQLException {
        when(mockDriver.connect(anyString(), any(Properties.class))).thenReturn(mockConnection);

        Connection actualConnection = connector.connect();

        assertEquals(mockConnection, actualConnection);

        verify(mockDriver).connect(eq(SOME_ROOT_URL + SOME_DB_NAME), propertiesArgumentCaptor.capture());
        Properties actualProperties = propertiesArgumentCaptor.getValue();

        assertEquals(SOME_USER, actualProperties.getProperty(USER_PROPERTY));
        assertEquals(SOME_PASSWORD, actualProperties.getProperty(PASSWORD_PROPERTY));
    }

    @Test
    void close() throws SQLException {
        connector.close(mockConnection);

        verify(mockConnection).close();
    }
}