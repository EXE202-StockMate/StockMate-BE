package com.stock_mate.BE.dto.response;

public record RawMaterialMediaResponse(
        Long id,
        String mediaUrl,
        String mediaType,
        String description,
        String publicId,
        String createDate
) {
}
