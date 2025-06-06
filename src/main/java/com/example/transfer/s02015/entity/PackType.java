package com.example.transfer.s02015.entity;

import com.example.transfer.dbf.annotation.DbfField;
import com.example.transfer.dbf.annotation.DbfSource;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "pack_type")
@DbfSource(value = "TIP_PACK.DBF", useLocalCache = true)
public class PackType {

    @Id
    @Column(name = "tpack_id")
    @DbfField("PACK_ID")
    private Long tpackId;

    @Column(name = "tpack_name")
    @DbfField("PACK_NAIM")
    private String tpackName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackType packType = (PackType) o;
        return Objects.equals(tpackId, packType.tpackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tpackId);
    }
}
