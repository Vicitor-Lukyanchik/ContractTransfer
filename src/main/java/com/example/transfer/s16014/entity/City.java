package com.example.transfer.s16014.entity;

import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;
import com.example.transfer.dbf.annotation.DefaultValue;
import com.example.transfer.dbf.annotation.MigrationOrder;
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
@Table(name = "CITY")
@DbfSource(value = "CITY.DBF", useLocalCache = true)
@MigrationOrder(2)
public class City {

    @Id
    @Column(name = "CITY_ID")
    @DbfField("CITY_KOD")
    @NotNull(message = "(Oracle: CITY_ID) Id города не может быть null")
    private Long cityId;

    @Column(name = "COUNT_ID")
    @DbfField("CITY_COUNT")
    private Long countryId;

    @Column(name = "CITY_NAME_R")
    @DbfField("CITY_NAZ")
    @NotNull(message = "(Oracle: CITY_NAME_R) Название города не может быть null")
    private String cityNameR;

    @Column(name = "CITY_NAME_E")
    @DefaultValue(" ")
    private String cityNameE;
}