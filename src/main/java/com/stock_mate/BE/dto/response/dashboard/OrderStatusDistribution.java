package com.stock_mate.BE.dto.response.dashboard;

import com.stock_mate.BE.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatusDistribution {
    OrderStatus status;
    Long count;
}
