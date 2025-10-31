package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.response.RequistionResponse;
import com.stock_mate.BE.entity.Requistion;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.RequistionMapper;
import com.stock_mate.BE.repository.RequistionRepository;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class RequistionService extends BaseSpecificationService<Requistion, RequistionResponse> {

    @Autowired
    RequistionRepository requistionRepository;

    @Autowired
    RequistionMapper requistionMapper;

    @Override
    protected JpaSpecificationExecutor<Requistion> getRepository() {
        return requistionRepository;
    }

    @Override
    protected Function<Requistion, RequistionResponse> getMapper() {
        return requistionMapper::toDto;
    }


    @Override
    protected Specification<Requistion> buildSpecification(String searchTerm) {
        return (root, query, cb) -> {
            //if searchTerm is null => no condition
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String search = searchTerm.trim();

            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("type")), searchPattern),
                    cb.like(cb.lower(root.get("unit")), searchPattern),
                    cb.like(cb.lower(root.get("note")), searchPattern)
            );
        };
    }

    public RequistionResponse getById(String id) {
        return requistionMapper.toDto(requistionRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.REQUISITION_NOT_FOUND, "Yêu cầu vật tư không tồn tại")
        ));
    }
}
