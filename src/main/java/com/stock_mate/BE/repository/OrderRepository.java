
package com.stock_mate.BE.repository;

import com.stock_mate.BE.dto.response.dashboard.MonthlyOrderCount;
import com.stock_mate.BE.dto.response.dashboard.OrderStatusDistribution;
import com.stock_mate.BE.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>,
        JpaSpecificationExecutor<Order> {

    @Query("SELECT new com.stock_mate.BE.dto.response.dashboard.OrderStatusDistribution(o.status, COUNT(o)) " +
            "FROM Order o " +
            "GROUP BY o.status")
    List<OrderStatusDistribution> countByStatus();

    @Query(value = "SELECT MONTH(createDate) as month, COUNT(*) as totalOrders " +
            "FROM StockMate.Orders " +
            "WHERE YEAR(createDate) = :year " +
            "GROUP BY MONTH(createDate) " +
            "ORDER BY MONTH(createDate)",
            nativeQuery = true)
    List<MonthlyOrderCount> countOrdersByMonth(@Param("year") Integer year);
}
