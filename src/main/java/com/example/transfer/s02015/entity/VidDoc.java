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
@Table(name = "vid_doc")
@DbfSource(value = "VID_DOC.DBF", useLocalCache = true)
@MigrationOrder(6)
public class VidDoc {

    @Id
    @Column(name = "vdoc_id")
    @DbfField("ID_DOC")
    private Long vdocId;

    @Column(name = "vdoc_naim")
    @DbfField("NAIM_DOC")
    private String vdocNaim;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VidDoc vidDoc = (VidDoc) o;
        return Objects.equals(vdocId, vidDoc.vdocId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vdocId);
    }
}