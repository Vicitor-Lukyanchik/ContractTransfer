package com.example.transfer.s02015.processor;

import com.example.transfer.dbf.exception.ProcessException;
import com.example.transfer.dbf.processor.FieldProcessor;
import com.example.transfer.s02015.annotation.NaimIdLookup;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class NaimIdLookupProcessor implements FieldProcessor {

    private static final String TABLE_NAME = "SM_NAME";
    private static final String LOOKUP_COLUMN = "NAIM_KOD_OLD";
    private static final String RESULT_COLUMN = "NAIM_ID";

    @Override
    public boolean supports(Annotation annotation) {
        return annotation instanceof NaimIdLookup;
    }

    @Override
    public Object process(Field field, Object entity, Connection connection) throws IllegalAccessException {
        if (connection == null) {
            throw new IllegalArgumentException("Connection required for this processor.");
        }

        try {
            NaimIdLookup annotation = field.getAnnotation(NaimIdLookup.class);
            if (annotation == null) {
                throw new ProcessException("Annotation @NaimIdLookup not found on field: " + field.getName());
            }

            String sourceFieldName = annotation.sourceField();
            Object sourceFieldValue = getSourceFieldValue(entity, sourceFieldName);

            if (sourceFieldValue == null) {
                throw new ProcessException("Source field value is null for field: " + field.getName());
            }

            Long resultValue = fetchFromDatabase(connection, sourceFieldValue.toString());

            field.setAccessible(true);
            field.set(entity, resultValue);

            return resultValue;
        } catch (Exception e) {
            throw new ProcessException("Ошибка при обработке аннотации @NaimIdLookup: " + e.getMessage());
        }
    }

    private Object getSourceFieldValue(Object entity, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field sourceField = entity.getClass().getDeclaredField(fieldName);
        sourceField.setAccessible(true);
        return sourceField.get(entity);
    }

    private Long fetchFromDatabase(Connection connection, String lookupValue) throws SQLException {
        String query = String.format("SELECT %s FROM \"%s\" WHERE \"%s\" = ?", RESULT_COLUMN, TABLE_NAME, LOOKUP_COLUMN);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, lookupValue);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getLong(RESULT_COLUMN);
            }
        }
        throw new ProcessException("Ошибка при обработке аннотации @NaimIdLookup: ");
    }
}
