package com.stock_mate.BE.dto.response;

import java.time.LocalDate;

public record RequistionResponse(
   String type,
   int quantity,
   String unit,
   LocalDate createDate,
    LocalDate updateDate,
   String note,
   UserResponse user,
   OrderResponse order,
   ShortageResponse shortage
) {
}
