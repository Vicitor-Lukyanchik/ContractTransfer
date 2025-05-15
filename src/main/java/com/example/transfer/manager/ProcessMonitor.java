package com.example.transfer.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessMonitor {
    /**
     * Проверяет, запущен ли процесс с указанным уникальным идентификатором.
     *
     * @param processName Имя процесса (например, "java.exe").
     * @param identifier  Уникальный идентификатор миграции (например, "s02015").
     * @return true, если процесс запущен с указанным идентификатором.
     */
    public static boolean isProcessRunning(String processName, String identifier) {
        try {
            // Запускаем команду WMIC для получения списка процессов с командной строкой и PID
            Process process = Runtime.getRuntime().exec("wmic process where \"name='" + processName + "'\" get CommandLine,ProcessId");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "CP866"));

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {

                    // Проверяем, содержит ли команда уникальный идентификатор
                    if (trimmedLine.contains(identifier)) {
                        return true; // Процесс найден
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // Процесс не найден
    }

    /**
     * Останавливает процесс с указанным уникальным идентификатором.
     *
     * @param processName Имя процесса (например, "java.exe").
     * @param identifier  Уникальный идентификатор миграции (например, "s02015").
     */
    public static void stopProcess(String processName, String identifier) {
        try {
            // Запускаем команду WMIC для получения PID процесса
            Process process = Runtime.getRuntime().exec("wmic process where \"name='" + processName + "'\" get CommandLine,ProcessId");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "CP866"));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 2 && line.contains(identifier)) {
                    // Получаем PID процесса
                    String pid = parts[parts.length - 1]; // PID всегда последний элемент
                    // Завершаем процесс по PID
                    Runtime.getRuntime().exec("taskkill /PID " + pid + " /F");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}