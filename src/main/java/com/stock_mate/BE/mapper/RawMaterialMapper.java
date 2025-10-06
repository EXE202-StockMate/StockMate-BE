package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.RawMaterialResponse;
import com.stock_mate.BE.entity.RawMaterial;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RawMaterialMapper {

    RawMaterialResponse toDto(RawMaterial rawMaterial);
}
