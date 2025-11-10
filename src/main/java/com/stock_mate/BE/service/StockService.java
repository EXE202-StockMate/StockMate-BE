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
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
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
        return buildSpecificationWithType(searchTerm, null);
    }

    protected Specification<Stock> buildSpecificationWithType(String searchTerm, String type) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter theo type - Chỉ có RAW_MATERIAL và FINISH_PRODUCT
            if(type != null && !type.isEmpty()) {
                switch (type.toUpperCase()) {
                    case "RAW_MATERIAL":
                    case "RM":
                        predicates.add(cb.isNotNull(root.get("rawMaterial")));
                        predicates.add(cb.isNull(root.get("finishProduct")));
                        break;
                    case "FINISH_PRODUCT":
                    case "FG":
                        predicates.add(cb.isNotNull(root.get("finishProduct")));
                        predicates.add(cb.isNull(root.get("rawMaterial")));
                        break;
                }
            }

            // Search term logic
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm.toLowerCase() + "%";
                List<Predicate> searchPredicates = new ArrayList<>();

                // Tìm trong Stock.stockName
                searchPredicates.add(
                    cb.like(cb.lower(cb.coalesce(root.get("stockName"), "")), searchPattern)
                );

                // Tìm trong Stock.unit
                searchPredicates.add(
                    cb.like(cb.lower(cb.coalesce(root.get("unit"), "")), searchPattern)
                );

                // Tìm trong RawMaterial.name - Sử dụng LEFT JOIN
                var rawMaterialJoin = root.join("rawMaterial", jakarta.persistence.criteria.JoinType.LEFT);
                searchPredicates.add(
                    cb.like(cb.lower(cb.coalesce(rawMaterialJoin.get("name"), "")), searchPattern)
                );

                // Tìm trong FinishProduct.name - Sử dụng LEFT JOIN
                var finishProductJoin = root.join("finishProduct", jakarta.persistence.criteria.JoinType.LEFT);
                searchPredicates.add(
                    cb.like(cb.lower(cb.coalesce(finishProductJoin.get("name"), "")), searchPattern)
                );

                // Handle integer fields (quantity)
                if (searchTerm.matches("\\d+")) {
                    try {
                        int intValue = Integer.parseInt(searchTerm);
                        searchPredicates.add(cb.equal(root.get("quantity"), intValue));
                    } catch (NumberFormatException e) {
                        // Ignore if parsing fails
                    }
                }

                // Kết hợp tất cả search predicates bằng OR
                predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
            }

            // Kết hợp tất cả predicates bằng AND
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Page<StockResponse> getAll(String search, String type, int page, int size, String[] sort) {
        if (search == null) {
            search = "";
        }

        // Build specification với type
        Specification<Stock> spec;
        if (isDateFormat(search)) {
            spec = buildDateSpecification(search);
            // Thêm type filter vào date spec nếu có
            if (type != null && !type.isEmpty()) {
                Specification<Stock> typeSpec = buildTypeSpecification(type);
                spec = spec.and(typeSpec);
            }
        } else {
            spec = buildSpecificationWithType(search, type);
        }

        // Xử lý sorting - Fix lỗi array index
        String sortField = sort[0];
        String sortDirection = "asc";

        // Kiểm tra nếu có format "field,direction"
        if (sortField.contains(",")) {
            String[] sortParts = sortField.split(",");
            sortField = sortParts[0];
            sortDirection = sortParts.length > 1 ? sortParts[1] : "asc";
        } else if (sort.length > 1) {
            // Nếu có 2 parameters riêng biệt: sort=quantity&sort=desc
            sortDirection = sort[1];
        }

        Sort sortObj = Sort.by(sortDirection.equalsIgnoreCase("desc") ?
                        Sort.Direction.DESC : Sort.Direction.ASC,
                sortField);

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Stock> entityPage = stockRepository.findAll(spec, pageable);

        return entityPage.map(stockMapper::toDto);
    }

    private Specification<Stock> buildTypeSpecification(String type) {
        return (root, query, cb) -> {
            switch (type.toUpperCase()) {
                case "RAW_MATERIAL":
                case "RM":
                    return cb.isNotNull(root.get("rawMaterial"));
                case "FINISH_PRODUCT":
                case "FG":
                    return cb.isNotNull(root.get("finishProduct"));
                default:
                    return cb.conjunction();
            }
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
        // Chỉ tìm và set khi ID không null/empty
        if(request.rmID() != null && !request.rmID().isEmpty()) {
            rawMaterialRepository.findById(request.rmID()).ifPresent(stockItem::setRawMaterial);
        }
        if(request.fgID() != null && !request.fgID().isEmpty()) {
            finishProductRepository.findById(request.fgID()).ifPresent(stockItem::setFinishProduct);
        }
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
        if(request.rmID() != null && !request.rmID().isEmpty()) {
            rawMaterialRepository.findById(request.rmID()).ifPresent(stockItem::setRawMaterial);
        }
        if(request.fgID() != null && !request.fgID().isEmpty()) {
            finishProductRepository.findById(request.fgID()).ifPresent(stockItem::setFinishProduct);
        }
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
