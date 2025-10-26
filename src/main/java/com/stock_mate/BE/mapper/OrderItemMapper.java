package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.OrderItemResponse;
import com.stock_mate.BE.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
