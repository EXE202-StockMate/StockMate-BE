package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.dto.response.dashboard.DashboardSummaryResponse;
import com.stock_mate.BE.service.DashboardService;
import com.stock_mate.BE.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/dashboard")
@Tag(name = "Dashboard", description ="API Thống kê Dashboard")
public class DashboardV1Controller {

    @Autowired
    DashboardService dashboardService;

    @Operation(summary = "Thông tin tổng quan dashboard")
    @GetMapping("/summary")
    public ResponseObject<DashboardSummaryResponse> getDashboardSummary() {
        return ResponseObject.<DashboardSummaryResponse>builder()
                .status(1000)
                .message("Lấy thông tin tổng quan dashboard thành công")
                .data(dashboardService.getTotalStatistics())
                .build();
    }

    @Operation(summary = "Biểu đồ tròn phân bố trạng thái đơn hàng")
    @GetMapping("orders/status-distribution")
    public ResponseObject<?> getOrderStatusDistribution() {
        return ResponseObject.builder()
                .status(1000)
                .message("Lấy biểu đồ tròn phân bố trạng thái đơn hàng thành công")
                .data(dashboardService.getOrderStatusDistribution())
                .build();
    }

    @Operation(summary = "Biểu đồ tròn thống kê loại số liệu được xuất")
    @GetMapping("raw-materials/quantity-distribution")
    public ResponseObject<?> getRawMaterialQuantityDistribution() {
        // Giả sử dashboardService có phương thức getRawMaterialQuantityDistribution
        return ResponseObject.builder()
                .status(1000)
                .message("Lấy biểu đồ tròn thống kê loại số liệu được xuất thành công")
                .data(dashboardService.getRawMaterialQuantityDistribution())
                .build();
    }

    @Operation(summary = "Biểu đồ cột thống kê số lượng đơn hàng theo từng tháng trong năm")
    @GetMapping("orders/monthly-distribution")
    public ResponseObject<?> getMonthlyOrderDistribution(@RequestParam(defaultValue = "2025") Integer year) {
        return ResponseObject.builder()
                .status(1000)
                .message("Lấy biểu đồ cột thống kê số lượng đơn hàng theo từng tháng trong năm "+ year +" thành công")
                .data(dashboardService.getMonthlyOrderDistribution(year))
                .build();
    }
}
