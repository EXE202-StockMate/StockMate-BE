package com.stock_mate.BE.dto.response;

public record StockItemResponse(
        int stockItemID,
        int stockID,
//        String rmID,
//        String fgID,
//        String sfgID,
        RawMaterialResponse rawMaterial,
        FinishProductResponse finishProduct,
        SemiFinishProductResponse semiFinishProduct,
        int quantity,
        String type,
        String createDate,
        String updateDate,
        String note,
        UserResponse user
) {
}
