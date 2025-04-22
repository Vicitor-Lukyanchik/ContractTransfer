package com.example.transfer.dbf.entity;

import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;
import lombok.Data;

@DbfSource("src/test/resources/dbf/test.dbf")
@Data
public class IncompatibleTypeEntity {
    @DbfField("ID")
    private Long id;

    @DbfField("NAME") // Ожидается String, но в DBF указан NUMERIC
    private String name;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}