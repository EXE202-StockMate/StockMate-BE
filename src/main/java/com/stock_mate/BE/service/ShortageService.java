package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.RequisitionRequest;
import com.stock_mate.BE.entity.*;
import com.stock_mate.BE.enums.MaterialType;
import com.stock_mate.BE.mapper.RequisitionMapper;
import com.stock_mate.BE.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShortageService {
    private final ShortageRepository shortageRepository;
    private final StockRepository stockRepository;
    private final BOMService bomService;
    private final RawMaterialRepository rawMaterialRepository;
    private final FinishProductRepository finishProductRepository;
    private final RequisitionRepository requisitionRepository;
    private final RequisitionMapper requisitionMapper;
    private final RequisitionService requisitionService;

    public List<Shortage> calculateShortageForOrder(Order order) {
        List<Shortage> shortages = new ArrayList<>();

        //Lấy tất cả OrderItem trong Order
        for (OrderItem orderItem : order.getOrderItems()) {
            String productId = orderItem.getFinishProduct().getFgID();
            int orderQuantity = orderItem.getQuantity();

            //Tính định mức nguyên liệu cho FinishProduct
            Map<String, Integer> materialRequirements = bomService
                    .calculateMaterialRequirement(productId, orderQuantity);

            //Kiểm tra shortage cho từng nguyên liệu
            for (Map.Entry<String, Integer> requirement : materialRequirements.entrySet()) {
                String materialId = requirement.getKey();
                Integer requiredQty = requirement.getValue();

                //Lấy số lượng hiện tại có trong kho
                Integer availableQty = getAvailableQuantity(materialId);

                //yêu cầu nhiều hơn hiện tại có
                if (requiredQty > availableQty) {
                    //create Shortage record
                    Shortage shortage = createShortage(order, materialId, requiredQty, availableQty);
                    shortages.add(shortage);

                    //auto create requisition
                    RequisitionRequest request = new RequisitionRequest(
                            materialId,
                            MaterialType.RAW_MATERIAL,
                            shortage.getUnit(),
                            shortage.getShortageQuantity(),
                            "Tự động tạo cho đơn hàng có mã là #" + order.getOrderID()
                    );
                    requisitionService.createRequisition(request);
                }
            }
        }
        return shortageRepository.saveAll(shortages);
    }

    private Shortage createShortage(Order order, String materialId, Integer required, Integer available) {
        Shortage shortage = new Shortage();
        shortage.setOrder(order);
        shortage.setRequiredQuantity(required);
        shortage.setAvailableQuantity(available);
        shortage.setShortageQuantity(required - available);

        // Tính phần trăm thiếu
        shortage.setShortagePercentage(
                BigDecimal.valueOf((double)(required - available) / required * 100)
                        .setScale(2, RoundingMode.HALF_UP)
        );
        shortage.setMaterialType(MaterialType.RAW_MATERIAL);
        // Set relationship với RawMaterial
        rawMaterialRepository.findById(materialId).ifPresent(rawMaterial -> {

            shortage.setRawMaterial(rawMaterial);
            shortage.setUnit(rawMaterial.getDimension()); // Hoặc field unit nào đó
        });

        return shortage;
    }

    private Integer getAvailableQuantity(String materialId) {
        //Kiểm tra trong Stock Table theo RawMaterial
        List<Stock> stocks = stockRepository.findByRawMaterial_RmID(materialId);
        return stocks.stream()
                .mapToInt(Stock::getQuantity)
                .sum();
    }
}
