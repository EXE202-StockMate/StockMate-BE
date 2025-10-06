package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.RawMaterialMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RawMaterialMediaRepository extends JpaRepository<RawMaterialMedia, Long> {

    // Tìm tất cả các media liên quan đến một raw material cụ thể
    List<RawMaterialMedia> findByRawMaterial_RmID(String materialId);

    // Xoá tất cả các media liên quan đến một raw
    // material cụ thể
    void deleteByRawMaterial_RmID(String materialId);
}