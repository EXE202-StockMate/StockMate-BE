package com.stock_mate.BE.entity;

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
@Table(name = "SemiFinishProduct")
public class SemiFinishProduct {
    @Id
    String sfgID;
    String name;
    String description;
    String image;
    String dimension;
    LocalDate createDate;
    LocalDate updateDate;
    int status;

    @OneToMany(mappedBy = "semiFinishProduct")
    List<Stock> stocks;
}
