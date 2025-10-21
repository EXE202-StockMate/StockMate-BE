package com.stock_mate.BE.dto.response;

public record SemiFinishProductResponse(
        String sfgID,
        String code,
        String name,
        String description,
        String image,
        String category,
        String dimension,
        Integer thickness,
        String createDate,
        String updateDate,
        int status
        //SemiFinishProductMediaResponse[] mediaList
) {
}
