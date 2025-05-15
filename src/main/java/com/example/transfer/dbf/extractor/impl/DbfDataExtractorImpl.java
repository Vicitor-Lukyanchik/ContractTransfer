package com.example.transfer.dbf.extractor.impl;

import com.example.transfer.dbf.domain.DbfColumn;
import com.example.transfer.dbf.domain.DbfTable;
import com.example.transfer.dbf.exception.DbfException;
import com.example.transfer.dbf.extractor.DbfDataExtractor;
import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DbfDataExtractorImpl implements DbfDataExtractor {

    private static final String DOS_ENCODING = "CP866";
    private static final String UNKNOWN = "UNKNOWN";
    private static final Map<Character, String> dbfTypeMapping = Map.of(
            'C', "CHAR",
            'N', "NUMERIC",
            'D', "DATE",
            'L', "LOGICAL",
            'F', "FLOAT",
            'M', "MEMO"
    );

    @Override
    public DbfTable extract(String path) {
        List<DbfColumn> columns;
        List<Map<String, Object>> rows;

        try (InputStream fis = new FileInputStream(path)) {
            DBFReader reader = new DBFReader(fis);
            reader.setCharactersetName(DOS_ENCODING);

            // Получаем метаданные столбцов
            columns = readDbfColumns(reader, path);
            rows = readDbfRows(reader, columns, path);
        } catch (DbfException e) {
            throw new DbfException(e.getMessage());
        } catch (Exception e){
            throw new DbfException("Ошибка при чтении файла: " + path);
        }

        return DbfTable.builder().columns(columns).rows(rows).build();
    }

    private List<DbfColumn> readDbfColumns(DBFReader reader, String path) {
        try {
            List<DbfColumn> columns = new ArrayList<>();
            int fieldCount = reader.getFieldCount();

            // Получаем метаданные столбцов
            for (int i = 0; i < fieldCount; i++) {
                DBFField field = reader.getField(i);
                byte dataType = field.getDataType();
                char typeChar = (char) dataType;
                String type = dbfTypeMapping.getOrDefault(typeChar, UNKNOWN);
                columns.add(DbfColumn.builder()
                        .name(field.getName())
                        .type(type)
                        .build());
            }
            return columns;
        } catch (DBFException e) {
            throw new DbfException("Ошибка при чтении файла: " + path);
        }

    }

    private List<Map<String, Object>> readDbfRows(DBFReader reader, List<DbfColumn> columns, String path) throws Exception {
        List<Map<String, Object>> rows = new ArrayList<>();
        int fieldCount = columns.size();
        int rowNumber = 0; // Счетчик строк

        // Читаем строки данных
        Object[] rowValues;
        while ((rowValues = reader.nextRecord()) != null) { // Проверяем, что запись существует
            rowNumber++; // Увеличиваем счетчик строки
            try {
                Map<String, Object> row = new HashMap<>();
                for (int i = 0; i < fieldCount; i++) {
                    row.put(columns.get(i).getName(), rowValues[i]); // Преобразуем массив в Map
                }
                rows.add(row);
            } catch (Exception e) {
                throw new DbfException("Ошибка при чтении строки №" + rowNumber + " файла: " + path, e);
            }
        }

        return rows;
    }
}