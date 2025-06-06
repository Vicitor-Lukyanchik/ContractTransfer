package com.example.transfer.s02015.processor;

import com.example.transfer.dbf.annotation.ProcessingOrder;
import com.example.transfer.dbf.exception.ProcessException;
import com.example.transfer.dbf.processor.FieldProcessor;
import com.example.transfer.s02015.annotation.RoundTo;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;

@Component
@ProcessingOrder(1)
public class RoundingProcessor implements FieldProcessor {

    @Override
    public boolean supports(Annotation annotation) {
        return annotation instanceof RoundTo;
    }

    @Override
    public Object process(Field field, Object entity, Connection connection) throws IllegalAccessException {

        RoundTo roundTo = field.getAnnotation(RoundTo.class);
        if (roundTo == null) {
            throw new ProcessException("Annotation @RoundTo not found on field: " + field.getName());
        }

        field.setAccessible(true);
        Object value = field.get(entity);

        if (value instanceof Double || value instanceof Float) {
            double roundedValue = roundValue(((Number) value).doubleValue(), roundTo.value());
            BigDecimal formattedValue = new BigDecimal(roundedValue).setScale(roundTo.value(), RoundingMode.HALF_UP);
            return formattedValue.doubleValue();
        } else if (value != null) {
            throw new ProcessException("Field must be of type Double or Float: " + field.getName());
        }

        return null;
    }

    private double roundValue(double value, int scale) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}