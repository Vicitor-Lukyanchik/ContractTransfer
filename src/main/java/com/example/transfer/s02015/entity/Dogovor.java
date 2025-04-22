package com.example.transfer.s02015.entity;

import com.example.transfer.dbf.annotation.*;
import com.example.transfer.s02015.annotation.DefaultValue;
import com.example.transfer.s02015.annotation.DepId;
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
@Table(name = "dogovor")
@DbfSource(value = "REG_DOG.DBF", useLocalCache = true)
@StartSql("UPDATE \"DOGOVOR\" SET \"DOG_OPERABLE\" = 0")
@MigrationOrder(1)
public class Dogovor {

    @Id
    @Column(name = "dog_id")
    @DbfField("D_KD")
    private Long dogId;

    @Column(name = "vdoc_id")
    @DbfField("OT_HR")
    private Long vdocId;

    @Column(name = "dog_pri")
    @DbfField("D_PR")
    private Long dogPri;

    @Column(name = "dog_nd")
    @DbfField("D_ND")
    private String dogNd;

    @Column(name = "dog_dath")
    @DbfField("D_DATH")
    private Date dogDath;

    @Column(name = "dog_reg")
    @DbfField("D_REG")
    private Date dogReg;

    @Column(name = "dog_dbeg")
    @DbfField("D_DATHO")
    private Date dogDbeg;

    @Column(name = "dog_dend")
    @DbfField("D_DATE")
    private Date dogDend;

    @Column(name = "firm_id")
    @DbfField("D_KFIRM")
    private Long firmId;

    @Column(name = "cur_id")
    @DbfField("D_CUR")
    private Long curId;

    @Column(name = "dog_sumd")
    @DbfField("D_SUMD")
    private Double dogSumd;

    @Column(name = "dog_them")
    @DbfField("D_KPO")
    private String dogThem;

    @Column(name = "dog_sluzba")
    @DbfField("D_OTD")
    private String dogSluzba;

    @Column(name = "dog_otv")
    @DbfField("D_OTV")
    private String dogOtv;

    @Column(name = "dog_sogl")
    @DbfField("D_SOGL")
    private Integer dogSogl;

    @Column(name = "dog_specif")
    @DbfField("D_SPECIF")
    private Integer dogSpecif;

    @Column(name = "rasch_id")
    @DbfField("D_OR_AC")
    private Long raschId;

    @Column(name = "dog_co_day")
    @DbfField("D_CO_DAY")
    private Long dogCoDay;

    @Column(name = "dog_day")
    @DbfField("D_DAY")
    private String dogDay;

    @Column(name = "dog_pr_adv")
    @DbfField("D_PR_ADV")
    private Long dogPrAdv;

    @Column(name = "dog_shtr")
    @DbfField("D_STR")
    private Double dogShtr;

    @Column(name = "dog_dotgr")
    @DbfField("D_OTGR")
    private Date dogDotgr;

    @Column(name = "dog_dopl")
    @DbfField("D_DAT_OPL")
    private Date dogDopl;

    @Column(name = "dog_dat_ed")
    @DbfField("D_DAT_ED")
    private Date dogDatEd;

    @Column(name = "tpack_id")
    @DbfField("D_PACK")
    private Long tpackId;

    @Column(name = "prod_spok")
    @DbfField("PORD_SROK")
    private Date prodSpok;

    @Column(name = "dog_isp_id")
    @DbfField("D_ISP")
    @CharToNumeric
    private Integer dogIspId;

    @Column(name = "dog_claim")
    private Long dogClaim;

    @Column(name = "deliv_id")
    @Transient
    private Long delivId;

    @Column(name = "dog_operable")
    @DefaultValue("1")
    private Integer dogOperable;

    @Column(name = "dep_id")
    @DepId(sourceField="dogSluzba", username="S11000", password = "S11000")
    private Integer depId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dogovor dogoVor = (Dogovor) o;
        return Objects.equals(dogId, dogoVor.dogId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dogId);
    }
}