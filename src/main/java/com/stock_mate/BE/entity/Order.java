package com.stock_mate.BE.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Orders")  // Tránh keyword "order"
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String orderID;
    LocalDate createDate;
    LocalDate updateDate;

    String code; //stt của đơn hàng, tính theo từng khách hàng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerID")
    Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Requistion> requistions;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderItem> orderItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Shortage> shortages;
}
