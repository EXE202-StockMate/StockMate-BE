package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.BOMHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BOMHeaderRepository extends JpaRepository<BOMHeader, Integer> {
  // Tìm BOM theo Finish Product ID
  Optional<BOMHeader> findByFinishProduct_FgID(String fgID);

  // Tìm BOM theo Semi-Finish Product ID
  Optional<BOMHeader> findBySemiFinishProduct_SfgID(String sfgID);

  // Tìm tất cả BOM của một Finish Product
  List<BOMHeader> findAllByFinishProduct_FgID(String fgID);

  // Tìm tất cả BOM của một Semi-Finish Product
  List<BOMHeader> findAllBySemiFinishProduct_SfgID(String sfgID);

  // Kiểm tra BOM có tồn tại cho product không
  boolean existsByFinishProduct_FgID(String fgID);

  boolean existsBySemiFinishProduct_SfgID(String sfgID);

  // Query phức tạp - tìm BOM có chứa raw material cụ thể
  @Query("SELECT DISTINCT bh FROM BOMHeader bh " +
          "JOIN bh.items bi " +
          "WHERE bi.rawMaterial.rmID = :rmID")
  List<BOMHeader> findBOMsContainingRawMaterial(@Param("rmID") String rmID);

  // Query - tìm BOM có chứa semi-finish product cụ thể
  @Query("SELECT DISTINCT bh FROM BOMHeader bh " +
          "JOIN bh.items bi " +
          "WHERE bi.semiFinishProduct.sfgID = :sfgID")
  List<BOMHeader> findBOMsContainingSemiFinishProduct(@Param("sfgID") String sfgID);

  // Query - lấy BOM với items (fetch join để tránh N+1 problem)
  @Query("SELECT bh FROM BOMHeader bh " +
          "LEFT JOIN FETCH bh.items " +
          "WHERE bh.finishProduct.fgID = :fgID")
  Optional<BOMHeader> findByFinishProductWithItems(@Param("fgID") String fgID);
}