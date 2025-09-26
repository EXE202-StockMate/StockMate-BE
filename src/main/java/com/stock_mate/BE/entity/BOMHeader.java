package com.stock_mate.BE.entity;

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
@Table(name = "BOMHeader")
public class BOMHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int headerID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fgID")
    FinishProduct finishProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sfgID")
    SemiFinishProduct semiFinishProduct;

    @Column(columnDefinition = "TEXT")
    String note;

    @OneToMany(mappedBy = "bomHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    List<BOMItem> items;
}
