package com.example.transfer.s16014.entity;

import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;
import com.example.transfer.dbf.annotation.MigrationOrder;
import com.example.transfer.dbf.annotation.DefaultValue;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "country")
@DbfSource(value = "COUNTRY.DBF", useLocalCache = true)
@MigrationOrder(1)
public class Country {

    @Id
    @Column(name = "COUNT_ID")
    @DbfField("COUNTR_KOD")
    @NotNull(message = "(Oracle: COUNT_ID) Id страны не может быть null")
    private Long countryId;

    @Column(name = "COUNT_NAME_R")
    @DbfField("COUNTR_NAZ")
    @NotNull(message = "(Oracle: COUNT_NAME_R) Название страны не может быть null")
    private String countryNameR;

    @Column(name = "COUNT_KB")
    @DbfField("COUNTR_KB")
    private String countryKB;

    @Column(name = "COUNT_NAME_E")
    @DefaultValue(" ")
    private String countryNameE;
}