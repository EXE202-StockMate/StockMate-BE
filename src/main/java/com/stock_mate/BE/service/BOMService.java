package com.stock_mate.BE.service;

import com.stock_mate.BE.entity.BOMHeader;
import com.stock_mate.BE.entity.BOMItem;
import com.stock_mate.BE.enums.MaterialType;
import com.stock_mate.BE.repository.BOMHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class BOMService {

    @Autowired
    private BOMHeaderRepository bomRepository;

    public Map<String, Integer> calculateMaterialRequirement(String productId, int orderQuantity){
        Optional<BOMHeader> bomOpt = bomRepository.findByFinishProduct_FgID(productId);
        if (bomOpt.isEmpty()) {
            throw new RuntimeException("BOM không tìm thấy sản phẩm với id: #" + productId);
        }

        BOMHeader bom = bomOpt.get();
        Map<String, Integer> requirements = new HashMap<>();

        for (BOMItem item : bom.getItems()) {
            String materialId = item.getMaterialType() == com.stock_mate.BE.enums.MaterialType.RAW_MATERIAL ?
                    item.getRawMaterial().getRmID() : item.getSemiFinishProduct().getSfgID();

            int totalRequired = item.getQuantity() * orderQuantity;
            requirements.put(materialId, totalRequired);
        }

        return requirements;
    }
}
