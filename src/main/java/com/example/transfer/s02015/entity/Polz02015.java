package com.example.transfer.s02015.entity;

import com.example.transfer.dbf.annotation.CharToNumeric;
import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;
import com.example.transfer.s02015.annotation.DepId;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "polz_02015")
@DbfSource(value = "PPP.DBF", useLocalCache = true)
public class Polz02015 {

    @Id
    @Column(name = "polz_id")
    @DbfField("P_KUSER")
    @CharToNumeric
    @NotNull(message = "(Oracle: polz_id) не может быть null")
    private Long polzId;

    @Column(name = "polz_name")
    @DbfField("P_FAM")
    @NotNull(message = "(Oracle: polz_name) не может быть null")
    private String polzName;

    @Column(name = "polz_fun")
    @DbfField("P_FUN")
    @NotNull(message = "(Oracle: polz_fun) не может быть null")
    private Boolean polzFun;

    @Column(name = "dep_id")
    @DepId(sourceField="dogSluzba", username="S11000", password = "S11000")
    private Integer depId;

    @DbfField("P_PODR")
    @Transient
    private String dogSluzba;

    @Column(name = "polz_adm")
    @DbfField("P_ADM")
    @NotNull(message = "(Oracle: polz_adm) не может быть null")
    private Boolean polzAdm;

    @Column(name = "polz_exit")
    @DbfField("P_EXIT")
    private Boolean polzExit;

    @Column(name = "polz_exp")
    @DbfField("P_EXP")
    private Boolean polzExp;

    @Column(name = "polz_urb")
    @DbfField("P_URB")
    private Boolean polzUrb;

    @Column(name = "id_user", insertable = false, updatable = false)
    private Long idUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Polz02015 polz02015 = (Polz02015) o;
        return Objects.equals(polzId, polz02015.polzId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(polzId);
    }
}