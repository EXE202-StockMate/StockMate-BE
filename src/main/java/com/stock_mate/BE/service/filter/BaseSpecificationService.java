package com.stock_mate.BE.service.filter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class BaseSpecificationService<T, R> {

    protected abstract JpaSpecificationExecutor<T> getRepository();

    protected abstract Function<T, R> getMapper();

    /**
     * Mỗi service con sẽ implement logic filter riêng.
     */
    protected abstract Specification<T> buildSpecification(String searchTerm);

    public Page<R> getAll(String search, int page, int size, String[]  sort) {

        Specification<T> spec = buildSpecification(search);

        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort sortObj = Sort.by(sortDirection.equalsIgnoreCase("desc") ?
                        Sort.Direction.DESC : Sort.Direction.ASC,
                sortField);

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<T> entityPage = getRepository().findAll(spec, pageable);

        return entityPage.map(getMapper());
    }
}
