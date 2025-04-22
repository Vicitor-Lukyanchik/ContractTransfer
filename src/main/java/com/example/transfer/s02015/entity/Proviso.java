package com.example.transfer.s02015.entity;

import com.example.transfer.dbf.annotation.CharToNumeric;
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
@Table(name = "proviso")
@DbfSource(value = "ALT_OPL.DBF", useLocalCache = true)
@MigrationOrder(4)
public class Proviso {
    @Id
    @Column(name = "prov_id")
    @DbfField("ID_OPL")
    private Long provId;

    @Column(name = "dog_id")
    @DbfField("D_KD")
    private Long dogId;

    @Column(name = "rasch_id")
    @DbfField("RASCH_ID")
    private Long raschId;

    @Column(name = "prov_co_day")
    @DbfField("CO_DAY")
    private Long provCoDay;

    @Column(name = "prov_day")
    @DbfField("DAY")
    private String provDay;

    @Column(name = "prov_pr_adv")
    @DbfField("PR_ADV")
    private Long provPrAdv;

    @Column(name = "prov_dotgr")
    @DbfField("DAT_OTGR")
    private Date provDotgr;

    @Column(name = "prov_dopl")
    @DbfField("DAT_OPL")
    private Date provDopl;

    @Column(name = "prov_dreg")
    @DbfField("DAT_PEG")
    private Date provDreg;

    @Column(name = "prov_dedit")
    @DbfField("DAT_ED")
    private Date provDedit;

    @Column(name = "prov_isp")
    @DbfField("ISP")
    @CharToNumeric
    private Long provIsp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proviso proviso = (Proviso) o;
        return Objects.equals(provId, proviso.provId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provId);
    }
}