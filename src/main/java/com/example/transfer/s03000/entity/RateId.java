package com.example.transfer.s03000.entity;

import lombok.*;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class RateId implements Serializable {
    private Long kod; // CUR_KOD
    private Date date; // RAT_DATA

}