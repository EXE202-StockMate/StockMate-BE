package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.request.BOM.BOMCreateRequest;
import com.stock_mate.BE.dto.request.BOM.BOMUpdateRequest;
import com.stock_mate.BE.dto.response.BOM.BOMResponse;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.service.BOMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BOM", description = "API Định mức sản phẩm")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/boms")
public class BOMController {

    @Autowired
    BOMService bomService;

    @PostMapping
    @Operation(summary = "Tạo định mức sản phẩm")
    public ResponseObject<BOMResponse> createBOM(@RequestBody BOMCreateRequest request) {
        return ResponseObject.<BOMResponse>builder()
                .status(1000)
                .data(bomService.createBOM(request))
                .message("Tạo định mức sản phẩm thành công")
                .build();
    }

    @PutMapping("/{bomId}")
    @Operation(summary = "Cập nhật định mức sản phẩm")
    public ResponseObject<BOMResponse> updateBOM(
            @PathVariable int bomId,
            @RequestBody BOMUpdateRequest request) {
        return ResponseObject.<BOMResponse>builder()
                .status(1000)
                .data(bomService.updateBOM(bomId, request))
                .message("Cập nhật định mức sản phẩm thành công")
                .build();
    }

    @GetMapping
    @Operation(summary = "Xem danh sách tất cả BOM")
    public ResponseObject<Page<BOMResponse>> getAllBOMs(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "finishProduct,note") String[] sort)
    {
        return ResponseObject.<Page<BOMResponse>>builder()
                .status(1000)
                .message("Lấy danh sách BOM thành công")
                .data(bomService.getAll(search, page, size, sort))
                .build();
    }

    @GetMapping("/{bomId}")
    @Operation(summary = "Xem chi tiết BOM theo ID")
    public ResponseObject<BOMResponse> getBOMById(@PathVariable int bomId) {
        return ResponseObject.<BOMResponse>builder()
                .status(1000)
                .message("Lấy BOM thành công")
                .data(bomService.getBOMById(bomId))
                .build();
    }

    @DeleteMapping("/{bomId}")
    @Operation(summary = "Xóa BOM")
    public ResponseObject<Void> deleteBOM(@PathVariable int bomId) {
        bomService.deleteBOM(bomId);
        return ResponseObject.<Void>builder()
                .status(1000)
                .message("Xóa BOM thành công")
                .build();
    }

}
