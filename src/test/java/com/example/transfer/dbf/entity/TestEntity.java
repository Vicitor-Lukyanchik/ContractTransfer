package com.example.transfer.dbf.entity;

import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;
import lombok.Data;

import java.sql.Date;

@DbfSource("src/test/resources/dbf/test.dbf")
@Data
public class TestEntity {
    @DbfField("ID")
    private Long id;

    @DbfField("NAME")
    private String name;

    @DbfField("DATE")
    private Date date;

    @DbfField("ISGOOD")
    private Boolean isGood;
}