package com.example.transfer.s03000.entity;

import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;
import com.example.transfer.dbf.annotation.MigrationOrder;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "RATE")
@IdClass(RateId.class)
@DbfSource(value = "RATE.DBF", useLocalCache = true)
@MigrationOrder(1)
public class Rate {

    @Id
    @Column(name = "CUR_KOD")
    @DbfField("KOD")
    private Long kod;

    @Id
    @Column(name = "RAT_DATA")
    @DbfField("DATA")
    private Date date;

    @Column(name = "RAT_KURS")
    @DbfField("KURS")
    private Double kurs;

    @Column(name = "RAT_SHKALA")
    @DbfField("SHKALA")
    private Long shkala;

    @Column(name = "RAT_KURS_BO")
    @DbfField("KURS_BO")
    private Double kursBO;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rate)) return false;
        Rate rate = (Rate) o;
        return Objects.equals(getKod(), rate.getKod()) && Objects.equals(getDate(), rate.getDate()) && Objects.equals(getKurs(), rate.getKurs()) && Objects.equals(getShkala(), rate.getShkala()) && Objects.equals(getKursBO(), rate.getKursBO());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKod(), getDate(), getKurs(), getShkala(), getKursBO());
    }
}