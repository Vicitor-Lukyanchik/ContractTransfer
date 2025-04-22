package com.example.transfer.dbf.entity;

import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;
import lombok.Data;

@DbfSource("")
@Data
public  class TestEntityWithUnknownField {
    @DbfField("ID")
    private Long id;

    @DbfField("UNKNOWN_FIELD") // Поле с неизвестным типом
    private Object unknownField;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Object getUnknownField() { return unknownField; }
    public void setUnknownField(Object unknownField) { this.unknownField = unknownField; }
}
