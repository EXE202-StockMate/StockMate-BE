package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.ShortageResponse;
import com.stock_mate.BE.entity.Shortage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RawMaterialMapper.class})
public interface ShortageMapper {

    @Mapping(target = "orderID", expression = "java(shortage.getOrder() != null ? shortage.getOrder().getOrderID() : null)")
    ShortageResponse toDto(Shortage shortage);
}
