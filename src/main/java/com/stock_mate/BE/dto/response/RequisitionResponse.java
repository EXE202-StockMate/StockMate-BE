package com.stock_mate.BE.dto.response;

import com.stock_mate.BE.enums.MaterialType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RequisitionResponse {
    String requisitionID;
    MaterialType type;
    int quantity;
    String unit; //ký, cái, cuộn
    LocalDate createDate;
    LocalDate updateDate;
    String note;
    String userID;
    RawMaterialResponse rawMaterial;
    SemiFinishProductResponse semiFinishProduct;
    FinishProductResponse finishProduct;
    OrderResponse order;
    ShortageResponse shortage;

}
