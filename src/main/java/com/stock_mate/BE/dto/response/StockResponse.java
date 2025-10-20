package com.stock_mate.BE.dto.response;

public record StockResponse(
        int stockID,
        String stockName,
        String image,
        String rmID,
        String fgID,
        String sfgID,
        int quantity,
        String unit,
        String status
) {}
