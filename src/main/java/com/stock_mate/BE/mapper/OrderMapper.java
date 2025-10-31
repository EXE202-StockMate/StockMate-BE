package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.OrderResponse;
import com.stock_mate.BE.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {
                OrderItemMapper.class,
                ShortageMapper.class,
                RequisitionMapper.class
        })
public interface OrderMapper {
    @Mapping(source = "user.userID", target = "userID")
    @Mapping(source = "customer.customerID", target = "customerID")
    OrderResponse toOrderResponse(Order order);
}
