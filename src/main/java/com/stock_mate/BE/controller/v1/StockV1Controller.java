package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.request.StockRequest;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.dto.response.StockItemResponse;
import com.stock_mate.BE.dto.response.StockResponse;
import com.stock_mate.BE.entity.Stock;
import com.stock_mate.BE.service.StockItemService;
import com.stock_mate.BE.service.StockService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Stock", description ="API Quản lý Kho")
@RestController
@RequestMapping("/v1/stocks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockV1Controller {
    @Autowired
    StockService stockService;

    @Autowired
    StockItemService stockItemService;

    @GetMapping("/{id}")
    public ResponseObject<StockResponse> getStockById(@PathVariable int id) {
        var stock = stockService.getById(id);
        return ResponseObject.<StockResponse>builder()
                .status(1000)
                .data(stock)
                .message("Get stock by id successfully")
                .build();
    }

    @GetMapping("{id}/history")
    public ResponseObject<StockItemResponse> getStockItemsByStockId(
            @PathVariable long id) {
        var stockItems = stockItemService.getStockItemById(id);
        return ResponseObject.<StockItemResponse>builder()
                .status(1000)
                .data(stockItems)
                .message("Get stock items by stock id successfully")
                .build();
    }

    @GetMapping("/history")
    public ResponseObject<Page<StockItemResponse>> getStockItems(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createDate,desc") String[] sort
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
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "quantity,desc") String[] sort) {

        var stocks = stockService.getAll(search, type, page, size, sort);
        return ResponseObject.<Page<StockResponse>>builder()
                .status(1000)
                .data(stocks)
                .message("Get all stocks successfully")
                .build();
    }

    @PostMapping("/import")
    public ResponseObject<StockItemResponse> importStock(@RequestBody StockRequest request) {
        var importedStock = stockService.importStock(request);
        return ResponseObject.<StockItemResponse>builder()
                .status(1000)
                .data(importedStock)
                .message("Import stock successfully")
                .build();
    }

    @PostMapping("/export")
    public ResponseObject<StockItemResponse> exportStock(@RequestBody StockRequest request) {
        var exportedStock = stockService.exportStock(request);
        return ResponseObject.<StockItemResponse>builder()
                .status(1000)
                .data(exportedStock)
                .message("Export stock successfully")
                .build();
    }
}
