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
@Table(name = "BANK")
@DbfSource(value = "FIRM.DBF", useLocalCache = true)
@MigrationOrder(4)
public class Bank {

    @Id
    @Column(name = "BANK_ID")
    @DbfField("FIRM_KOD")
    @NotNull(message = "(Oracle: BANK_ID) не может быть null")
    private Long bankId;

    @Column(name = "FIRM_ID")
    @DbfField("FIRM_KOD")
    private Long firmId;

    @Column(name = "CUR_KOD")
    @DefaultValue("906")
    private Long curKod;

    @Column(name = "BANK_RSCH")
    @DbfField("RSCH")
    private String rsch;

    @Column(name = "BANK_MFO")
    @DbfField("MFO")
    private String mfo;

    @Column(name = "BANK_NAME_R")
    @DbfField("BANK_R")
    private String bankR;

    @Column(name = "BANK_NAME_E")
    @DbfField("BANK_E")
    private String bankE;

    @Column(name = "BANK_ADR_R")
    @DbfField("ADRB_R")
    private String adrR;

    @Column(name = "BANK_ADR_E")
    @DbfField("ADRB_E")
    private String adrE;

    @Column(name = "BANK_RSCH_B")
    @DbfField("RSCHB")
    private String rschb;

    @Column(name = "BANK_K_R")
    @DbfField("BANKK_R")
    private String bankKR;

    @Column(name = "BANK_K_E")
    @DbfField("BANKK_E")
    private String bankKE;

    @Column(name = "BANK_ADRBK_R")
    @DbfField("ADRBK_R")
    private String adrbkR;

    @Column(name = "BANK_ADRBK_E")
    @DbfField("ADRBK_E")
    private String adrbkE;

    @Column(name = "BANK_BLZ")
    @DbfField("BLZ")
    private String blz;

    @Column(name = "BANK_SWIFT")
    @DbfField("SWIFT")
    private String swift;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bank)) return false;
        Bank bank = (Bank) o;
        return Objects.equals(getBankId(), bank.getBankId()) && Objects.equals(getFirmId(), bank.getFirmId()) && Objects.equals(getCurKod(), bank.getCurKod()) && Objects.equals(getRsch(), bank.getRsch()) && Objects.equals(getMfo(), bank.getMfo()) && Objects.equals(getBankR(), bank.getBankR()) && Objects.equals(getBankE(), bank.getBankE()) && Objects.equals(getAdrR(), bank.getAdrR()) && Objects.equals(getAdrE(), bank.getAdrE()) && Objects.equals(getRschb(), bank.getRschb()) && Objects.equals(getBankKR(), bank.getBankKR()) && Objects.equals(getBankKE(), bank.getBankKE()) && Objects.equals(getAdrbkR(), bank.getAdrbkR()) && Objects.equals(getAdrbkE(), bank.getAdrbkE()) && Objects.equals(getBlz(), bank.getBlz()) && Objects.equals(getSwift(), bank.getSwift());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBankId(), getFirmId(), getCurKod(), getRsch(), getMfo(), getBankR(), getBankE(), getAdrR(), getAdrE(), getRschb(), getBankKR(), getBankKE(), getAdrbkR(), getAdrbkE(), getBlz(), getSwift());
    }
}
