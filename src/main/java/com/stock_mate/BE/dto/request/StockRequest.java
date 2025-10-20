package com.stock_mate.BE.dto.request;

import com.stock_mate.BE.enums.StockStatus;

public record StockRequest(
    String rmID,
    String fgID,
    String sfgID,
    String stockName,
    int quantity,
    String unit,
    String image,
    String note,
    //đưa dung ve enum
    StockStatus status
) {}
