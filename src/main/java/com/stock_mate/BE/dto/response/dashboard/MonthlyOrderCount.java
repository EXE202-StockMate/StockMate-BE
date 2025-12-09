package com.stock_mate.BE.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

public interface MonthlyOrderCount {
    Integer getMonth();
    Long getTotalOrders();
}
