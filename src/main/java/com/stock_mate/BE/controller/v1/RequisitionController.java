package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.request.RequisitionRequest;
import com.stock_mate.BE.dto.response.RequisitionResponse;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.service.RequisitionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Requisition", description = "API Yêu cầu vật tư")
@RestController
@RequestMapping("/v1/requistions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequisitionController {
    @Autowired
    RequisitionService requisitionService;

    @PostMapping
    public ResponseObject<RequisitionResponse> createRequisition(@RequestBody RequisitionRequest request) {
        // Implementation for creating a requisition would go here
        return ResponseObject.<RequisitionResponse>builder()
                .status(1000)
                .data(requisitionService.createRequisition(request))
                .message("Chức năng tạo yêu cầu vật tư chưa được triển khai")
                .build();
    }

    @GetMapping
    public ResponseObject<Page<RequisitionResponse>> getAllRequisitions(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "type,unit") String[] sort) {

        Page<RequisitionResponse> list = requisitionService.getAll(search, page, size, sort);

        return ResponseObject.<Page<RequisitionResponse>>builder()
                .status(1000)
                .data(list)
                .message("Lấy danh sách yêu cầu vật tư thành công")
                .build();
    }

    @GetMapping("/{id}")
    public ResponseObject<RequisitionResponse> getRequisitionById(@RequestParam String id) {
        RequisitionResponse requistion = requisitionService.getById(id);
        return ResponseObject.<RequisitionResponse>builder()
                .status(1000)
                .data(requistion)
                .message("Lấy yêu cầu vật tư thành công")
                .build();
    }
}
