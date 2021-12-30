package com.eu.at_it.pantheon.mysql.service;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;

/**
 * Function to set a value into an instance of an object
 */
class FieldValueSetter<T> implements BiConsumer<T, Object> {
    private final Field field;

    /**
     * @param field reflection of the variable that is to be used to set the value in the object
     */
    FieldValueSetter(Field field) {
        this.field = field;
    }

    @Override
    public void accept(T setOn, Object value) {
        try {
            field.set(setOn, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set value on field", e);
        }
    }

    Field getField() {
        return field;
    }
}
