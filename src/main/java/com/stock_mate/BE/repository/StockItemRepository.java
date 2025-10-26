package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.Stock;
import com.stock_mate.BE.entity.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockItemRepository extends JpaRepository<StockItem, Long>, JpaSpecificationExecutor<StockItem> {
    List<StockItem> findByStock_StockID(int stockID);
}

