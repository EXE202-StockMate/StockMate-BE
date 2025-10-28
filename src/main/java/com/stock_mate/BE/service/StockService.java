package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.StockRequest;
import com.stock_mate.BE.dto.response.StockItemResponse;
import com.stock_mate.BE.dto.response.StockResponse;
import com.stock_mate.BE.entity.Stock;
import com.stock_mate.BE.entity.StockItem;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.StockItemMapper;
import com.stock_mate.BE.mapper.StockMapper;
import com.stock_mate.BE.repository.*;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class StockService extends BaseSpecificationService<Stock, StockResponse> {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final StockItemMapper stockItemMapper;
    private final StockItemRepository stockItemRepository;

    private final RawMaterialRepository rawMaterialRepository;
    private final FinishProductRepository finishProductRepository;
    private final SemiFinishProductRepository semiFinishProductRepository;


    @Override
    protected JpaSpecificationExecutor<Stock> getRepository() {
        return stockRepository;
    }

    @Override
    protected Function<Stock, StockResponse> getMapper() {
        return stockMapper::toDto;
    }

    @Override
    protected Specification<Stock> buildSpecification(String searchTerm) {
        return (root, query, cb) -> {
            //if searchTerm is null => no condition
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String search = searchTerm.trim();

            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("stockName")), searchPattern),
                    cb.like(cb.lower(root.get("image")), searchPattern),
                    cb.like(cb.lower(root.get("quantity")), searchPattern),
                    cb.like(cb.lower(root.get("unit")), searchPattern),
                    cb.like(cb.lower(root.get("status")), searchPattern),
                    // Handle integer status field correctly
                    searchTerm.matches("\\d+") ?
                            cb.equal(root.get("status"), Integer.parseInt(searchTerm)) :
                            cb.or() // Empty predicate that will be ignored if not a number
            );
        };
    }

    @Transactional
    public StockItemResponse importStock(StockRequest request) {
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
        rawMaterialRepository.findById(request.rmID()).ifPresent(stockItem::setRawMaterial);
        finishProductRepository.findById(request.fgID()).ifPresent(stockItem::setFinishProduct);
//        semiFinishProductRepository.findById(request.sfgID()).ifPresent(stockItem::setSemiFinishProduct);
        stockItem.setQuantity(request.quantity());
        stockItem.setType("IMPORT");
        stockItem.setCreateDate(LocalDate.now());
        stockItem.setUpdateDate(LocalDate.now());
        stockItem.setNote(request.note());
        StockItem savedStockItem = stockItemRepository.save(stockItem);

        return stockItemMapper.toDto(savedStockItem);
    }

    @Transactional
    public StockItemResponse exportStock(StockRequest request) {
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
        rawMaterialRepository.findById(request.rmID()).ifPresent(stockItem::setRawMaterial);
        finishProductRepository.findById(request.fgID()).ifPresent(stockItem::setFinishProduct);
//        semiFinishProductRepository.findById(request.sfgID()).ifPresent(stockItem::setSemiFinishProduct);
        stockItem.setQuantity(request.quantity());
        stockItem.setType("EXPORT");
        stockItem.setCreateDate(LocalDate.now());
        stockItem.setUpdateDate(LocalDate.now());
        stockItem.setNote(request.note());
        StockItem s = stockItemRepository.save(stockItem);

        return stockItemMapper.toDto(s);
    }

    public StockResponse getById(int stockID) {
        Stock stock = stockRepository.findById(stockID)
                .orElseThrow(() -> new AppException(ErrorCode.STOCK_NOT_FOUND, "Stock not found with ID: " + stockID));
        return stockMapper.toDto(stock);
    }

}
