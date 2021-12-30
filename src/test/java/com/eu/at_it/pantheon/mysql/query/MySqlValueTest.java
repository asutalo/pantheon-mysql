package com.eu.at_it.pantheon.mysql.query;

import com.mysql.cj.MysqlType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MySqlValueTest {
    private static final MysqlType SOME_TYPE = MysqlType.SMALLINT;
    private static final String SOME_KEY = "SOME_KEY";
    private static final int SOME_VALUE = 1;

    @Test
    void equals() {
        MySqlValue mySqlValue1 = mySqlValue();
        MySqlValue mySqlValue2 = mySqlValue();

        Assertions.assertEquals(mySqlValue1, mySqlValue2);
    }

    @Test
    void hashcode() {
        MySqlValue mySqlValue1 = mySqlValue();

        Assertions.assertEquals(mySqlValue1.hashCode(), mySqlValue1.hashCode());
    }

    private MySqlValue mySqlValue() {
        return new MySqlValue(SOME_TYPE, SOME_KEY, SOME_VALUE);
    }
}