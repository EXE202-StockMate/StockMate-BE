package com.stock_mate.BE.dto.response.dashboard;

public record DashboardSummaryResponse(
    Long totalOrders,
    Long totalFinishedProducts,
    Long totalRawMaterials,
    Long totalShortages
) {}
