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

    @OneToMany(mappedBy = "rawMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    List<RawMaterialMedia> mediaList;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDate.now();
        this.updateDate = LocalDate.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDate.now();
    }

    @PostLoad
    public void setImage() {
        if (mediaList == null || mediaList.isEmpty()) {
            this.image = null;
            return;
        }
        this.image = mediaList.get(0).getMediaUrl();
    }
}
