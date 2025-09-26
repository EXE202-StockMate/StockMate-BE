package com.stock_mate.BE.entity;

import com.stock_mate.BE.enums.MaterialType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Table(name = "BOMItem")
public class BOMItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int itemID;

    @ManyToOne
    @JoinColumn(name = "headerID")
    BOMHeader bomHeader;

    @Enumerated(EnumType.STRING)
    MaterialType materialType;

    //chỉ 1 trong 2 sẽ có giá trị
    @ManyToOne
    @JoinColumn(name = "rmID")
    RawMaterial rawMaterial;

    @ManyToOne
    @JoinColumn(name = "sfgID")
    SemiFinishProduct semiFinishProduct;

    int quantity;
    String unit;
    String note;

}
