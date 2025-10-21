package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.request.StockRequest;
import com.stock_mate.BE.dto.response.StockResponse;
import com.stock_mate.BE.entity.FinishProduct;
import com.stock_mate.BE.entity.RawMaterial;
import com.stock_mate.BE.entity.SemiFinishProduct;
import com.stock_mate.BE.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface StockMapper {

    @Mapping(target = "rawMaterial", expression = "java(mapRawMaterial(stock.rmID()))")
    @Mapping(target = "finishProduct", expression = "java(mapFinishProduct(stock.fgID()))")
    @Mapping(target = "semiFinishProduct", expression = "java(mapSemiFinishProduct(stock.sfgID()))")
    Stock toEntity(StockRequest stock);

    default RawMaterial mapRawMaterial(String rmID){
        if(rmID == null|| rmID.isEmpty()) {
            return null;
        }
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setRmID(rmID);
        return rawMaterial;
    }
    default FinishProduct mapFinishProduct(String fgID){
        if(fgID == null || fgID.isEmpty()) {
            return null;
        }
        FinishProduct finishProduct = new FinishProduct();
        finishProduct.setFgID(fgID);
        return finishProduct;
    }
    default SemiFinishProduct mapSemiFinishProduct(String sfgID) {
        if (sfgID == null || sfgID.isEmpty()) {
            return null;
        }
        SemiFinishProduct semiFinishProduct = new SemiFinishProduct();
        semiFinishProduct.setSfgID(sfgID);
        return semiFinishProduct;
    }
    @Mapping(target = "rawMaterial", source = "rawMaterial")
    @Mapping(target = "finishProduct", source = "finishProduct")
    @Mapping(target = "semiFinishProduct", source = "semiFinishProduct")
    StockResponse toDto(Stock stock);
}
