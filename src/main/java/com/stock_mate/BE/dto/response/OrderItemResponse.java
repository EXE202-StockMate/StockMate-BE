package com.stock_mate.BE.dto.response;


public record OrderItemResponse(
        int itemID,
        int quantity,
        FinishProductResponse finishProduct
) {
}
