package com.stock_mate.BE.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stock_mate.BE.enums.FinishProductCategory;

import java.time.LocalDate;

public record FinishProductResponse(
        String fgID,
        String name,
        String description,
        String image,
        FinishProductCategory category,
        String dimension,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate createDate,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate updateDate,
        int status,
        FinishProductMediaResponse[] mediaList
) {
}
