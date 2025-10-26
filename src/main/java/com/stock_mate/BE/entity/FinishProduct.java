package com.stock_mate.BE.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    String fgID;
    String name;
    String description;
    String image;
    @Enumerated(EnumType.STRING)
    FinishProductCategory category;
    String dimension;
    LocalDate createDate;
    LocalDate updateDate;
    int status;

    @OneToMany(mappedBy = "finishProduct")
    List<Stock> stocks;

    @OneToMany(mappedBy = "finishProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<FinishProductMedia> mediaList;

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
