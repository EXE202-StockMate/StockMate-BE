package com.stock_mate.BE.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Table(name = "FinishProductMedia")
public class FinishProductMedia {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "finish_product_id", nullable = false)
    @JsonBackReference
    private FinishProduct finishProduct;

    @Column(nullable = false)
    private String mediaUrl;

    @Column(nullable = false)
    private String mediaType; // "IMAGE" or "VIDEO"

    private String description;

    @Column(name = "public_id")
    private String publicId; // Cloudinary public ID for deletion

    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }

}
