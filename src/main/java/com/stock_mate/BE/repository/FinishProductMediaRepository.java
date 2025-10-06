package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.FinishProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FinishProductMediaRepository extends JpaRepository<FinishProductMedia, Long> {

    List<FinishProductMedia> findByFinishProduct_FgID(String productId);
    @Modifying
    @Transactional
    void deleteByFinishProduct_FgID(String finishProductId);
}