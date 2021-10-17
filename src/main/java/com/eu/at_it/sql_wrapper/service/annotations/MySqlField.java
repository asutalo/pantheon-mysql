package com.eu.at_it.sql_wrapper.service.annotations;

import com.mysql.cj.MysqlType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MySqlField {
    MysqlType type();

    String column() default "";

    boolean primary() default false;
}
