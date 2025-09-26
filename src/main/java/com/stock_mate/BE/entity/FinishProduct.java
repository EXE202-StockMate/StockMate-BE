package com.stock_mate.BE.entity;

import com.stock_mate.BE.enums.FinishProductCategory;
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
@Table(name = "FinishProduct")
public class FinishProduct {
    @Id
    String fgID;
    String name;
    String description;
    String image;
    FinishProductCategory category;
    String dimension;
    LocalDate createDate;
    LocalDate updateDate;
    int status;

    @OneToMany(mappedBy = "finishProduct")
    List<Stock> stocks;
}
