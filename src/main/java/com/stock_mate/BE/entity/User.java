package com.stock_mate.BE.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String userID;

    @Column(unique = true, nullable = false)
    String username;

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

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDate.now();
        this.updateDate = LocalDate.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDate.now();
    }
}
