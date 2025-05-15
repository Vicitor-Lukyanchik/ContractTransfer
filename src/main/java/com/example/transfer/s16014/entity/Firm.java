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
import java.sql.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "FIRM")
@DbfSource(value = "FIRM.DBF", useLocalCache = true)
@MigrationOrder(3)
public class Firm {

    @Id
    @Column(name = "FIRM_ID")
    @DbfField("FIRM_KOD")
    @NotNull(message = "(Oracle: FIRM_ID) Id фирмы не может быть null")
    private Long firmId;

    @Column(name = "CITY_ID")
    @DbfField("FIRM_CITY")
    private Long cityId;

    @Column(name = "CA_ID")
    @DefaultValue("1")
    private Long caId;

    @Column(name = "FIRM_NAME_R")
    @DbfField("FIRM_NAZ")
    @NotNull(message = "(Oracle: FIRM_NAME_R) Название фирмы не может быть null")
    private String firmNameR;

    @Column(name = "FIRM_NAME_E")
    @DbfField("NFIRM_E")
    private String firmNameE;

    @Column(name = "FIRM_IND")
    @DbfField("IND")
    private Long firmInd;

    @Column(name = "FIRM_UNN_INN")
    @DbfField("UNN_INN")
    private String firmUnnInn;

    @Column(name = "FIRM_ADR_R")
    @DbfField("ADR_R")
    private String firmAdr;

    @Column(name = "FIRM_ADR_E")
    @DbfField("ADR_E")
    private String firmAdrE;

    @Column(name = "FIRM_OKPO")
    @DbfField("OKPO")
    private String firmOkpo;

    @Column(name = "FIRM_OKONH")
    @DbfField("OKONH")
    private String firmOkonh;

    @Column(name = "FIRM_SP")
    private String firmSp;

    @Column(name = "FIRM_NP")
    private String firmNp;

    @Column(name = "FIRM_LNP")
    private String firmLnp;

    @Column(name = "FIRM_DVP")
    private Date firmDvp;

    @Column(name = "FIRM_KV")
    private String firmKv;

    @Column(name = "FIRM_KPP")
    @DbfField("KPP")
    private String firmKpp;

    @Column(name = "FIRM_NAME_ALL")
    @DbfField("FIRM_NAL")
    private String firmNameAll;

    @Column(name = "FIRM_ADR_RP")
    @DbfField("ADR_RP")
    private String firmAdrPr;

    @Column(name = "FIRM_ADR_EP")
    @DbfField("ADR_EP")
    private String firmAdrEp;

    @Column(name = "FIRM_STATUS")
    @DbfField("STATUS")
    private Integer firmStatus;

    @Column(name = "FIRM_KOD_GP")
    @DbfField("KOD_GP")
    private Integer firmKodGp;

    @Column(name = "FIRM_OGRN")
    @DbfField("OGRN")
    private String ogrn;

    @Column(name = "FIRM_PR_VZL")
    @DbfField("PR_VZL")
    private Integer prVzl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Firm)) return false;
        Firm firm = (Firm) o;
        return Objects.equals(getFirmId(), firm.getFirmId()) && Objects.equals(getCityId(), firm.getCityId()) && Objects.equals(getCaId(), firm.getCaId()) && Objects.equals(getFirmNameR(), firm.getFirmNameR()) && Objects.equals(getFirmNameE(), firm.getFirmNameE()) && Objects.equals(getFirmInd(), firm.getFirmInd()) && Objects.equals(getFirmUnnInn(), firm.getFirmUnnInn()) && Objects.equals(getFirmAdr(), firm.getFirmAdr()) && Objects.equals(getFirmAdrE(), firm.getFirmAdrE()) && Objects.equals(getFirmOkpo(), firm.getFirmOkpo()) && Objects.equals(getFirmOkonh(), firm.getFirmOkonh()) && Objects.equals(getFirmSp(), firm.getFirmSp()) && Objects.equals(getFirmNp(), firm.getFirmNp()) && Objects.equals(getFirmLnp(), firm.getFirmLnp()) && Objects.equals(getFirmDvp(), firm.getFirmDvp()) && Objects.equals(getFirmKv(), firm.getFirmKv()) && Objects.equals(getFirmKpp(), firm.getFirmKpp()) && Objects.equals(getFirmNameAll(), firm.getFirmNameAll()) && Objects.equals(getFirmAdrPr(), firm.getFirmAdrPr()) && Objects.equals(getFirmAdrEp(), firm.getFirmAdrEp()) && Objects.equals(getFirmStatus(), firm.getFirmStatus()) && Objects.equals(getFirmKodGp(), firm.getFirmKodGp()) && Objects.equals(getOgrn(), firm.getOgrn()) && Objects.equals(getPrVzl(), firm.getPrVzl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirmId(), getCityId(), getCaId(), getFirmNameR(), getFirmNameE(), getFirmInd(), getFirmUnnInn(), getFirmAdr(), getFirmAdrE(), getFirmOkpo(), getFirmOkonh(), getFirmSp(), getFirmNp(), getFirmLnp(), getFirmDvp(), getFirmKv(), getFirmKpp(), getFirmNameAll(), getFirmAdrPr(), getFirmAdrEp(), getFirmStatus(), getFirmKodGp(), getOgrn(), getPrVzl());
    }
}
