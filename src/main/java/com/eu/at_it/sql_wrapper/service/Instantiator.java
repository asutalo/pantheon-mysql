package com.eu.at_it.sql_wrapper.service;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

/**
 * Function to produce an instance of the supplied constructor
 **/
class Instantiator<T> implements Supplier<T> {
    private final Constructor<T> declaredConstructor;

    /**
     * @param declaredConstructor - the default empty constructor to be used to provide an instance
     */
    Instantiator(Constructor<T> declaredConstructor) {
        this.declaredConstructor = declaredConstructor;
    }

    Constructor<T> getDeclaredConstructor() {
        return declaredConstructor;
    }

    @Override
    public T get() {
        try {
            return declaredConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Initialization failed", e);
        }
    }
}
