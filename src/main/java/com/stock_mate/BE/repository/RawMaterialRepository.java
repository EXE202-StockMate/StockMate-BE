package com.stock_mate.BE.repository;

import com.stock_mate.BE.dto.response.dashboard.RawMaterialCategoryDistribution;
import com.stock_mate.BE.entity.FinishProduct;
import com.stock_mate.BE.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, String>,
        JpaSpecificationExecutor<RawMaterial> {

    @Query("SELECT new com.stock_mate.BE.dto.response.dashboard.RawMaterialCategoryDistribution(r.category, COUNT(r)) " +
            "FROM RawMaterial r GROUP BY r.category")
    List<RawMaterialCategoryDistribution> countByCategory();

}