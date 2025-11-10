package com.stock_mate.BE.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Table(name = "OrderItem")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int itemID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderID")
    @JsonIgnore
    Order order;

    int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fgID")
    FinishProduct finishProduct;
}
