package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.RequistionResponse;
import com.stock_mate.BE.entity.Requistion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        UserMapper.class,
        OrderMapper.class,
        ShortageMapper.class})
public interface RequistionMapper {

    RequistionResponse toDto(Requistion requistion);
}
