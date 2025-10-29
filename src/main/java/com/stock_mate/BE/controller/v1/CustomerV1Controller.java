package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.request.CustomerRequest;
import com.stock_mate.BE.dto.response.CustomerResponse;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Customer", description = "API Quản lý khách hàng")
@RestController
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerV1Controller {

    @Autowired
    CustomerService customerService;

    @GetMapping
    @Operation(summary = "Lấy danh sách khách hàng")
    public ResponseObject<Page<CustomerResponse>> getAllCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createDate,desc") String[] sort
    ) {
        Page<CustomerResponse> list = customerService.getAll(search, page, size, sort);
        return ResponseObject.<Page<CustomerResponse>>builder()
                .status(1000)
                .data(list)
                .message("Lấy danh sách khách hàng thành công")
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết khách hàng theo ID")
    public ResponseObject<CustomerResponse> getCustomerById(@PathVariable String id) {
        return ResponseObject.<CustomerResponse>builder()
                .status(1000)
                .data(customerService.getCustomerById(id))
                .message("Lấy thông tin khách hàng thành công")
                .build();
    }

    @PostMapping
    @Operation(summary = "Tạo mới khách hàng")
    public ResponseObject<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        return ResponseObject.<CustomerResponse>builder()
                .status(1000)
                .data(customerService.createCustomer(request))
                .message("Tạo khách hàng thành công")
                .build();
    }


    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin khách hàng")
    public ResponseObject<CustomerResponse> updateCustomer(
            @PathVariable String id,
            @RequestBody CustomerRequest request) {
        return ResponseObject.<CustomerResponse>builder()
                .status(1000)
                .data(customerService.updateCustomer(id, request))
                .message("Cập nhật khách hàng thành công")
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa khách hàng")
    public ResponseObject<Boolean> deleteCustomer(@PathVariable String id) {
        return ResponseObject.<Boolean>builder()
                .status(1000)
                .data(customerService.deleteCustomer(id))
                .message("Xóa khách hàng thành công")
                .build();
    }
}
