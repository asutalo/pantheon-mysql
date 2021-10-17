package com.eu.at_it.sql_wrapper.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InstantiatorTest extends FunctionsTestBase {
    @Test
    void get_shouldGenerateAnInstanceFromDefaultConstructor() throws NoSuchMethodException {
        FunctionsTestBase.TestClass actual = new Instantiator<>(getTestClassDefaultConstructor()).get();

        Assertions.assertNotNull(actual);
    }

    @Test
    void get_shouldThrowExceptionWhenInstantiationFails() {
        Assertions.assertThrows(RuntimeException.class, () -> new Instantiator<>(getDoomedToFailDefaultConstructor()).get());
    }
}