//package com.stock_mate.BE.service;
//
//import com.stock_mate.BE.entity.Requistion;
//import com.stock_mate.BE.mapper.RequistionMapper;
//import com.stock_mate.BE.repository.RequistionRepository;
//import com.stock_mate.BE.service.filter.BaseSpecificationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.stereotype.Service;
//
//import java.util.function.Function;
//
//@Service
//@RequiredArgsConstructor
//public class RequistionService extends BaseSpecificationService<Requistion, String> {
//
//    @Autowired
//    RequistionRepository requistionRepository;
//
//    @Autowired
//    RequistionMapper requistionMapper;
//
//    @Override
//    protected JpaSpecificationExecutor<Requistion> getRepository() {
//        return requistionRepository;
//    }
//
//    @Override
//    protected Function<Requistion, RequistionResponse> getMapper() {
//        return requistionMapper::toId;
//    }
//
//    @Override
//    protected Specification<Requistion> buildSpecification(String searchTerm) {
//        return null;
//    }
//}
