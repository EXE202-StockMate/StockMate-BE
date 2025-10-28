package com.stock_mate.BE.dto.request;

import com.stock_mate.BE.enums.RawMaterialCategory;

public record RawMaterialUpdateRequest(
        String rmID,
        String name,
        String code,
        String description,
        RawMaterialCategory category,
        String dimension,
        Integer thickness,
        Integer status
) {

}
