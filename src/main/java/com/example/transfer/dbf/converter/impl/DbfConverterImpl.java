package com.example.transfer.dbf.converter.impl;

import com.example.transfer.dbf.annotation.CharToNumeric;
import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;
import com.example.transfer.dbf.annotation.NullChanger;
import com.example.transfer.dbf.converter.DbfConverter;
import com.example.transfer.dbf.domain.DbfColumn;
import com.example.transfer.dbf.domain.DbfTable;
import com.example.transfer.dbf.exception.DbfConverterException;
import com.example.transfer.dbf.extractor.DbfDataExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DbfConverterImpl implements DbfConverter {

    private static final String DBF_TYPE_CHAR = "CHAR";
    private static final String DBF_TYPE_LOGICAL = "LOGICAL";
    private static final String DBF_TYPE_NUMERIC = "NUMERIC";
    private static final String DBF_TYPE_DATE = "DATE";

    private final DbfDataExtractor dbfDataExtractor;

    @Value("${local.cache.path}")
    private String localCachePath;

    @Value("${dbf.default.source.path}")
    private String dbfDefaultSourcePath;

    @Override
    public <T> List<T> convert(Class<T> entityClass) {
        if (!entityClass.isAnnotationPresent(DbfSource.class)) {
            throw new DbfConverterException("Класс не содержит аннотацию @DbfSource");
        }

        DbfTable dbfTable = getDbfTable(entityClass);
        List<Map<String, Object>> dbfRows = dbfTable.getRows(); // Извлекаем строки из DbfTable
        List<DbfColumn> dbfColumns = dbfTable.getColumns(); // Извлекаем метаданные столбцов

        // Преобразуем данные в экземпляры сущности
        List<T> entities = new ArrayList<>();
        for (Map<String, Object> dbfRow : dbfRows) {
            try {
                entities.add(createEntityFromDbf(entityClass, dbfRow, dbfColumns));
            } catch (DbfConverterException e) {
                throw new DbfConverterException(e.getMessage(), e);
            } catch (Exception e) {
                throw new DbfConverterException("Ошибка при преобразовании строки данных: " + e.getMessage(), e);
            }
        }

        return entities;
    }

    private <T> DbfTable getDbfTable(Class<T> entityClass) {
        DbfSource annotation = entityClass.getAnnotation(DbfSource.class);
        String dbfPath = generateDbfFilePath(annotation.path()) + annotation.value();

        if (annotation.useLocalCache()) {
            String localCacheFilePath = localCachePath + annotation.value();
            copyToLocalCache(dbfPath, localCacheFilePath);
            dbfPath = localCacheFilePath;
        }

        try {
            return dbfDataExtractor.extract(dbfPath);
        } catch (Exception e) {
            throw new DbfConverterException("Ошибка при чтении файла " + ": " + dbfPath, e);
        }
    }

    private String generateDbfFilePath(String path) {
        if (path.isEmpty()) {
            return dbfDefaultSourcePath;
        }
        return path;
    }

    private void copyToLocalCache(String sourcePath, String destinationPath) {
        try {
            Files.createDirectories(Paths.get(localCachePath));
            Files.copy(Paths.get(sourcePath), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new DbfConverterException("Ошибка при копировании файла с сетевого диска: " + sourcePath, e);
        }
    }

    private <T> T createEntityFromDbf(Class<T> entityClass, Map<String, Object> dbfRow, List<DbfColumn> dbfColumns)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        T entity = entityClass.getDeclaredConstructor().newInstance();
        Map<String, String> dbfColumnTypeMap = dbfColumns.stream().collect(Collectors.toMap(DbfColumn::getName, DbfColumn::getType));

        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(DbfField.class)) {
                String dbfFieldName = field.getAnnotation(DbfField.class).value();
                if (!dbfRow.containsKey(dbfFieldName)) {
                    throw new DbfConverterException("Столбец '" + dbfFieldName + "' не найден в данных .dbf-файла");
                }

                String dbfColumnType = dbfColumnTypeMap.get(dbfFieldName);
                if (dbfColumnType == null) {
                    throw new DbfConverterException("Тип данных для столбца '" + dbfFieldName + "' не определен в .dbf-файле");
                }

                checkTypeCompatibility(field, dbfColumnType, dbfFieldName);

                try {
                    field.set(entity, convertValue(field, dbfRow.get(dbfFieldName)));
                } catch (IllegalArgumentException e) {
                    throw new DbfConverterException("Ошибка при преобразовании значения для поля:" + field.getName(), e);
                }
            }
        }

        return entity;
    }

    private void checkTypeCompatibility(Field field, String dbfColumnType, String dbfFieldName) {
        String upperDbfColumnType = dbfColumnType.toUpperCase();
        Class<?> fieldType = field.getType();

        if (DBF_TYPE_CHAR.equals(upperDbfColumnType)) {
            if (fieldType != String.class && !field.isAnnotationPresent(CharToNumeric.class)) {
                throw new DbfConverterException("Несовместимые типы данных: поле '" + dbfFieldName + "' имеет тип 'CHAR', но ожидается тип 'String'");
            }
        } else if (DBF_TYPE_NUMERIC.equals(upperDbfColumnType)) {
            if (!(fieldType == Integer.class || fieldType == Long.class || fieldType == Float.class || fieldType == Double.class)) {
                throw new DbfConverterException("Несовместимые типы данных: поле '" + dbfFieldName + "' имеет тип 'NUMERIC', но ожидается тип 'Integer', 'Long', 'Float' или 'Double'");
            }
        } else if (DBF_TYPE_DATE.equals(upperDbfColumnType)) {
            if (fieldType != java.sql.Date.class) {
                throw new DbfConverterException("Несовместимые типы данных: поле '" + dbfFieldName + "' имеет тип 'DATE', но ожидается тип 'java.sql.Date'");
            }
        } else if (DBF_TYPE_LOGICAL.equals(upperDbfColumnType)) {
            if (!(fieldType == Boolean.class || fieldType == boolean.class)) {
                throw new DbfConverterException("Несовместимые типы данных: поле '" + dbfFieldName + "' имеет тип 'LOGICAL', но ожидается тип 'Boolean' или 'boolean'");
            }
        } else {
            throw new DbfConverterException("Неизвестный тип данных: поле '" + dbfFieldName + "' имеет тип '" + dbfColumnType + "', который не поддерживается");
        }
    }

    private Object convertValue(Field field, Object value) {
        Class<?> fieldType = field.getType();
        if (value == null) {
            if (field.isAnnotationPresent(NullChanger.class)) {
                if (fieldType == Long.class || fieldType == long.class) {
                    return 0L;
                } else if (fieldType == Integer.class || fieldType == int.class) {
                    return 0;
                } else if (fieldType == Double.class || fieldType == double.class) {
                    return .0;
                }
            }
            return null;
        }

        try {
            if (field.isAnnotationPresent(CharToNumeric.class)) {
                return convertCharToNumericValue(value, fieldType);
            } else if (fieldType == Long.class || fieldType == long.class) {
                if (value instanceof Number) {
                    return ((Number) value).longValue();
                }
                String stringValue = value.toString().split("\\.")[0];
                return Long.parseLong(stringValue);
            } else if (fieldType == Integer.class || fieldType == int.class) {
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }
                String stringValue = value.toString().split("\\.")[0];
                return Integer.parseInt(stringValue);
            } else if (fieldType == Double.class || fieldType == double.class) {
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
                return Double.parseDouble(value.toString());
            } else if (fieldType == Float.class || fieldType == float.class) {
                if (value instanceof Number) {
                    return ((Number) value).floatValue();
                }
                return Float.parseFloat(value.toString());
            } else if (fieldType == String.class) {
                String trim = value.toString().trim();
                if (trim.isEmpty()) {
                    return null;
                }
                return trim;
            } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                return Boolean.parseBoolean(value.toString());
            } else if (fieldType == java.sql.Date.class) {
                if (value instanceof java.util.Date) {
                    return new java.sql.Date(((java.util.Date) value).getTime());
                } else if (value instanceof String) {
                    return java.sql.Date.valueOf(value.toString());
                }
            }
            return value;
        } catch (NumberFormatException e) {
            throw new DbfConverterException("Невозможно преобразовать значение '" + value + "' к типу " + fieldType.getName(), e);
        }
    }

    private static Object convertCharToNumericValue(Object value, Class<?> fieldType) {
        String stringValue = value.toString().split("\\.")[0];
        if (!value.toString().trim().isEmpty()) {
            if (fieldType == Long.class || fieldType == long.class) {
                return Long.parseLong(stringValue);
            } else if (fieldType == Integer.class || fieldType == int.class) {
                return Integer.parseInt(stringValue);
            } else if (fieldType == Double.class || fieldType == double.class) {
                return Double.parseDouble(value.toString());
            } else if (fieldType == Float.class || fieldType == float.class) {
                return Float.parseFloat(value.toString());
            }
        }
        return null;
    }
}
