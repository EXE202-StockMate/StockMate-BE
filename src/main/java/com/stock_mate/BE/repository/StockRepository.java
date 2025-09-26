package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
    List<Stock> findByRawMaterial_RmID(String rmID);
}