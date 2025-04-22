package com.example.transfer.s02015.processor;

import com.example.transfer.dbf.annotation.ProcessingOrder;
import com.example.transfer.dbf.exception.ProcessException;
import com.example.transfer.dbf.processor.FieldProcessor;
import com.example.transfer.dbf.service.impl.DbfMigrationSchedulerImpl;
import com.example.transfer.s02015.annotation.PackIdLookup;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
@ProcessingOrder(20)
public class PackIdLookupProcessor implements FieldProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PackIdLookupProcessor.class);

    @Override
    public boolean supports(Annotation annotation) {
        return annotation instanceof PackIdLookup;
    }

    @Override
    public Object process(Field field, Object entity, Connection connection) throws IllegalAccessException {
        if (connection == null) {
            throw new ProcessException("Connection required for this processor.");
        }

        try {
            PackIdLookup annotation = field.getAnnotation(PackIdLookup.class);
            if (annotation == null) {
                throw new ProcessException("Annotation @PackIdLookup not found on field: " + field.getName());
            }

            Long specifId = getFieldValue(entity, "specifId");
            Long dogId = getFieldValue(entity, "dogId");
            String kodm = getFieldValue(entity, "kodm");
            Double vesUnit = roundValue(getFieldValue(entity, "vesUnit"));

            if (specifId == null || dogId == null || kodm == null || vesUnit == null) {
                logger.error("D_KD:" + dogId + "; S_ID:" + specifId + "; KODM:" + kodm + "; VES_UNIT:" + vesUnit);
                throw new ProcessException("Не все необходимые поля заполнены для обработки аннотации @PackIdLookup. (Packing)");
            }

            Long packId = fetchPackIdFromDatabase(connection, specifId, dogId, kodm, vesUnit);

            field.setAccessible(true);
            field.set(entity, packId);

            return packId;
        } catch (Exception e) {
            throw new ProcessException("Ошибка при обработке аннотации @PackIdLookup: " + e.getMessage());
        }
    }

    private <T> T getFieldValue(Object entity, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = entity.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(entity);
    }

    private double roundValue(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(6, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private Long fetchPackIdFromDatabase(Connection connection, Long specifId, Long dogId, String kodm, Double vesUnit) throws SQLException {
        String query = "SELECT \"PACK_ID\" FROM \"PACKING\" " +
                "WHERE \"SPECIF_ID\" = ? " +
                "AND \"DOG_ID\" = ? " +
                "AND \"KODM\" = ? " +
                "AND \"VES_UNIT\" = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, specifId);
            statement.setLong(2, dogId);
            statement.setString(3, kodm);
            statement.setDouble(4, vesUnit);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Long packId = resultSet.getLong("PACK_ID");
                if (resultSet.wasNull()) {
                    return null;
                }
                return packId;
            }
        }
        throw new ProcessException("Ошибка при обработке аннотации @PackIdLookup (Packing)");
    }
}
