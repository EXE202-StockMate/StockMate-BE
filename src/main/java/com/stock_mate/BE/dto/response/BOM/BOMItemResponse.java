package com.stock_mate.BE.dto.response.BOM;

import com.stock_mate.BE.dto.response.RawMaterialResponse;
import com.stock_mate.BE.dto.response.SemiFinishProductResponse;
import com.stock_mate.BE.enums.MaterialType;

public record BOMItemResponse(
        int itemID,
        MaterialType materialType,
        RawMaterialResponse rawMaterial,
        SemiFinishProductResponse semiFinishProduct,
        int quantity,
        String unit,
        String note

) {
}
