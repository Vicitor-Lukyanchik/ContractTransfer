package com.example.transfer.dbf.entity;

import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;

@DbfSource("")
public class TestEntityWithIgnoredField {
    @DbfField("ID")
    private Long id;

    @DbfField("NAME")
    private String name;

    private String ignoredField; // Поле без аннотации @DbfField

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIgnoredField() { return ignoredField; }
    public void setIgnoredField(String ignoredField) { this.ignoredField = ignoredField; }
}