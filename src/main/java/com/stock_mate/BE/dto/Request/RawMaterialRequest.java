package com.stock_mate.BE.dto.request;

import com.stock_mate.BE.enums.RawMaterialCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawMaterialRequest {
    String name;
    String code;
    String description;
    RawMaterialCategory category;
    String dimension;
    Integer thickness;
}
