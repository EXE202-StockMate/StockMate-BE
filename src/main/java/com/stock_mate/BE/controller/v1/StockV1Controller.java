package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.request.StockRequest;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.dto.response.StockItemResponse;
import com.stock_mate.BE.dto.response.StockResponse;
import com.stock_mate.BE.entity.Stock;
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

    //    @GetMapping
//    public ResponseObject<StockResponse> getStockById(@RequestParam int stockID) {
//        var stock = stockService.getStockById(stockID);
//        return ResponseObject.<StockResponse>builder()
//                .status(1000)
//                .data(stock)
//                .message("Get stock by ID successfully")
//                .build();
//    }

    @GetMapping
    public ResponseObject<List<StockResponse>> getAllStocks() {
         var stocks = stockService.getAllStocks();
         return ResponseObject.<List<StockResponse>>builder()
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
