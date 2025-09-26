package com.stock_mate.BE.entity;

import com.stock_mate.BE.enums.RawMaterialCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Table(name = "RawMaterial")
public class RawMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String rmID;

    String code;
    String name;

    @Column(columnDefinition = "TEXT")
    String description;
    String image;

    @Enumerated(EnumType.STRING)
    RawMaterialCategory category;
    String dimension;

    // For MDF boards - thickness in mm
    Integer thickness;

    LocalDate createDate;
    LocalDate updateDate;
    int status;

    @OneToMany(mappedBy = "rawMaterial")
    List<Stock> stocks;

}
