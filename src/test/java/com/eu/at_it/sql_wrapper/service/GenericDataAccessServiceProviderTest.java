package com.eu.at_it.sql_wrapper.service;

import com.eu.at_it.sql_wrapper.service.annotations.MySqlField;
import com.mysql.cj.MysqlType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static com.eu.at_it.sql_wrapper.service.GenericDataAccessServiceProvider.FAILED_TO_LOCATE_AN_EMPTY_CONSTRUCTOR;
import static com.eu.at_it.sql_wrapper.service.GenericDataAccessServiceProvider.NO_PRIMARY_KEY_FOUND;
import static com.eu.at_it.sql_wrapper.service.GenericDataAccessServiceProvider.THERE_CAN_BE_ONLY_ONE_PRIMARY_KEY;

class GenericDataAccessServiceProviderTest {
    private static final String PRIMARY_KEY_FIELD_IN_TEST_TARGET = "stringField";

    private final GenericDataAccessServiceProvider genericDataAccessServiceProvider = GenericDataAccessServiceProvider.getInstance();

    @Test
    void getTableName_shouldReturnSimpleClassName() {
        Assertions.assertEquals(TestTarget.class.getSimpleName(), genericDataAccessServiceProvider.getTableName(TestTarget.class));
    }

    @Test
    void getInstantiator_shouldCreateInstantiatorFromEmptyConstructor() throws NoSuchMethodException {
        Assertions.assertEquals(TestTarget.class.getDeclaredConstructor(), genericDataAccessServiceProvider.getInstantiator(TestTarget.class).getDeclaredConstructor());
    }

    @Test
    void getInstantiator_shouldThrowExceptionWhenEmptyConstructorNotFound() {
        Assertions.assertThrows(RuntimeException.class, () -> genericDataAccessServiceProvider.getInstantiator(TestTargetNoEmptyConstructor.class));
    }

    @Test
    void getFieldMySqlValues_shouldProvideAllValuesExceptPrimaryKey() {
        int nonPrimaryFieldsInTestTarget = 2;
        LinkedList<FieldMySqlValue<TestTarget>> actualFieldMySqlValues = genericDataAccessServiceProvider.getFieldMySqlValues(TestTarget.class);

        Assertions.assertEquals(nonPrimaryFieldsInTestTarget, actualFieldMySqlValues.size());
        actualFieldMySqlValues.forEach(fieldValue -> Assertions.assertNotEquals(PRIMARY_KEY_FIELD_IN_TEST_TARGET, fieldValue.getFieldName()));
    }

    @Test
    void getPrimaryKeyFieldMySqlValue_shouldProvidePrimaryKeyMySqlValue() {
        Assertions.assertEquals(PRIMARY_KEY_FIELD_IN_TEST_TARGET, genericDataAccessServiceProvider.getPrimaryKeyFieldMySqlValue(TestTarget.class).getFieldName());
    }

    @Test
    void getPrimaryKeyFieldMySqlValue_shouldThrowExceptionWhenNoPrimaryKeyFound() {
        Assertions.assertThrows(RuntimeException.class, () -> genericDataAccessServiceProvider.getPrimaryKeyFieldMySqlValue(TestTargetNoEmptyConstructor.class));
    }

    @Test
    void getPrimaryKeyFieldValueSetter_shouldProvideFieldValueSetterForPrimaryKey() {
        Assertions.assertEquals(PRIMARY_KEY_FIELD_IN_TEST_TARGET, genericDataAccessServiceProvider.getPrimaryKeyFieldValueSetter(TestTarget.class).getField().getName());
    }

    @Test
    void getPrimaryKeyFieldValueSetter_shouldThrowExceptionWhenNoPrimaryKeyFound() {
        Assertions.assertThrows(RuntimeException.class, () -> genericDataAccessServiceProvider.getPrimaryKeyFieldValueSetter(TestTargetNoEmptyConstructor.class));
    }

    @Test
    void getResultSetFieldValueSetters_shouldProvideFieldValueSettersForAllFields() {
        int fieldsInTestTarget = 3;

        Assertions.assertEquals(fieldsInTestTarget, genericDataAccessServiceProvider.getResultSetFieldValueSetters(TestTarget.class).size());
    }

    @Test
    void validateClass_shouldThrowExceptionWhenNoDefaultConstructor() {
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> genericDataAccessServiceProvider.validateClass(TestTargetNoEmptyConstructor.class));
        Assertions.assertEquals(FAILED_TO_LOCATE_AN_EMPTY_CONSTRUCTOR, runtimeException.getMessage());
    }

    @Test
    void validateClass_shouldThrowExceptionWhenNoPrimaryKeyFound() {
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> genericDataAccessServiceProvider.validateClass(NoneArePrimary.class));
        Assertions.assertEquals(NO_PRIMARY_KEY_FOUND, runtimeException.getMessage());
    }

    @Test
    void validateClass_shouldThrowExceptionWhenMultiplePrimaryKeyFound() {
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> genericDataAccessServiceProvider.validateClass(MultiPrimary.class));
        Assertions.assertEquals(THERE_CAN_BE_ONLY_ONE_PRIMARY_KEY, runtimeException.getMessage());
    }

    static class TestTarget {
        @MySqlField(type = MysqlType.VARCHAR, primary = true)
        private final String stringField = "str";
        @MySqlField(type = MysqlType.INT)
        private final int intField = 1;
        @MySqlField(type = MysqlType.VARCHAR)
        private String otherStringField = "other";

        private TestTarget() {
        }

        TestTarget(String otherStringField) {
            this.otherStringField = otherStringField;
        }
    }

    static class TestTargetNoEmptyConstructor {
        TestTargetNoEmptyConstructor(String otherStringField) {
        }
    }

    static class MultiPrimary {
        @MySqlField(type = MysqlType.VARCHAR, primary = true)
        private final int intField = 1;

        @MySqlField(type = MysqlType.VARCHAR, primary = true)
        private final int intField2 = 1;
    }

    static class NoneArePrimary {
        @MySqlField(type = MysqlType.VARCHAR)
        private final int intField = 1;
    }
}