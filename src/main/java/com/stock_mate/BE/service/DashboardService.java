package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.response.dashboard.DashboardSummaryResponse;
import com.stock_mate.BE.repository.FinishProductRepository;
import com.stock_mate.BE.repository.OrderRepository;
import com.stock_mate.BE.repository.RawMaterialRepository;
import com.stock_mate.BE.repository.ShortageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    OrderRepository orderRepo;
    @Autowired
    RawMaterialRepository rawRepo;
    @Autowired
    FinishProductRepository finishRepo;
    @Autowired
    ShortageRepository shortageRepo;

    public Object getMonthlyOrderDistribution(Integer year) {
        return orderRepo.countOrdersByMonth(year);
    }

    public Object getRawMaterialQuantityDistribution() {
        return rawRepo.countByCategory();
    }

    public Object getOrderStatusDistribution() {
        return orderRepo.countByStatus();
    }

    public DashboardSummaryResponse getTotalStatistics() {
        Long totalOrders = orderRepo.count();
        Long totalFinishedProducts = finishRepo.count();
        Long totalRawMaterials = rawRepo.count();
        Long totalShortages = shortageRepo.count();
        return new DashboardSummaryResponse(
                totalOrders,
                totalFinishedProducts,
                totalRawMaterials,
                totalShortages
        );
     }
}
