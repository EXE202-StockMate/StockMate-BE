package com.stock_mate.BE.dto.request;

import jakarta.validation.constraints.AssertTrue;

public record StockRequest(
    String rmID,
    String fgID,
    String sfgID,
    String stockName,
    int quantity,
    String unit,
    String image,
    String note
    //đưa dung ve enum
    //StockStatus status
) {}
