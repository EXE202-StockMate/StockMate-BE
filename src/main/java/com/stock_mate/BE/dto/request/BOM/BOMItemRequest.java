package com.stock_mate.BE.dto.request.BOM;

import com.stock_mate.BE.enums.MaterialType;

public record BOMItemRequest(
        MaterialType materialType,
        String materialId, // rawMaterialId hoac semiFinishProductId tuy theo materialType
        int quantity,
        String unit
) {}
