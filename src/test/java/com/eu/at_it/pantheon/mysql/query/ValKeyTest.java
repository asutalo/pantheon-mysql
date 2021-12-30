package com.eu.at_it.pantheon.mysql.query;

import com.mysql.cj.MysqlType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.mysql.cj.MysqlType.VARCHAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ValKeyTest {
    private static final int SOME_INDEX = 1;
    private static final String SOME_VALUE = "SOME_VALUE";
    private static final String SOME_KEY = "SOME_KEY";
    private static final String SOME_SEPARATOR = ",";
    private static final MysqlType SOME_TYPE = VARCHAR;

    private final ValKey valKey = valKey();

    @Test
    void applyToString() {
        String query = "where ";
        String expectedQuery = query + SOME_SEPARATOR + ValKey.VAL + ValKey.AS + SOME_KEY;

        assertEquals(expectedQuery, valKey.apply(query));
    }

    @Test
    void applyToPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        valKey.apply(mockPreparedStatement);

        verify(mockPreparedStatement).setObject(SOME_INDEX, SOME_VALUE, SOME_TYPE);
    }

    @Test
    void getValue() {
        assertEquals(SOME_KEY, valKey.getKey());
    }

    @Test
    void getValueType() {
        assertEquals(SOME_TYPE, valKey.getValueType());
    }


    @Test
    void equals() {
        ValKey valKey1 = valKey();
        ValKey valKey2 = valKey();

        Assertions.assertEquals(valKey1, valKey2);
    }

    @Test
    void hashcode() {
        ValKey valKey = valKey();

        Assertions.assertEquals(valKey.hashCode(), valKey.hashCode());
    }

    private ValKey valKey() {
        return new ValKey(SOME_TYPE, SOME_VALUE, SOME_KEY, SOME_SEPARATOR, SOME_INDEX);
    }
}