package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.RequisitionResponse;
import com.stock_mate.BE.entity.Requisition;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {
        UserMapper.class,
        OrderMapper.class,
        ShortageMapper.class})
public interface RequisitionMapper {

    RequisitionResponse toDto(Requisition requisition);
}
