package com.stock_mate.BE.dto.request;

import com.stock_mate.BE.enums.MaterialType;

public record RequisitionRequest(
    String materialID,
    MaterialType type,
    String unit,
    Integer quantity,
    String note
) {
}
