package com.stock_mate.BE.dto.response.dashboard;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MonthlyOrderResponse {
    Integer year;
    List<MonthlyOrderCount> charts;
}
