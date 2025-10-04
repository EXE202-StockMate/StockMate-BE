package com.stock_mate.BE.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

public record RawMaterialResponse(
        String rmID,
        String code,
        String name,
        String description,
        String image,
        String category,
        String dimension,
        Integer thickness,
        @JsonFormat(pattern = "dd-MM-yyyy")
        String createDate,
        @JsonFormat(pattern = "dd-MM-yyyy")
        String updateDate,
        int status,
        RawMaterialMediaResponse[] mediaList) {
}
