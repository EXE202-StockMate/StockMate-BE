package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.request.RawMaterialRequest;
import com.stock_mate.BE.dto.response.RawMaterialMediaResponse;
import com.stock_mate.BE.dto.response.RawMaterialResponse;
import com.stock_mate.BE.entity.RawMaterial;
import com.stock_mate.BE.entity.RawMaterialMedia;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RawMaterialMapper {

    RawMaterialResponse toDto(RawMaterial rawMaterial);

    RawMaterial toEntity(RawMaterialRequest request);

    List<RawMaterialMediaResponse> toMediaDtoList(List<RawMaterialMedia> savedMedia);
}
