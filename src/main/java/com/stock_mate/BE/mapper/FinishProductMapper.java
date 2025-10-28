package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.request.FinishProductRequest;
import com.stock_mate.BE.dto.response.FinishProductResponse;
import com.stock_mate.BE.dto.response.RawMaterialResponse;
import com.stock_mate.BE.entity.FinishProduct;
import com.stock_mate.BE.entity.RawMaterial;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FinishProductMapper {
    FinishProductResponse toDto(FinishProduct finishProduct);
    FinishProduct toEntity(FinishProductRequest request);
}
