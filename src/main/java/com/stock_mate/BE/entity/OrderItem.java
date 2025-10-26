package com.stock_mate.BE.entity;

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
    Order order;

    String materialID;
    int quantity;

    @OneToOne
    FinishProduct finishProduct;
}
