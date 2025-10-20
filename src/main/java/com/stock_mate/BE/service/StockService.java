package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.StockRequest;
import com.stock_mate.BE.dto.response.StockResponse;
import com.stock_mate.BE.entity.Stock;
import com.stock_mate.BE.entity.StockItem;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.StockMapper;
import com.stock_mate.BE.repository.StockRepository;
import com.stock_mate.BE.repository.StockItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final StockItemRepository stockItemRepository;

    @Transactional
    public Stock importStock(StockRequest request) {
        Stock stock = null;

        // Kiểm tra raw material
        if(request.rmID() != null && !request.rmID().isEmpty()) {
            //tim Stock theo rmID trong db
            List<Stock> existStock = stockRepository.findByRawMaterial_RmID(request.rmID());

            //neu da ton tai thi cong viec cong don so luong
            if(!existStock.isEmpty()) {
                stock = existStock.get(0);
                stock.setQuantity(stock.getQuantity() + request.quantity());
            }
        }

        // Kiểm tra semi finish product
//        if(stock == null && request.sfgID() != null && !request.sfgID().isEmpty()) {
//            List<Stock> existStock = stockRepository.findBySemiFinishProduct_SfgID(request.sfgID());
//            if(!existStock.isEmpty()) {
//                stock = existStock.get(0);
//                stock.setQuantity(stock.getQuantity() + request.quantity());
//            }
//        }

        // Kiểm tra finish product
        if(stock == null && request.fgID() != null && !request.fgID().isEmpty()) {
            List<Stock> existStock = stockRepository.findByFinishProduct_FgID(request.fgID());
            if(!existStock.isEmpty()) {
                stock = existStock.get(0);
                stock.setQuantity(stock.getQuantity() + request.quantity());
            }
        }

        // Nếu chưa tồn tại, tạo Stock mới
        if(stock == null) {
            stock = stockMapper.toEntity(request);
        }

        // Lưu Stock
        stock = stockRepository.save(stock);

        // Tạo StockItem để ghi nhận lịch sử IMPORT
        StockItem stockItem = new StockItem();
        stockItem.setStock(stock);
        stockItem.setRmID(request.rmID());
        stockItem.setFgID(request.fgID());
//        stockItem.setSfgID(request.sfgID());
        stockItem.setQuantity(request.quantity());
        stockItem.setType("IMPORT");
        stockItem.setCreateDate(LocalDate.now());
        stockItem.setUpdateDate(LocalDate.now());
        stockItem.setNote(request.note());
        stockItemRepository.save(stockItem);

        return stock;
    }

    @Transactional
    public Stock exportStock(StockRequest request) {
        Stock stock = null;

        // Tìm Stock theo raw material
        if(request.rmID() != null && !request.rmID().isEmpty()) {
            List<Stock> existStock = stockRepository.findByRawMaterial_RmID(request.rmID());
            if(!existStock.isEmpty()) {
                stock = existStock.get(0);
            }
        }

        // Tìm Stock theo semi finish product
//        if(stock == null && request.sfgID() != null && !request.sfgID().isEmpty()) {
//            List<Stock> existStock = stockRepository.findBySemiFinishProduct_SfgID(request.sfgID());
//            if(!existStock.isEmpty()) {
//                stock = existStock.get(0);
//            }
//        }

        // Tìm Stock theo finish product
        if(stock == null && request.fgID() != null && !request.fgID().isEmpty()) {
            List<Stock> existStock = stockRepository.findByFinishProduct_FgID(request.fgID());
            if(!existStock.isEmpty()) {
                stock = existStock.get(0);
            }
        }

        // Nếu không tìm thấy Stock
        if(stock == null) {
            throw new RuntimeException("Trong kho không tồn tại mặt hàng này.");
        }

        // Kiểm tra số lượng đủ để export
        if(stock.getQuantity() < request.quantity()) {
            String detailMessage = String.format(
                    "Trong kho không có đủ số lượng!! Số lượng có sẵn: %d, Số lượng yêu cầu xuất: %d",
                    stock.getQuantity(),
                    request.quantity()
            );
            throw new AppException(ErrorCode.OUT_OF_STOCK, detailMessage);
        }

        // Trừ quantity từ Stock
        stock.setQuantity(stock.getQuantity() - request.quantity());
        stock = stockRepository.save(stock);

        // Tạo StockItem để ghi nhận lịch sử EXPORT
        StockItem stockItem = new StockItem();
        stockItem.setStock(stock);
        stockItem.setRmID(request.rmID());
        stockItem.setFgID(request.fgID());
//        stockItem.setSfgID(request.sfgID());
        stockItem.setQuantity(request.quantity());
        stockItem.setType("EXPORT");
        stockItem.setCreateDate(LocalDate.now());
        stockItem.setUpdateDate(LocalDate.now());
        stockItem.setNote(request.note());
        stockItemRepository.save(stockItem);

        return stock;
    }

    // Xem danh cách vat tư trong kho
    public List<StockResponse> getAllStocks() {
        var stocks = stockRepository.findAll();
        return stocks.stream().map(stockMapper::toDto).toList();
    }

    // Xem lịch sử nhập xuất kho
    public List<StockItem> getAllStockItems() {
        return stockItemRepository.findAll();
    }

}
