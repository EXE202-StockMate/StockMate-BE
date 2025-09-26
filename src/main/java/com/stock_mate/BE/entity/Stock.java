package com.stock_mate.BE.entity;

import com.stock_mate.BE.enums.StockStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Table(name = "Stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int stockID;

    String stockName;
    String image;

    @ManyToOne
    @JoinColumn(name = "rmID")
    RawMaterial rawMaterial; // Reference đến RawMaterial

    @ManyToOne
    @JoinColumn(name = "fgID")
    FinishProduct finishProduct;

    @ManyToOne
    @JoinColumn(name = "sfgID")
    SemiFinishProduct semiFinishProduct;

    int quantity;

    String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    StockStatus status;

}
