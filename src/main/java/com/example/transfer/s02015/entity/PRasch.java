package com.example.transfer.s02015.entity;

import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;
import com.example.transfer.dbf.annotation.MigrationOrder;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "p_rasch")
@DbfSource(value = "P_RASCH.DBF", useLocalCache = true)
@MigrationOrder(5)
public class PRasch {
    @Id
    @Column(name = "rasch_id")
    @DbfField("RASCH_ID")
    private Long raschId;

    @Column(name = "rasch_naim")
    @DbfField("RASCH_NAIM")
    private String raschNaim;

    @Column(name = "rasch_imp")
    @DbfField("RASCH_IMP")
    private Integer raschImp;

    @Column(name = "rasch_exp")
    @DbfField("RASCH_EXP")
    private Integer raschExp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PRasch pRasch = (PRasch) o;
        return Objects.equals(raschId, pRasch.raschId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(raschId);
    }
}