package com.example.transfer.s02015.annotation;

import com.example.transfer.s02015.constant.DbCredentials;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PackIdLookup {
    String username() default DbCredentials.DEFAULT_USERNAME; // Логин для подключения к БД
    String password() default DbCredentials.DEFAULT_PASSWORD; // Пароль для подключения к БД
}