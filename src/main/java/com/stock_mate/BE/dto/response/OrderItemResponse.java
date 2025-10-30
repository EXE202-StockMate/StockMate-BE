package com.stock_mate.BE.dto.response;


public record OrderItemResponse(
        int itemID,
        String materialID,
        int quantity,
        FinishProductResponse finishProduct
) {
}
