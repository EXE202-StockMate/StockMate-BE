package com.stock_mate.BE.dto.request;

import com.stock_mate.BE.enums.FinishProductCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinishProductRequest {
    String name;
    String description;
    FinishProductCategory category;
    String dimension;
}
