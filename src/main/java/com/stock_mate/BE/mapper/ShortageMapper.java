package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.ShortageResponse;
import com.stock_mate.BE.entity.Shortage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShortageMapper {
    ShortageResponse toDto(Shortage shortage);
}
