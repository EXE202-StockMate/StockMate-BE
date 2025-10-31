package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.response.StockItemResponse;
import com.stock_mate.BE.dto.response.StockResponse;
import com.stock_mate.BE.entity.Stock;
import com.stock_mate.BE.entity.StockItem;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.StockItemMapper;
import com.stock_mate.BE.repository.StockItemRepository;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class StockItemService extends BaseSpecificationService<StockItem, StockItemResponse> {

    private final StockItemRepository stockItemRepository;
    private final StockItemMapper stockItemMapper;

    @Override
    protected JpaSpecificationExecutor<StockItem> getRepository() {
        return stockItemRepository;
    }

    @Override
    protected Function<StockItem, StockItemResponse> getMapper() {
        return stockItemMapper::toDto;
    }

    @Override
    protected Specification<StockItem> buildSpecification(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String search = searchTerm.trim();
            String searchPattern = "%" + search.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("stockItemID").as(String.class)), searchPattern),
                    cb.like(cb.lower(root.get("type")), searchPattern),
                    cb.like(cb.lower(root.get("quantity").as(String.class)), searchPattern),
                    cb.like(cb.lower(root.get("createDate").as(String.class)), searchPattern),
                    cb.like(cb.lower(root.get("updateDate").as(String.class)), searchPattern),
                    cb.like(cb.lower(root.get("note")), searchPattern),

                    // keep numeric equality for status when search is numeric
                    search.matches("\\d+") ?
                            cb.equal(root.get("status"), Integer.parseInt(search)) :
                            cb.disjunction()
            );
        };
    }

    public StockItemResponse getStockItemById(long stockItemID) {
        StockItem stockItem = stockItemRepository.findById(stockItemID)
                .orElseThrow(() -> new AppException(ErrorCode.STOCK_NOT_FOUND, "Stock Item not found with ID: " + stockItemID));
        return stockItemMapper.toDto(stockItem);
    }
}
