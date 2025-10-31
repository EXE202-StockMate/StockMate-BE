package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.RequisitionRequest;
import com.stock_mate.BE.dto.response.RequisitionResponse;
import com.stock_mate.BE.entity.Requisition;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.RequisitionMapper;
import com.stock_mate.BE.repository.RequisitionRepository;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class RequisitionService extends BaseSpecificationService<Requisition, RequisitionResponse> {

    @Autowired
    RequisitionRepository requisitionRepository;

    @Autowired
    RequisitionMapper requisitionMapper;

    @Override
    protected JpaSpecificationExecutor<Requisition> getRepository() {
        return requisitionRepository;
    }

    @Override
    protected Function<Requisition, RequisitionResponse> getMapper() {
        return requisitionMapper::toDto;
    }


    @Override
    protected Specification<Requisition> buildSpecification(String searchTerm) {
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

    public RequisitionResponse getById(String id) {
        return requisitionMapper.toDto(requisitionRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.REQUISITION_NOT_FOUND, "Yêu cầu vật tư không tồn tại")
        ));
    }

    @Transactional
    public RequisitionResponse createRequisition(RequisitionRequest requisition) {
        Requisition newRequisition = new Requisition();
        newRequisition.setType(requisition.type());
        newRequisition.setQuantity(requisition.quantity());
        newRequisition.setUnit(requisition.unit());
        newRequisition.setNote(requisition.note());
        newRequisition.setCreateDate(LocalDate.now());
        newRequisition.setUpdateDate(LocalDate.now());
        requisitionRepository.save(newRequisition);
        return requisitionMapper.toDto(newRequisition);
    }
}
