package com.example.transfer.dbf.util;

import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.Id;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

@Component
public class ProcessorUtils {

    public static final String RECOMMENDATIONS = "Рекомендации: Проверьте корректность данных в указанном поле и записи.";
    public static final String INDENT = "                      ";
    //    public static final String INDENT = "         ";
    public static final int COUNT_INFO_FIELD = 5;

    @Value("${dbf.default.source.path}")
    private String defaultDbfSourcePath; // Значение по умолчанию из application.properties

    public String buildErrorMessage(Object entity, Field field, String problem) {
        DbfSource dbfSource = entity.getClass().getAnnotation(DbfSource.class);
        String filePath = generateFilePath(dbfSource);

        String recordInfo = getRecordInfo(entity);

        String fieldName = "Поле: " + getFieldNameWithAnnotation(field);

        return String.format("[ОШИБКА]\n" +
                        INDENT + "Ошибка в файле: %s\n" +
                        INDENT + "%s\n" +
                        INDENT + "%s\n" +
                        INDENT + "Проблема: %s\n" +
                        INDENT + "%s",
                filePath, recordInfo, fieldName, problem, RECOMMENDATIONS
        );
    }

    public String buildErrorMessage(Object entity, Map<Field, String> problems) {
        DbfSource dbfSource = entity.getClass().getAnnotation(DbfSource.class);
        String filePath = generateFilePath(dbfSource);

        String recordInfo = getRecordInfo(entity);

        StringBuilder problemsBuilder = new StringBuilder();
        problems.forEach((field, problem) -> {
            // Добавляем отступ для каждой проблемы
            problemsBuilder.append(INDENT).append("- Поле ")
                    .append(getFieldNameWithAnnotation(field))
                    .append(": ").append(problem).append("\n");
        });


        return String.format(
                "[ОШИБКА]\n" +
                        INDENT + "Ошибка в файле: %s\n" +
                        INDENT + "%s\n" +
                        INDENT + "Проблемы:\n%s" +
                        INDENT + "%s",
                filePath, recordInfo, problemsBuilder, RECOMMENDATIONS
        );
    }

    private String generateFilePath(DbfSource dbfSource) {
        if (dbfSource == null) {
            return "Неизвестный файл";
        }

        String path = dbfSource.path();
        String value = dbfSource.value();

        // Если path пустой, используем дефолтное значение
        if (path == null || path.isEmpty()) {
            return defaultDbfSourcePath + value;
        }

        return path + value;
    }

    private static String getRecordInfo(Object entity) {
        StringBuilder recordInfo = new StringBuilder("Запись:");

        try {
            Field idField = findFieldWithAnnotation(entity.getClass(), Id.class);
            if (idField != null) {
                idField.setAccessible(true);
                Object idValue = idField.get(entity);
                if (idValue != null) {
                    recordInfo.append(" ID=").append(idValue);
                    return recordInfo.toString();
                }
            }

            appendAdditionalFields(recordInfo, entity);

            return recordInfo.toString();
        } catch (IllegalAccessException e) {
            return "Запись: Данные объекта: " + entity;
        }
    }

    private static Field findFieldWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotation)) {
                return field;
            }
        }
        return null;
    }

    private static void appendAdditionalFields(StringBuilder recordInfo, Object entity) throws IllegalAccessException {
        Field[] fields = entity.getClass().getDeclaredFields();
        int fieldsAdded = 0;

        for (Field field : fields) {
            if (fieldsAdded >= COUNT_INFO_FIELD) {
                return;
            }

            field.setAccessible(true);
            Object fieldValue = field.get(entity);

            if (fieldValue != null) {
                recordInfo.append(" ").append(field.getName()).append("=").append(fieldValue);
                fieldsAdded++;
            } else {
                recordInfo.append(" ").append(field.getName()).append("=").append("null");
                fieldsAdded++;
            }
        }
    }

    private static String getFieldNameWithAnnotation(Field field) {
        if (field == null) {
            return "Поле не указано";
        }

        DbfField dbfFieldAnnotation = field.getAnnotation(DbfField.class);
        if (dbfFieldAnnotation != null && !dbfFieldAnnotation.value().isEmpty()) {
            return dbfFieldAnnotation.value(); // Возвращаем значение из аннотации
        }

        return field.getName();
    }
}
