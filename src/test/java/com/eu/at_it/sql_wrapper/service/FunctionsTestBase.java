package com.eu.at_it.sql_wrapper.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class FunctionsTestBase {
    static final int START_VALUE = 1;

    Field getField() {
        Field testField = FieldValueSetterTest.TestClass.class.getDeclaredFields()[0];
        testField.setAccessible(true);
        return testField;
    }

    Constructor<TestClass> getTestClassDefaultConstructor() throws NoSuchMethodException {
        Constructor<TestClass> declaredConstructor = TestClass.class.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        return declaredConstructor;
    }

    Constructor<DoomedToFailTestClass> getDoomedToFailDefaultConstructor() throws NoSuchMethodException {
        return DoomedToFailTestClass.class.getDeclaredConstructor();
    }

    static class TestClass {
        private int val = START_VALUE;

        TestClass() {
        }

        TestClass(String s) {
            //ignore
        }

        int getVal() {
            return val;
        }

        //to prevent intellij from adding "final" on the var...
        void setVal() {
            val = 0;
        }
    }

    static class DoomedToFailTestClass extends TestClass {
        DoomedToFailTestClass() {
            throw new RuntimeException();
        }
    }
}
