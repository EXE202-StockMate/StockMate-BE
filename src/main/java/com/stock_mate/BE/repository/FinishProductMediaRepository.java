package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.FinishProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinishProductMediaRepository extends JpaRepository<FinishProductMedia, Long> {

    List<FinishProductMedia> findByFinishProduct_FgID(String productId);
    void deleteByFinishProduct_FgID(String finishProductId);
}