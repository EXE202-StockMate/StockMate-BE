package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.FinishProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinishProductRepository extends JpaRepository<FinishProduct, String> {
}