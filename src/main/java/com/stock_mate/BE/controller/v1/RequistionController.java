package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.response.RawMaterialResponse;
import com.stock_mate.BE.dto.response.RequistionResponse;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.service.RequistionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Requistion", description = "API Yêu cầu vật tư")
@RestController
@RequestMapping("/v1/requistions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequistionController {
    @Autowired
    RequistionService requistionService;

    @GetMapping
    public ResponseObject<Page<RequistionResponse>> getAllRequistions(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        Page<RequistionResponse> list = requistionService.getAll(search, page, size, sort);

        return ResponseObject.<Page<RequistionResponse>>builder()
                .status(1000)
                .data(list)
                .message("Lấy danh sách vật tư thành công")
                .build();
    }

    @GetMapping("/{id}")
    public ResponseObject<RequistionResponse> getRequistionById(@RequestParam String id) {
        RequistionResponse requistion = requistionService.getById(id);
        return ResponseObject.<RequistionResponse>builder()
                .status(1000)
                .data(requistion)
                .message("Lấy yêu cầu vật tư thành công")
                .build();
    }
}
