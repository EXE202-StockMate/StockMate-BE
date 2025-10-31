package com.stock_mate.BE.dto.request.BOM;

import java.util.List;

public record BOMUpdateRequest(
    List<BOMItemRequest> items
) {
}
