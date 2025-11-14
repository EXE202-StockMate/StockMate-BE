package com.stock_mate.BE.dto.request;

import com.stock_mate.BE.dto.response.OrderResponse;
import com.stock_mate.BE.entity.FinishProduct;
import com.stock_mate.BE.entity.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public record OrderItemResquest(
        int quantity,
        String fgID
) {
}
