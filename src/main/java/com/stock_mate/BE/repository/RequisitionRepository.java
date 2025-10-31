package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.Requisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionRepository extends JpaRepository<Requisition, String>,
        JpaSpecificationExecutor<Requisition> {
}