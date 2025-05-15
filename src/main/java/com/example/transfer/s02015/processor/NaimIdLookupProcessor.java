package com.example.transfer.s02015.processor;

import com.example.transfer.dbf.exception.ProcessException;
import com.example.transfer.dbf.processor.FieldProcessor;
import com.example.transfer.dbf.util.ProcessorUtils;
import com.example.transfer.s02015.annotation.NaimIdLookup;
import lombok.RequiredArgsConstructor;
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

    private final ProcessorUtils processorUtils;

    @Override
    public boolean supports(Annotation annotation) {
        return annotation instanceof NaimIdLookup;
    }

    @Override
    public Object process(Field field, Object entity, Connection connection) throws IllegalAccessException {
        if (connection == null) {
            throw new ProcessException("Неудаётся подключиться к базе");
        }

        try {
            NaimIdLookup annotation = field.getAnnotation(NaimIdLookup.class);
            if (annotation == null) {
                throw new ProcessException("Аннотация не найдена в поле: " + field.getName() + "=" + field.get(entity));
            }

            String sourceFieldName = annotation.sourceField();
            Object sourceFieldValue = getSourceFieldValue(entity, sourceFieldName);

            if (sourceFieldValue == null) {
                throw new ProcessException("Значение исходного поля равно null для поля " + field.getName());
            }

            Long resultValue = fetchFromDatabase(connection, sourceFieldValue.toString());

            field.setAccessible(true);
            field.set(entity, resultValue);

            return resultValue;
        } catch (Exception e) {
            throw new ProcessException(processorUtils.buildErrorMessage(entity, field,e.getMessage()));
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
        throw new ProcessException("Ошибка при запросе: " + query);
    }
}
