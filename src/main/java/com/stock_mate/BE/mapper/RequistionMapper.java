package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.RequistionResponse;
import com.stock_mate.BE.entity.Requistion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequistionMapper {

    @Mapping(source = "user.userID", target = "userID")
    @Mapping(source = "order.orderID", target = "orderID")
    @Mapping(source = "shortage.shortageID", target = "shortageID")
    RequistionResponse toDto(Requistion requistion);
}
