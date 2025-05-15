package com.example.transfer.s02015.entity;

import com.example.transfer.dbf.annotation.*;
import com.example.transfer.dbf.annotation.DefaultValue;
import com.example.transfer.s02015.annotation.NaimIdLookup;
import com.example.transfer.s02015.annotation.PackIdLookup;
import com.example.transfer.s02015.annotation.RoundTo;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "packing")
@DbfSource(value = "PACKING.DBF", useLocalCache = true)
@StartSql("UPDATE \"PACKING\" SET \"PACK_OPERABLE\" = 0")
public class Packing {

    @Id
    @Column(name = "pack_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_s02015_spr_gen")
    @SequenceGenerator(
            name = "seq_s02015_spr_gen",
            sequenceName = "seq_s02015_spr",
            allocationSize = 1
    )
    @PackIdLookup
    private Long packId;

    @Column(name = "dog_id")
    @DbfField("D_KD")
    @NotNull(message = "(Oracle: dog_id) Номер договора не может быть null")
    private Long dogId;

    @Column(name = "specif_id")
    @DbfField("S_ID")
    @NullChanger
    @NotNull(message = "(Oracle: specif_id) Номер спецификации не может быть null")
    private Long specifId;

    @Column(name = "kod_group")
    @DbfField("KOD1")
    @NotNull(message = "(Oracle: kod_group) не может быть null")
    private String kodGroup;

    @Column(name = "kod_subgr")
    @DbfField("KOD3")
    @NotNull(message = "(Oracle: kod_subgr) не может быть null")
    private String kodSubgr;

    @Column(name = "kodm")
    @DbfField("KODM")
    @NotNull(message = "(Oracle: kodm) не может быть null")
    private String kodm;

    @Column(name = "pack_dat_reg")
    @DbfField("DAT_REG")
    @NotNull(message = "(Oracle: pack_dat_reg) не может быть null")
    private Date packDatReg;

    @Column(name = "pack_dat_ed")
    @DbfField("DAT_ED")
    private Date packDatEd;

    @Column(name = "kol_unit")
    @DbfField("KOL_UNIT")
    @NotNull(message = "(Oracle: kol_unit) не может быть null")
    private Long kolUnit;

    @Column(name = "pack_isp")
    @DbfField("ISP")
    @CharToNumeric
    @NotNull(message = "(Oracle: pack_isp) не может быть null")
    private Long packIsp;

    @Column(name = "ves_unit")
    @DbfField("VES_UNIT")
    @RoundTo(6)
    @NotNull(message = "(Oracle: ves_unit) не может быть null")
    private Double vesUnit;

    @Column(name = "pack_operable")
    @DefaultValue("1")
    private Integer packOperable;

    @Column(name = "naim_id")
    @NaimIdLookup(sourceField = "kodm", username = "S16071", password = "S16071")
    private Long naimId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Packing packing = (Packing) o;
        return Objects.equals(packId, packing.packId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packId);
    }

}