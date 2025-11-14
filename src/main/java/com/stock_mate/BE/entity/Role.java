package com.stock_mate.BE.entity;

import com.stock_mate.BE.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Table(name = "Role")
public class Role {
    @Id
    @Column(length = 20)
    String name;

    @Column(columnDefinition = "TEXT")
    String description;

//    @ManyToMany(fetch = FetchType.EAGER)
//    Set<Permission> permissions;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "Role_Permission",
            joinColumns = @JoinColumn(name = "role_name"),
            inverseJoinColumns = @JoinColumn(name = "permission_name"),
            foreignKey = @ForeignKey(name = "fk_role_permission_role"),
            inverseForeignKey = @ForeignKey(name = "fk_role_permission_permission")
    )
    Set<Permission> permissions = new java.util.HashSet<>();
}
