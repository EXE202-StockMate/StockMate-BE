package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.StockItemResponse;
import com.stock_mate.BE.entity.StockItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {RawMaterialMapper.class, FinishProductMapper.class, SemiFinishProductMapper.class, UserMapper.class})

public interface StockItemMapper {

//    @Mapping(source = "rawMaterial", target = "rawMaterial")
//    @Mapping(source = "finishProduct", target = "finishProduct")
//    @Mapping(source = "semiFinishProduct", target = "semiFinishProduct")
//    @Mapping(source = "user", target = "user")
    StockItemResponse toDto(StockItem stockItem);
}
