package com.stock_mate.BE.controller.v2;

import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.dto.response.StockItemResponse;
import com.stock_mate.BE.dto.response.StockResponse;
import com.stock_mate.BE.service.StockItemService;
import com.stock_mate.BE.service.StockService;
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

import java.util.List;

@Tag(name = "Stock", description ="API Quản lý Kho")
@RestController
@RequestMapping("/v2/stocks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockV2Controller {

    @Autowired
    StockService stockService;

    @Autowired
    StockItemService stockItemService;

    @GetMapping("/history")
    public ResponseObject<Page<StockItemResponse>> getStockItems(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdDate,desc") String[] sort
    ) {
        var stockItems = stockItemService.getAll(search, page, size, sort);
        return ResponseObject.<Page<StockItemResponse>>builder()
                .status(1000)
                .data(stockItems)
                .message("Get stock items successfully")
                .build();
    }

    @GetMapping
    public ResponseObject<Page<StockResponse>> getAllStocks(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "quantity,type") String[] sort) {

        var stocks = stockService.getAll(search, page, size, sort);
        return ResponseObject.<Page<StockResponse>>builder()
                .status(1000)
                .data(stocks)
                .message("Get all stocks successfully")
                .build();
    }
}
