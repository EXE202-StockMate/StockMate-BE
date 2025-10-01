package com.stock_mate.BE.dto.response;

public record RawMaterialResponse(
        String rmID,
        String code,
        String name,
        String description,
        String image,
        String category,
        String dimension,
        Integer thickness,
        String createDate,
        String updateDate,
        int status,
        RawMaterialMediaResponse[] mediaList) {
}
