package com.stock_mate.BE.dto.response.dashboard;

import com.stock_mate.BE.enums.RawMaterialCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RawMaterialCategoryDistribution {
    RawMaterialCategory category;
    Long count;
}
