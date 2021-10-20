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
    private static final String COLUMN_NAME = "column";

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
        Assertions.assertTrue(actualFieldMySqlValues.stream().noneMatch(testTargetFieldMySqlValue -> testTargetFieldMySqlValue.getFieldName().equals(PRIMARY_KEY_FIELD_IN_TEST_TARGET)));
        Assertions.assertTrue(actualFieldMySqlValues.stream().anyMatch(testTargetFieldMySqlValue -> testTargetFieldMySqlValue.getFieldName().equals(COLUMN_NAME)));
        Assertions.assertTrue(actualFieldMySqlValues.stream().anyMatch(testTargetFieldMySqlValue -> !testTargetFieldMySqlValue.getFieldName().equals(COLUMN_NAME)));
    }

    @Test
    void getPrimaryKeyFieldMySqlValue_shouldProvidePrimaryKeyMySqlValue() {
        Assertions.assertEquals(PRIMARY_KEY_FIELD_IN_TEST_TARGET, genericDataAccessServiceProvider.getPrimaryKeyFieldMySqlValue(TestTarget.class).getFieldName());
    }

    @Test
    void getPrimaryKeyFieldMySqlValue_shouldProvidePrimaryKeyMySqlValueWithProvidedColumnName() {
        Assertions.assertEquals(COLUMN_NAME, genericDataAccessServiceProvider.getPrimaryKeyFieldMySqlValue(TestTargetNoEmptyConstructor.class).getFieldName());
    }

    @Test
    void getPrimaryKeyFieldMySqlValue_shouldThrowExceptionWhenNoPrimaryKeyFound() {
        Assertions.assertThrows(RuntimeException.class, () -> genericDataAccessServiceProvider.getPrimaryKeyFieldMySqlValue(NoneArePrimary.class));
    }

    @Test
    void getPrimaryKeyFieldValueSetter_shouldProvideFieldValueSetterForPrimaryKey() {
        Assertions.assertEquals(PRIMARY_KEY_FIELD_IN_TEST_TARGET, genericDataAccessServiceProvider.getPrimaryKeyFieldValueSetter(TestTarget.class).getField().getName());
    }

    @Test
    void getPrimaryKeyFieldValueSetter_shouldThrowExceptionWhenNoPrimaryKeyFound() {
        Assertions.assertThrows(RuntimeException.class, () -> genericDataAccessServiceProvider.getPrimaryKeyFieldValueSetter(NoneArePrimary.class));
    }

    @Test
    void getResultSetFieldValueSetters_shouldProvideFieldValueSettersForAllAnnotatedFields() {
        int fieldsInTestTarget = 3;

        LinkedList<ResultSetFieldValueSetter<TestTarget>> resultSetFieldValueSetters = genericDataAccessServiceProvider.getResultSetFieldValueSetters(TestTarget.class);
        Assertions.assertEquals(fieldsInTestTarget, resultSetFieldValueSetters.size());

        Assertions.assertFalse(resultSetFieldValueSetters.stream().anyMatch(setter -> setter.getFieldName().equals("notAnnotated")));
        Assertions.assertTrue(resultSetFieldValueSetters.stream().anyMatch(setter -> setter.getFieldName().equals(COLUMN_NAME)));
        Assertions.assertTrue(resultSetFieldValueSetters.stream().anyMatch(setter -> !setter.getFieldName().equals(COLUMN_NAME)));
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
        @MySqlField(type = MysqlType.INT, column = COLUMN_NAME)
        private final int intField = 1;
        @MySqlField(type = MysqlType.VARCHAR)
        private String otherStringField = "other";
        private String notAnnotated = "other";

        private TestTarget() {
        }

        TestTarget(String otherStringField) {
            this.otherStringField = otherStringField;
            notAnnotated = "";
        }
    }

    static class TestTargetNoEmptyConstructor {
        @MySqlField(type = MysqlType.VARCHAR, primary = true, column = COLUMN_NAME)
        private final int intField = 1;

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