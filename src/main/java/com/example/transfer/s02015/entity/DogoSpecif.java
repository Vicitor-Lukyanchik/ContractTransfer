package com.example.transfer.s02015.entity;

import com.example.transfer.dbf.annotation.*;
import com.example.transfer.dbf.annotation.DefaultValue;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "dogo_specif")
@StartSql("UPDATE \"DOGO_SPECIF\" SET \"SPECIF_OPERABLE\" = 0")
@DbfSource(value = "SPECIF.DBF", useLocalCache = true)
@MigrationOrder(3)
public class DogoSpecif {

    @Column(name = "dog_id")
    @DbfField("S_KD")
    @NotNull(message = "(Oracle: dog_id) Номер договора в спецификации не может быть null")
    private Long dogId;

    @Id
    @Column(name = "specif_id")
    @DbfField("S_ID")
    @NotNull(message = "(Oracle: specif_id) Номер спецификации не может быть null")
    private Long specifId;

    @Column(name = "specif_ns")
    @DbfField("S_NS")
    private String specifNs;

    @Column(name = "specif_dath")
    @DbfField("S_DATH")
    private Date specifDath;

    @Column(name = "specif_reg")
    @DbfField("S_REG")
    private Date specifReg;

    @Column(name = "cur_id")
    @DbfField("S_CUR")
    private Long curId;

    @Column(name = "specif_sums")
    @DbfField("S_SUMS")
    private Double specifSums;

    @Column(name = "specif_nds")
    @DbfField("S_NDS")
    private Double specifNds;

    @Column(name = "specif_kurs")
    @DbfField("S_KURS")
    private Double specifKurs;

    @Column(name = "rasch_id")
    @DbfField("S_OR_AC")
    private Long raschId;

    @Column(name = "specif_co_day")
    @DbfField("S_CO_DAY")
    private Long specifCoDay;

    @Column(name = "specif_pr_adv")
    @DbfField("S_PR_ADV")
    private Long specifPrAdv;

    @Column(name = "specif_day")
    @DbfField("S_DAY")
    private String specifDay;

    @Column(name = "specif_shtr")
    @DbfField("S_STR")
    private Double specifShtr;

    @Column(name = "specif_dopl")
    @DbfField("S_DAT_OPL")
    private Date specifDopl;

    @Column(name = "specif_dotgr")
    @DbfField("S_DAT_OTGR")
    private Date specifDotgr;

    @Column(name = "specif_dat_ed")
    @DbfField("S_DAT_ED")
    private Date specifDatEd;

    @Column(name = "tpack_id")
    @DbfField("S_PACK")
    private Long tpackId;

    @Column(name = "specif_isp")
    @DbfField("S_ISP")
    @CharToNumeric
    private Long specifIsp;

    @Column(name = "deliv_id", insertable = false, updatable = false)
    private Long delivId;

    @Column(name = "specif_operable")
    @DefaultValue("1")
    private Integer specifOperable;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DogoSpecif that = (DogoSpecif) o;
        return Objects.equals(dogId, that.dogId) && Objects.equals(specifId, that.specifId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dogId, specifId);
    }
}