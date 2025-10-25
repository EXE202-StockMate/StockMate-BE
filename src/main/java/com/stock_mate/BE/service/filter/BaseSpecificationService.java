package com.stock_mate.BE.service.filter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class BaseSpecificationService<T, R> {

    protected abstract JpaSpecificationExecutor<T> getRepository();

    protected abstract Function<T, R> getMapper();

    /**
     * Mỗi service con sẽ implement logic filter riêng.
     */
    protected Specification<T> buildSpecification(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }

            if (isDateFormat(searchTerm)) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date searchDate = dateFormat.parse(searchTerm);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(searchDate);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    Date startDate = calendar.getTime();

                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    Date endDate = calendar.getTime();

                    return cb.or(
                            cb.between(root.get("createDate"), startDate, endDate),
                            cb.between(root.get("updateDate"), startDate, endDate)
                    );
                } catch (ParseException e) {
                    // Nếu không parse được, bỏ qua
                    return cb.conjunction();
                }
            }
            return cb.conjunction();
        };
    }

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

    protected boolean isDateFormat(String dateStr) {
        try {
            new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
