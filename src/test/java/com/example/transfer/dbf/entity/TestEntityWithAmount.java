package com.example.transfer.dbf.entity;

import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;

@DbfSource("src/test/resources/dbf/test.dbf")
public class TestEntityWithAmount {
    @DbfField("ID")
    private Long id;

    @DbfField("AMOUNT")
    private Double amount;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}