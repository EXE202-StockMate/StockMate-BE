package com.stock_mate.BE.dto.request;

public record RequisitionRequest(
    String type,
    String unit,
    Integer quantity,
    String note
) {
}
