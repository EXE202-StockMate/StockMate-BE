package com.stock_mate.BE.dto.request;

import com.stock_mate.BE.enums.FinishProductCategory;

public record FinishProductUpdateRequest(
        String fgID,
        String name,
        String description,
        FinishProductCategory category,
        String dimension,
        Integer status
) {
}
