package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.Shortage;
import com.stock_mate.BE.enums.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ShortageRepository extends JpaRepository<Shortage, Integer> {
    // Tìm shortage theo đơn hàng
    List<Shortage> findByOrder_OrderID(String orderID);

    // Tìm shortage theo loại nguyên liệu
    List<Shortage> findByMaterialType(MaterialType materialType);

    // Tìm shortage theo % thiếu hụt
    @Query("SELECT s FROM Shortage s WHERE s.shortagePercentage >= :percentage")
    List<Shortage> findByShortagePercentageGreaterThan(@Param("percentage") BigDecimal percentage);

    // Tìm shortage theo raw material
    List<Shortage> findByRawMaterial_RmID(String rmID);

    // Tìm shortage theo semi-finish product
    List<Shortage> findBySemiFinishProduct_SfgID(String sfgID);
}