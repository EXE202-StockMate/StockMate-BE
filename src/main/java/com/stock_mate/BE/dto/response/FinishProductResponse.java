package com.stock_mate.BE.dto.response;

import com.stock_mate.BE.enums.FinishProductCategory;

public record FinishProductResponse(
        String fgID,
        String name,
        String description,
        String image,
        FinishProductCategory category,
        String dimension,
        String createDate,
        String updateDate,
        int status,
        FinishProductMediaResponse[] mediaList
) {
}
