package com.example.transfer.dbf.entity;

import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;

@DbfSource("")
public class TestEntityWithLargeString {
    @DbfField("ID")
    private Long id;

    @DbfField("DESCRIPTION")
    private String description;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}