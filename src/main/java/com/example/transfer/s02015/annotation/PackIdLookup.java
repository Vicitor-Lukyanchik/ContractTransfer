package com.example.transfer.s02015.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PackIdLookup {
    String username() default "s02015"; // Логин для подключения к БД
    String password() default "s02015"; // Пароль для подключения к БД
}