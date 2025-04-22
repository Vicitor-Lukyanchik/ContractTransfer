package com.example.transfer.dbf.annotation;

import com.example.transfer.s02015.constant.DbCredentials;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbfSource {
    String value(); // Название dbf-файла

    String path() default DbCredentials.DEFAULT_DBF_SOURCE_PATH;

    boolean useLocalCache() default false;
}