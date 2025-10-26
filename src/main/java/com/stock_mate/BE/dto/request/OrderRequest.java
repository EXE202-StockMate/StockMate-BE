package com.stock_mate.BE.dto.request;

import com.stock_mate.BE.entity.*;

import java.time.LocalDate;
import java.util.List;

public record OrderRequest (
        String userID,
        String customerID,
        List<OrderItemResquest> items
){}
