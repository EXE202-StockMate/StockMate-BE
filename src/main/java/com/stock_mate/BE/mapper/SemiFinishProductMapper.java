package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.SemiFinishProductResponse;
import com.stock_mate.BE.entity.SemiFinishProduct;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SemiFinishProductMapper {
    SemiFinishProductResponse toDto(SemiFinishProduct semiFinishProduct);
}
