package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.BOMItem;
import com.stock_mate.BE.entity.Customer;
import com.stock_mate.BE.enums.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BOMItemRepository extends JpaRepository<BOMItem, Integer>, JpaSpecificationExecutor<BOMItem> {
  // Tìm tất cả items của một BOM header
  List<BOMItem> findByBomHeader_HeaderID(Integer headerID);

  // Tìm items theo loại material
  List<BOMItem> findByMaterialType(MaterialType materialType);

  // Tìm items chứa raw material cụ thể
  List<BOMItem> findByRawMaterial_RmID(String rmID);

  // Tìm items chứa semi-finish product cụ thể
  List<BOMItem> findBySemiFinishProduct_SfgID(String sfgID);

  // Query - tổng số lượng raw material được sử dụng
  @Query("SELECT SUM(bi.quantity) FROM BOMItem bi " +
          "WHERE bi.rawMaterial.rmID = :rmID")
  Integer getTotalQuantityUsedForRawMaterial(@Param("rmID") String rmID);

  // Query - tổng số lượng semi-finish product được sử dụng
  @Query("SELECT SUM(bi.quantity) FROM BOMItem bi " +
          "WHERE bi.semiFinishProduct.sfgID = :sfgID")
  Integer getTotalQuantityUsedForSemiFinishProduct(@Param("sfgID") String sfgID);
}