package com.example.transfer.dbf.processor.impl;

import com.example.transfer.dbf.annotation.StartSql;
import com.example.transfer.dbf.processor.EntityProcessor;
import com.example.transfer.dbf.processor.FieldProcessor;
import com.example.transfer.dbf.processor.ProcessorRegistry;
import com.example.transfer.dbf.exception.ProcessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EntityProcessorImpl implements EntityProcessor {

    private final Map<String, Connection> connectionPool = new HashMap<>();

    private final Map<Class<?>, Map<Annotation, Field>> annotationCache = new HashMap<>();


    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final ProcessorRegistry processorRegistry;

    @Value("${spring.datasource.url}")
    public String DB_URL;

    @Override
    public <T> void processEntities(List<T> entities) {
        try {
            if (!entities.isEmpty()) {
                Class<?> clazz = entities.get(0).getClass();
                executeStartSqlAnnotations(clazz);
            }
            List<FieldProcessor> sortedProcessors = processorRegistry.getAllProcessorsSorted();

            for (T entity : entities) {
                processEntity(entity, sortedProcessors);
            }
        } finally {
            closeAllConnections();
        }
    }

    private void executeStartSqlAnnotations(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            StartSql startSqlAnnotation = field.getAnnotation(StartSql.class);
            if (startSqlAnnotation != null) {
                String sql = startSqlAnnotation.value();
                if (!sql.isEmpty()) {
                    try {
                        jdbcTemplate.update(sql, new MapSqlParameterSource());
                    } catch (Exception e) {
                        throw new ProcessException("Ошибка при выполнении SQL-запроса: " + e.getMessage());
                    }
                }
            }
        }
    }

    private <T> void processEntity(T entity, List<FieldProcessor> sortedProcessors) {
        Map<Annotation, Field> fieldAnnotations = getFieldAnnotations(entity);
        for (FieldProcessor processor : sortedProcessors) {
            for (Map.Entry<Annotation, Field> entry : fieldAnnotations.entrySet()) {
                Annotation annotation = entry.getKey();
                Field field = entry.getValue();

                if (processor.supports(annotation)) {
                    field.setAccessible(true);
                    try {
                        Connection connection = getConnectionForAnnotation(annotation);
                        if (connection != null) {
                            Object process = processor.process(field, entity, connection);
                            field.set(entity, process);
                        } else {
                            field.set(entity, processor.process(field, entity));
                        }
                    } catch (IllegalAccessException | SQLException | NoSuchMethodException |
                             InvocationTargetException e) {
                        throw new ProcessException(e.getMessage());
                    }
                }
            }
        }
    }

    private <T> Map<Annotation, Field> getFieldAnnotations(T entity) {
        Class<?> clazz = entity.getClass();
        return annotationCache.computeIfAbsent(clazz, this::loadFieldAnnotations);
    }

    private Map<Annotation, Field> loadFieldAnnotations(Class<?> clazz) {
        Map<Annotation, Field> fieldAnnotations = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                fieldAnnotations.put(annotation, field);
            }
        }
        return fieldAnnotations;
    }

    private Connection getConnectionForAnnotation(Annotation annotation)
            throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String annotationName = annotation.annotationType().getName();

        if (connectionPool.containsKey(annotationName)) {
            return connectionPool.get(annotationName);
        }

        if (requiresDatabaseConnection(annotation)) {
            Connection connection = createConnection(annotation);
            connectionPool.put(annotationName, connection);
            return connection;
        }

        return null;
    }

    private boolean requiresDatabaseConnection(Annotation annotation)
            throws InvocationTargetException, IllegalAccessException {
        try {
            String username = getAnnotationValue(annotation, "username");
            String password = getAnnotationValue(annotation, "password");
            return !username.isEmpty() && !password.isEmpty();
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private Connection createConnection(Annotation annotation)
            throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String username = getAnnotationValue(annotation, "username");
        String password = getAnnotationValue(annotation, "password");
        return DriverManager.getConnection(DB_URL, username, password);
    }

    private String getAnnotationValue(Annotation annotation, String parameterName)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = annotation.annotationType().getDeclaredMethod(parameterName);
        return (String) method.invoke(annotation);
    }

    private void closeAllConnections() {
        for (Connection connection : connectionPool.values()) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new ProcessException("Ошибка при закрытии соединения: " + e.getMessage());
                }
            }
        }
        connectionPool.clear();
    }
}
