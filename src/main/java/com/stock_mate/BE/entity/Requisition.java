package com.stock_mate.BE.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "Requisition")
public class Requisition {
    @Id
    String requistionID;
    String type;
    int quantity;
    String unit; //ký, cái, cuộn
    LocalDate createDate;
    LocalDate updateDate;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", length = 20)
//    RequistionStatus status;

    @Column(columnDefinition = "TEXT")
    String note;

    @ManyToOne
    @JoinColumn(name = "user")
    User user;

    String materialID;

    @ManyToOne
    @JoinColumn(name = "orderID")
    @JsonIgnore
    Order order;

    @ManyToOne
    @JoinColumn(name = "shortageID")
    Shortage shortage;

}
