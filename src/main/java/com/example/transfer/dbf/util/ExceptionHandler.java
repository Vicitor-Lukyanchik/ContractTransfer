package com.example.transfer.dbf.util;

import com.example.transfer.dbf.service.impl.DbfMigrationSchedulerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

public class ExceptionHandler {

    public static final Logger logger = LoggerFactory.getLogger(DbfMigrationSchedulerImpl.class);
    public static final String INDENT = "                               ";


    public static void handleDataIntegrityViolationException(Exception e) {
        DataIntegrityViolationException constraintViolationException = (DataIntegrityViolationException) e;
        String message = constraintViolationException.getMessage();
        String constraintName = extractConstraintName(message);
        StringBuilder resultMessage = new StringBuilder();

        resultMessage.append("Произошла ошибка при выполнении операции с базой данных:\n");
        resultMessage.append(INDENT + "Причина: Нарушение ограничения целостности базы данных.\n");
        if (constraintName != null) {
            resultMessage.append(INDENT + "Ограничение: " + constraintName + "\n");
            resultMessage.append(INDENT + "Рекомендация: Убедитесь, что данные соответствуют ограничениям базы данных.\n");
            resultMessage.append(INDENT + "Например, проверьте, существует ли родительская запись для внешнего ключа.\n\n");
        } else {
            resultMessage.append(INDENT + "Рекомендация: Убедитесь, что данные соответствуют ограничениям базы данных.\n");
            resultMessage.append(INDENT + "Например, проверьте, существует ли запись которая больше по размеру чем нужно, не соответствет типу данных или пустое значение где обязательно not null.\n\n");
        }
        logger.error(resultMessage.toString());
    }

    private static String extractConstraintName(String message) {
        if (message == null) return null;
        String regex = "constraint \\[(.*?)\\]";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1); // Возвращаем имя ограничения
        }
        return null;
    }
}
