package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Integer>,
        JpaSpecificationExecutor<OrderItem> {
}
