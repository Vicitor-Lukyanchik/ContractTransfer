package com.example.transfer.s02015.processor;

import com.example.transfer.dbf.exception.ProcessException;
import com.example.transfer.s02015.annotation.DepId;
import com.example.transfer.dbf.processor.FieldProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DepIdProcessor implements FieldProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DepIdProcessor.class);

    @Override
    public boolean supports(Annotation annotation) {
        return annotation.annotationType().getSimpleName().equals("DepId");
    }

    @Override
    public Object process(Field field, Object entity, Connection connection) throws IllegalAccessException {
        if (connection == null) {
            throw new IllegalArgumentException("Connection required for this processor.");
        }

        try {
            Annotation annotation = field.getAnnotation(DepId.class);

            if (annotation == null) {
                throw new IllegalArgumentException("Annotation not found on field: " + field.getName());
            }

            String sourceFieldName = getAnnotationValue(annotation);

            Field sourceField = entity.getClass().getDeclaredField(sourceFieldName);
            sourceField.setAccessible(true);
            String asSluzba = (String) sourceField.get(entity);

            return getDepartmentId(asSluzba, connection);

        } catch (NoSuchFieldException | SQLException | NoSuchMethodException | InvocationTargetException e) {
            throw new ProcessException("Ошибка при обработке поля: " + e.getMessage());
        }
    }

    private String getAnnotationValue(Annotation annotation)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var method = annotation.annotationType().getDeclaredMethod("sourceField");
        return (String) method.invoke(annotation);
    }

    private Integer getDepartmentId(String asSluzba, Connection connection) throws SQLException {
        if (asSluzba == null || asSluzba.isEmpty()) return 0;

        int liKod = 0;

        if (asSluzba.length() >= 4 && asSluzba.substring(2, 4).equals("00")) {
            String lsPodr = asSluzba.substring(0, 2);
            String query1 = "SELECT \"DEP_ID\" FROM \"DEPARTMENT\" " +
                    "WHERE \"DEP_LEVEL\" = 1 AND \"DEP_DIGIT_CODE_LEV1\" = ?";

            try (PreparedStatement statement = connection.prepareStatement(query1)) {
                statement.setString(1, lsPodr);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    liKod = resultSet.getInt("DEP_ID");
                }
            }

        } else if ("2911".equals(asSluzba)) {
            liKod = 120;
        } else {
            String query2 = "SELECT GetDepId(?) AS DEP_ID FROM DUAL";
            try (PreparedStatement statement = connection.prepareStatement(query2)) {
                statement.setString(1, asSluzba);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    liKod = resultSet.getInt("DEP_ID");
                }
            }
        }

        return liKod;
    }
}