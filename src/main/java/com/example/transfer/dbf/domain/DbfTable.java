package com.example.transfer.dbf.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder(toBuilder = true)
public class DbfTable {
    private List<DbfColumn> columns; // столбцы
    private List<Map<String, Object>> rows; // Данные в формате Map (ключ - имя столбца, значение - данные)

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Если нет столбцов или строк, возвращаем пустую строку
        if (columns == null || columns.isEmpty() || rows == null || rows.isEmpty()) {
            return "Empty DbfTable";
        }

        // Определяем максимальную ширину каждого столбца
        int[] columnWidths = new int[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            columnWidths[i] = Math.max(columns.get(i).getName().length(), 8); // Минимальная ширина 8 символов
        }

        // Вычисляем максимальную ширину для каждого столбца на основе данных
        for (Map<String, Object> row : rows) {
            for (int i = 0; i < columns.size(); i++) {
                String columnName = columns.get(i).getName();
                String value = String.valueOf(row.get(columnName));
                columnWidths[i] = Math.max(columnWidths[i], value.length());
            }
        }

        // Формируем строку заголовков
        for (int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i).getName();
            sb.append(String.format("%-" + columnWidths[i] + "s", columnName));
            if (i < columns.size() - 1) {
                sb.append(" | "); // Разделитель между столбцами
            }
        }
        sb.append("\n");

        // Формируем разделительную линию
        for (int i = 0; i < columns.size(); i++) {
            sb.append(String.format("%-" + columnWidths[i] + "s", "-".repeat(columnWidths[i])));
            if (i < columns.size() - 1) {
                sb.append(" | ");
            }
        }
        sb.append("\n");

        // Формируем строки данных
        int rowCount = Math.min(rows.size(), 10); // Ограничиваем вывод первыми 10 строками
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Map<String, Object> row = rows.get(rowIndex);
            for (int i = 0; i < columns.size(); i++) {
                String columnName = columns.get(i).getName();
                String value = String.valueOf(row.get(columnName));
                sb.append(String.format("%-" + columnWidths[i] + "s", value));
                if (i < columns.size() - 1) {
                    sb.append(" | ");
                }
            }
            sb.append("\n");
        }

        // Если данных больше 10 строк, добавляем сообщение
        if (rows.size() > 10) {
            sb.append("... (and ").append(rows.size() - 10).append(" more rows)\n");
        }

        return sb.toString();
    }
}