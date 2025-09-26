package com.stock_mate.BE.entity;

import com.stock_mate.BE.enums.UserStatus;
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
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String userID;

    String fullName;

    @Column(length = 10)
    String phoneNumber;

    String email;

    String password;

    String image;
    LocalDate createDate;
    LocalDate updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleName")
    Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    UserStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managerID")
    User manager;
}
