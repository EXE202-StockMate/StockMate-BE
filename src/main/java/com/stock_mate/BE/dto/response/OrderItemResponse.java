package com.stock_mate.BE.dto.response;

import com.stock_mate.BE.entity.FinishProduct;
import com.stock_mate.BE.entity.Order;
import jakarta.persistence.*;

public record OrderItemResponse(
        int itemID,
        String materialID,
        int quantity,
        FinishProduct finishProduct
) {
}
