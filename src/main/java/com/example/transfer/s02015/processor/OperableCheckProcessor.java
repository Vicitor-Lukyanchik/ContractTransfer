package com.example.transfer.s02015.processor;

import com.example.transfer.dbf.annotation.ProcessingOrder;
import com.example.transfer.dbf.processor.FieldProcessor;
import com.example.transfer.s02015.annotation.OperableCheck;

import javax.persistence.*;

import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
@ProcessingOrder(1000)
public class OperableCheckProcessor implements FieldProcessor {

    @Override
    public boolean supports(Annotation annotation) {
        return annotation instanceof OperableCheck;
    }

    @Override
    public Object process(Field field, Object entity, Connection connection) throws IllegalAccessException {
        if (connection == null) {
            throw new IllegalArgumentException("Connection required for this processor.");
        }

        try {
            OperableCheck annotation = field.getAnnotation(OperableCheck.class);
            if (annotation == null) {
                throw new IllegalArgumentException("Annotation @OperableCheck not found on field: " + field.getName());
            }

            String lookupFieldName = annotation.lookupField();
            Object lookupFieldValue = getFieldValue(entity, lookupFieldName);

            if (lookupFieldValue == null) {
                return 1;
            }

            String columnName = entity.getClass().getDeclaredField(lookupFieldName).getAnnotation(Column.class).name();
            String tableName = getTableName(entity.getClass());

            String sql = String.format("SELECT * FROM %s WHERE %s = ?", tableName, columnName);
            Object oracleEntity = fetchFromOracle(connection, sql, lookupFieldValue, entity.getClass());

            if (oracleEntity == null) {
                return 1;
            }

            return compareFields(entity, oracleEntity) ? 1 : 0;
        } catch (Exception e) {
            System.err.println("Ошибка при обработке аннотации @OperableCheck: " + e.getMessage());
            return 1;
        }
    }

    private Object getFieldValue(Object entity, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = entity.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(entity);
    }

    private String getTableName(Class<?> clazz) {
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        if (tableAnnotation != null && !tableAnnotation.name().isEmpty()) {
            return tableAnnotation.name();
        }
        throw new IllegalArgumentException("Название таблицы не указано в аннотации @Table для класса: " + clazz.getName());
    }

    private Object fetchFromOracle(Connection connection, String sql, Object parameter, Class<?> clazz) throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, parameter);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            return populateEntity(clazz, resultSet);
        }
    }

    private Object populateEntity(Class<?> clazz, ResultSet resultSet) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
        Object entity = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            String columnName = field.getAnnotation(Column.class).name().toUpperCase();
            Object value = resultSet.getObject(columnName);

            if (value == null) {
                field.set(entity, null);
            } else {
                value = convertValueToFieldType(value, field.getType());
                field.set(entity, value);
            }
        }
        return entity;
    }

    private Object convertValueToFieldType(Object value, Class<?> fieldType) {
        if (fieldType == Long.class || fieldType == long.class) {
            return ((BigDecimal) value).longValue(); // Преобразуем BigDecimal в Long
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return ((BigDecimal) value).intValue(); // Преобразуем BigDecimal в Integer
        } else if (fieldType == Double.class || fieldType == double.class) {
            return ((BigDecimal) value).doubleValue(); // Преобразуем BigDecimal в Double
        } else if (fieldType == LocalDate.class && value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate(); // Преобразуем SQL Date в LocalDate
        } else if (fieldType == java.sql.Date.class && value instanceof java.sql.Timestamp) {
            return new java.sql.Date(((java.sql.Timestamp) value).getTime()); // Преобразуем Timestamp в Date
        } else if (fieldType == LocalDateTime.class && value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime(); // Преобразуем Timestamp в LocalDateTime
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return ((BigDecimal) value).intValue() != 0; // Преобразуем NUMBER(1) в Boolean
        } else if (fieldType == String.class) {
            return value.toString(); // Преобразуем всё в String
        } else {
            return value; // Для остальных типов оставляем значение без изменений
        }
    }

    private boolean compareFields(Object currentEntity, Object oracleEntity) throws IllegalAccessException, NoSuchFieldException {
        Class<?> clazz = currentEntity.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.getAnnotation(OperableCheck.class) == null) {

                field.setAccessible(true);
                Object currentValue = field.get(currentEntity);

                Field oracleField = oracleEntity.getClass().getDeclaredField(field.getName());
                oracleField.setAccessible(true);
                Object oracleValue = oracleField.get(oracleEntity);

                if (!Objects.equals(currentValue, oracleValue)) {
                    return true;
                }
            }
        }

        return false;
    }
}
