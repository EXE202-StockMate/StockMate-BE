package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.BOM.*;
import com.stock_mate.BE.entity.BOMHeader;
import com.stock_mate.BE.entity.BOMItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BOMMapper {

    @Mapping(target = "headerID", source = "headerID")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "finishProduct", source = "finishProduct")
    BOMResponse toDto(BOMHeader bomHeader);

    @Mapping(target = "itemID", source = "itemID")
    @Mapping(target = "materialType", source = "materialType")
    @Mapping(target = "rawMaterial", source = "rawMaterial")
    @Mapping(target = "semiFinishProduct", source = "semiFinishProduct")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "note", source = "note")
    BOMItemResponse toItemDto(BOMItem bomItem);
}
