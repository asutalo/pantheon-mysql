package com.eu.at_it.sql_wrapper.client;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Connector {
    static final String USER_PROPERTY = "user";
    static final String PASSWORD_PROPERTY = "password";
    private final Driver jdbcDriver;
    private final String jdbcRootUrl;
    private final List<String> dbParams;

    public Connector(Driver jdbcDriver, String jdbcRootUrl, LinkedList<String> dbParams) {
        this.jdbcDriver = jdbcDriver;
        this.jdbcRootUrl = jdbcRootUrl;
        this.dbParams = dbParams;
    }

    public Connection connect() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(USER_PROPERTY, dbParams.get(1));
        properties.setProperty(PASSWORD_PROPERTY, dbParams.get(2));

        return jdbcDriver.connect(
                jdbcRootUrl + dbParams.get(0), properties);
    }

    public void close(Connection connection) throws SQLException {
        connection.close();
    }
}
