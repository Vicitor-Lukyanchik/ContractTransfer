package com.example.transfer.s02015.entity;

import com.example.transfer.dbf.annotation.CharToNumeric;
import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;
import com.example.transfer.dbf.annotation.MigrationOrder;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "dogo_sogl")
@DbfSource(value = "REG_DCOP.DBF", useLocalCache = true)
@MigrationOrder(2)
public class DogoSogl {

    @Column(name = "dog_id")
    @DbfField("D_KD")
    private Long dogId;

    @Id
    @Column(name = "sogl_id")
    @DbfField("D_KSO")
    private Long soglId;

    @Column(name = "sogl_nd")
    @DbfField("D_ND")
    private String soglNd;

    @Column(name = "sogl_dath")
    @DbfField("D_DATH")
    private Date soglDath;

    @Column(name = "sogl_reg")
    @DbfField("D_REG")
    private Date soglReg;

    @Column(name = "sogl_dbeg")
    @DbfField("D_DATHO")
    private Date soglDbeg;

    @Column(name = "sogl_dend")
    @DbfField("D_DATE")
    private Date soglDend;

    @Column(name = "firm_id")
    @DbfField("D_KFIRM")
    private Long firmId;

    @Column(name = "cur_id")
    @DbfField("D_CUR")
    private Long curId;

    @Column(name = "sogl_sumd")
    @DbfField("D_SUMD")
    private Double soglSumd;

    @Column(name = "sogl_otv")
    @DbfField("D_OTV")
    private String soglOtv;

    @Column(name = "rasch_id")
    @DbfField("D_OR_AC")
    private Long raschId;

    @Column(name = "sogl_co_day")
    @DbfField("D_CO_DAY")
    private Long soglCoDay;

    @Column(name = "sogl_day")
    @DbfField("D_DAY")
    private String soglDay;

    @Column(name = "sogl_pr_adv")
    @DbfField("D_PR_ADV")
    private Long soglPrAdv;

    @Column(name = "sogl_shtr")
    @DbfField("D_STR")
    private Double soglShtr;

    @Column(name = "sogl_dotgr")
    @DbfField("D_OTGR")
    private Date soglDotgr;

    @Column(name = "sogl_dopl")
    @DbfField("D_DAT_OPL")
    private Date soglDopl;

    @Column(name = "sogl_dat_ed")
    @DbfField("D_DAT_ED")
    private Date soglDatEd;

    @Column(name = "sogl_isp_id")
    @DbfField("D_ISP")
    @CharToNumeric
    private Long soglIspId;

    @Column(name = "sogl_them", insertable = false, updatable = false)
    private String soglThem;

    @Column(name = "sogl_sluzba")
    @DbfField("D_OTD")
    private String soglSluzba;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DogoSogl dogoSogl = (DogoSogl) o;
        return Objects.equals(dogId, dogoSogl.dogId) && Objects.equals(soglId, dogoSogl.soglId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dogId, soglId);
    }
}