package com.example.transfer.s02015.processor;

import com.example.transfer.dbf.exception.ProcessException;
import com.example.transfer.dbf.processor.FieldProcessor;
import com.example.transfer.s02015.annotation.DefaultValue;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;

@Component
public class DefaultValueProcessor implements FieldProcessor {

    @Override
    public boolean supports(Annotation annotation) {
        return annotation instanceof DefaultValue;
    }

    @Override
    public Object process(Field field, Object entity, Connection connection) throws IllegalAccessException {
        DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);
        if (defaultValue == null) {
            throw new ProcessException("Annotation @DefaultValue not found on field: " + field.getName());
        }

        field.setAccessible(true);

        Object currentValue = field.get(entity);

        if (currentValue != null) {
            return currentValue;
        }

        String defaultValueStr = defaultValue.value();

        Class<?> fieldType = field.getType();
        try {
            Object defaultValueParsed = parseDefaultValue(fieldType, defaultValueStr);
            field.set(entity, defaultValueParsed); // Устанавливаем значение по умолчанию
            return defaultValueParsed;
        } catch (Exception e) {
            throw new ProcessException("Failed to parse default value for field: " + field.getName(), e);
        }
    }

    /**
     * Преобразует строковое значение в соответствующий тип данных поля.
     */
    private Object parseDefaultValue(Class<?> fieldType, String value) {
        if (fieldType == Integer.class || fieldType == int.class) {
            return Integer.parseInt(value);
        } else if (fieldType == Double.class || fieldType == double.class) {
            return Double.parseDouble(value);
        } else if (fieldType == Float.class || fieldType == float.class) {
            return Float.parseFloat(value);
        } else if (fieldType == Long.class || fieldType == long.class) {
            return Long.parseLong(value);
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (fieldType == String.class) {
            return value;
        } else if (fieldType == BigDecimal.class) {
            return new BigDecimal(value);
        } else {
            throw new ProcessException("Unsupported field type: " + fieldType.getName());
        }
    }
}