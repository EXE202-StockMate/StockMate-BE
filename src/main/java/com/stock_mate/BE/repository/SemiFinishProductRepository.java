package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.SemiFinishProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemiFinishProductRepository extends JpaRepository<SemiFinishProduct, String> {
}