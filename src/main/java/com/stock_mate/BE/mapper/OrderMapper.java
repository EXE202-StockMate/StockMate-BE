package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.OrderResponse;
import com.stock_mate.BE.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toOrderResponse(Order order);
}
