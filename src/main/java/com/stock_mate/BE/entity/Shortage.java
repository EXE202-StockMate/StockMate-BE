package com.stock_mate.BE.entity;

import com.stock_mate.BE.enums.MaterialType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.logging.Level;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Table(name = "Shortage")
public class Shortage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int shortageID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderID")
    Order order;

    @Enumerated(EnumType.STRING)
    MaterialType materialType;

    //Chỉ 1 trong 2 rawMaterial & semiFinishProduct mới có value
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rmID")
    RawMaterial rawMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sfgID")
    SemiFinishProduct semiFinishProduct;

    // Số lượng cần thiết cho đơn hàng
    @Column(nullable = false)
    Integer requiredQuantity;

    // Số lượng hiện có trong kho
    @Column(nullable = false)
    Integer availableQuantity;

    // Số lượng thiếu
    @Column(nullable = false)
    Integer shortageQuantity;

    // Phần trăm thiếu (tính theo loại nguyên liệu)
    @Column(precision = 5, scale = 2)
    BigDecimal shortagePercentage;

    String unit;

    // Ghi chú
    @Column(columnDefinition = "TEXT")
    String note;

    @Column(nullable = false)
    LocalDate createDate;
    LocalDate updateDate;

    @PrePersist
    protected void onCreate() {
        createDate = LocalDate.now();

    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDate.now();
    }

    //Tính sai số/hao phí
    private void calculateShortage() {
        if(requiredQuantity != null && availableQuantity != null) {
            shortageQuantity = Math.max(0, requiredQuantity - availableQuantity);

            if(requiredQuantity > 0){
                shortagePercentage = BigDecimal.valueOf(shortageQuantity * 100 / requiredQuantity)
                        .setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }
    }

    public String getMaterialName(){
        return materialType == MaterialType.RAW_MATERIAL ?
                rawMaterial.getName() : semiFinishProduct.getName();
    }

    public String getMaterialID(){
        return materialType == MaterialType.RAW_MATERIAL ?
                rawMaterial.getRmID() : semiFinishProduct.getSfgID();
    }

    public boolean isShortage(){
        return shortageQuantity != null && shortageQuantity > 0;
    }

}
