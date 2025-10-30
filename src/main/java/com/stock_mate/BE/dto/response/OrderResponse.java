package com.stock_mate.BE.dto.response;

import com.stock_mate.BE.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class OrderResponse{
    String orderID;
    LocalDate createDate;
    LocalDate updateDate;
    String code; //stt của đơn hàng, tính theo từng khách hàng
    String userID;
    String customerID;
    OrderStatus status;
    List<RequistionResponse> requistions;
    List<OrderItemResponse> orderItems;
    List<ShortageResponse> shortages;
}
