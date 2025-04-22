package com.example.transfer.dbf.processor.impl;

import com.example.transfer.dbf.annotation.ProcessingOrder;
import com.example.transfer.dbf.processor.FieldProcessor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;

@Component
@ProcessingOrder(10)
public class ProcessorA implements FieldProcessor {
    @Override
    public boolean supports(Annotation annotation) {
        return false;
    }

    @Override
    public Object process(Field field, Object entity, Connection connection) throws IllegalAccessException {
        return null;
    }
}