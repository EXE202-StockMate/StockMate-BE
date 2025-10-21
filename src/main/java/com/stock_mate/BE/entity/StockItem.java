package com.stock_mate.BE.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Table(name = "StockItem")
public class StockItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long stockItemID;

    @ManyToOne
    @JoinColumn(name = "stockID")
    Stock stock;

    @ManyToOne
    @JoinColumn(name = "rmID")
    RawMaterial rawMaterial;

    @ManyToOne
    @JoinColumn(name = "fgID")
    FinishProduct finishProduct;

    @ManyToOne
    @JoinColumn(name = "sfgID")
    SemiFinishProduct semiFinishProduct;

    int quantity;

    @Column(length = 10)
    String type; //IMPORT or EXPORT

    LocalDate createDate;
    LocalDate updateDate;

    String note;
    @ManyToOne
    @JoinColumn(name = "user")
    User user;
}
