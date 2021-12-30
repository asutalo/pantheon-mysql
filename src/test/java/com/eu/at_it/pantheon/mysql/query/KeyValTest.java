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

class KeyValTest {
    private static final int SOME_INDEX = 1;
    private static final String SOME_VALUE = "SOME_VALUE";
    private static final String SOME_KEY = "SOME_KEY";
    private static final String SOME_SEPARATOR = ",";
    private static final MysqlType SOME_TYPE = VARCHAR;

    private final KeyVal keyVal = keyVal();

    @Test
    void applyToString() {
        String query = "where ";
        String expectedQuery = query + SOME_SEPARATOR + SOME_KEY + KeyVal.VAL;

        assertEquals(expectedQuery, keyVal.apply(query));
    }

    @Test
    void applyToPreparedStatement() throws SQLException {
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        keyVal.apply(mockPreparedStatement);

        verify(mockPreparedStatement).setObject(SOME_INDEX, SOME_VALUE, SOME_TYPE);
    }

    @Test
    void getValue() {
        assertEquals(SOME_VALUE, keyVal.getValue());
    }

    @Test
    void getValueType() {
        assertEquals(SOME_TYPE, keyVal.getValueType());
    }

    @Test
    void getKey() {
        assertEquals(SOME_KEY, keyVal.getKey());
    }

    @Test
    void equals() {
        KeyVal keyVal1 = keyVal();
        KeyVal keyVal2 = keyVal();

        Assertions.assertEquals(keyVal1, keyVal2);
    }

    @Test
    void hashcode() {
        KeyVal KeyVal = keyVal();

        Assertions.assertEquals(KeyVal.hashCode(), KeyVal.hashCode());
    }

    private KeyVal keyVal() {
        return new KeyVal(SOME_TYPE, SOME_KEY, SOME_VALUE, SOME_SEPARATOR, SOME_INDEX);
    }
}