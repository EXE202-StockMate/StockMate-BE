package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.ShortageResponse;
import com.stock_mate.BE.entity.Shortage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShortageMapper {

    @Mapping(source = "order.orderID", target = "orderID")
    @Mapping(source = "rawMaterial.rmID", target = "rmID")
    @Mapping(source = "semiFinishProduct.sfgID", target = "sfgID")
    ShortageResponse toDto(Shortage shortage);
}
