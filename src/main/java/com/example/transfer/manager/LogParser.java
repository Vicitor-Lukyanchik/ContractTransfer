package com.example.transfer.manager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogParser {

    public static class LogInfo {
        public final String lastLog;
        public final String timestamp;
        public final boolean hasError;

        public LogInfo(String lastLog, String timestamp, boolean hasError) {
            this.lastLog = lastLog;
            this.timestamp = timestamp;
            this.hasError = hasError;
        }
    }

    public static LogInfo getLastLogInfo(String logFilePath) throws IOException {
        StringBuilder lastLog = new StringBuilder();
        StringBuilder currentBlock = new StringBuilder();
        String line;
        String timestamp = "нет данных";
        boolean hasError = false;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFilePath), "Windows-1251"))) {
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty()) continue;

                if (isDateLine(trimmedLine)) {
                    // Сохраняем предыдущий блок
                    if (currentBlock.length() > 0) {
                        lastLog.setLength(0);
                        lastLog.append(currentBlock);
                        // Извлекаем временную метку из начала строки
                        timestamp = trimmedLine.substring(0, 19); // Формат "ГГГГ-ММ-ДД ЧЧ:ММ:СС"
                    }
                    currentBlock.setLength(0);
                    hasError = false;
                }

                currentBlock.append(trimmedLine).append("\n");
                if (trimmedLine.toLowerCase().contains("ошибка")||trimmedLine.toLowerCase().contains("exception")) {
                    hasError = true;
                }
            }

            // После завершения чтения файла сохраняем последний блок
            if (currentBlock.length() > 0) {
                lastLog.setLength(0);
                lastLog.append(currentBlock);
            }
        }

        return new LogInfo(lastLog.toString().trim(), timestamp, hasError);
    }

    /**
     * Проверяет, является ли строка началом новой записи (содержит дату).
     *
     * @param line Строка для проверки.
     * @return true, если строка содержит дату в формате "ГГГГ-ММ-ДД ЧЧ:ММ:СС".
     */
    private static boolean isDateLine(String line) {
        return line.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*");
    }
}