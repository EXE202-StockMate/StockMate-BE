package com.stock_mate.BE.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

public record FinishProductMediaResponse(
        Long id,
        String mediaUrl,
        String mediaType,
        String description,
        String publicId,
        @JsonFormat(pattern = "dd-MM-yyyy")
        String createDate
) {
}
