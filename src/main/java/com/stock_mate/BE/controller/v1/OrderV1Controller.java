package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.request.OrderRequest;
import com.stock_mate.BE.dto.response.OrderResponse;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.enums.OrderStatus;
import com.stock_mate.BE.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order", description ="API Quản lý Đơn hàng")
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class OrderV1Controller {

    @Autowired
    OrderService orderService;

    @PutMapping("/{orderID}/status")
    public ResponseObject<OrderResponse> updateOrderStatus(
            @PathVariable String orderID,
            @RequestParam OrderStatus status
    ){
        return ResponseObject.<OrderResponse>builder()
                .status(1000)
                .message("Cập nhật trạng thái đơn hàng thành công")
                .data(orderService.updateOrderStatus(orderID, status))
                .build();
    }

    @PostMapping
    public ResponseObject<OrderResponse> createOrder(@RequestBody OrderRequest request){
        OrderResponse rs = orderService.createOrder(request);
        String msg = "";
        if (rs.getStatus().equals(OrderStatus.AVAILABLE)) {
            msg = "Đơn hàng đã được tạo";
        } else if (rs.getStatus().equals(OrderStatus.UNAVAILABLE)) {
            msg = "Đơn hàng đã được tạo nhưng không khả dụng, kiểm tra lại vật tư trong kho";
        }
        return ResponseObject.<OrderResponse>builder()
                .status(1000)
                .message(msg)
                .data(rs)
                .build();
    }

    @GetMapping("/{orderID}")
    public ResponseObject<OrderResponse> getOrder(@PathVariable String orderID){
        return ResponseObject.<OrderResponse>builder()
                .status(1000)
                .message("Tìm thấy đơn hàng")
                .data(orderService.findById(orderID))
                .build();
    }

    @GetMapping
    public ResponseObject<Page<OrderResponse>> getAll (
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "customer,asc") String[] sort
    ){
        return ResponseObject.<Page<OrderResponse>>builder()
                .status(1000)
                .message("Danh sách đơn hàng")
                .data(orderService.getAll(search, page, size, sort))
                .build();
    }

    @DeleteMapping("/{orderID}")
    public ResponseObject<Boolean> deleteOrder(@PathVariable String orderID){
        return ResponseObject.<Boolean>builder()
                .status(1000)
                .message("Xóa đơn hàng thành công")
                .data(orderService.deleteById(orderID))
                .build();
    }





























}
