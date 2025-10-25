package com.stock_mate.BE.dto.request;

import com.stock_mate.BE.enums.RawMaterialCategory;

public record RawMaterialRequest(
        String name,
        String code,
        String description,
        RawMaterialCategory category,
        String dimension,
        Integer thickness
) {}
