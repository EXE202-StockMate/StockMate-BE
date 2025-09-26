package com.stock_mate.BE.service;

import com.stock_mate.BE.entity.Order;
import com.stock_mate.BE.entity.OrderItem;
import com.stock_mate.BE.entity.Shortage;
import com.stock_mate.BE.entity.Stock;
import com.stock_mate.BE.enums.MaterialType;
import com.stock_mate.BE.repository.ShortageRepository;
import com.stock_mate.BE.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShortageService {
    ShortageRepository shortageRepository;
    StockRepository stockRepository;
    BOMService bomService;

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
                    Shortage shortage = createShortage(order, materialId, requiredQty, availableQty);
                    shortages.add(shortage);
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

        //xác định loại material và set relationship
        if(isRawMaterial(materialId)) {
            shortage.setMaterialType(MaterialType.RAW_MATERIAL);
            //set material relationship nếu cần
        } else {
            //set semi-finish product relationship nếu cần
            shortage.setMaterialType(MaterialType.SEMI_FINISH_PRODUCT);
        }
        return shortage;
    }

    private Integer getAvailableQuantity(String materialId) {
        //Kiểm tra trong Stock Table
        List<Stock> stocks = stockRepository.findByRawMaterial_RmID(materialId);
        return stocks.stream()
                .mapToInt(Stock::getQuantity)
                .sum();
    }

    private boolean isRawMaterial(String materialId) {
        // Logic để xác định loại material
        return materialId.startsWith("RM");
    }
}
