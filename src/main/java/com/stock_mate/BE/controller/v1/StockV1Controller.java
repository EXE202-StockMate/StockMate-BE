package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.request.StockRequest;
import com.stock_mate.BE.dto.response.StockResponse;
import com.stock_mate.BE.entity.Stock;
import com.stock_mate.BE.service.StockService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Stock", description ="API Quản lý Kho")
@RestController
@RequestMapping("/v1/stocks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockV1Controller {
    @Autowired
    StockService stockService;

    @GetMapping
    public Iterable<StockResponse> getAllStocks() {
        return stockService.getAllStocks();
    }

    @PostMapping("/import")
    public String importStock(@RequestBody StockRequest request) {
        Stock importedStock = stockService.importStock(request);
        return "Imported stock with ID: " + importedStock.getStockID();
    }

    @PostMapping("/export")
    public String exportStock(@RequestBody StockRequest request) {
        Stock exportedStock = stockService.exportStock(request);
        return "Exported stock with ID: " + exportedStock.getStockID() + ", Remaining Quantity: " + exportedStock.getQuantity();
    }
}
