package com.example.transfer.dbf.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

public interface FieldProcessor {


    boolean supports(Annotation annotation);

    Object process(Field field, Object entity, Connection connection) throws IllegalAccessException;

    default Object process(Field field, Object entity) throws IllegalAccessException {
        return process(field, entity, null);
    }
}