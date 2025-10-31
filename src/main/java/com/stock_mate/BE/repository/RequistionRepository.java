package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.RawMaterial;
import com.stock_mate.BE.entity.Requistion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RequistionRepository extends JpaRepository<Requistion, String>,
        JpaSpecificationExecutor<Requistion> {
}