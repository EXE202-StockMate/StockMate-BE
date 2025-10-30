package com.stock_mate.BE.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stock_mate.BE.entity.Order;
import com.stock_mate.BE.entity.RawMaterial;
import com.stock_mate.BE.entity.SemiFinishProduct;
import com.stock_mate.BE.enums.MaterialType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ShortageResponse {
    int shortageID;
    String orderID;
    MaterialType materialType;
    String rmID;
    String sfgID;
    Integer requiredQuantity;
    Integer availableQuantity;
    Integer shortageQuantity;
    BigDecimal shortagePercentage;
    String unit;
    String note;
    LocalDate createDate;
    LocalDate updateDate;
}
