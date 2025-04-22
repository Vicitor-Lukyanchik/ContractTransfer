package com.example.transfer.dbf.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class DbfColumn {
    private String name; // Название столбца
    private String type; // Тип данных (например, "NUMERIC", "CHAR", "DATE")
}