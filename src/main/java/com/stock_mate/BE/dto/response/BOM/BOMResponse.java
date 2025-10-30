package com.stock_mate.BE.dto.response.BOM;

import com.stock_mate.BE.dto.response.FinishProductResponse;

import java.time.LocalDate;
import java.util.List;

public record BOMResponse(
        String headerID,
        FinishProductResponse finishProduct,
        LocalDate createdDate,
        LocalDate updateDate,
        List<BOMItemResponse> items
) {}
