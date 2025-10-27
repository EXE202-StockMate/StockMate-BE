package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.request.CustomerRequest;
import com.stock_mate.BE.dto.response.CustomerResponse;
import com.stock_mate.BE.entity.Customer;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponse toResponse(Customer customer);
    Customer toEntity(CustomerRequest request);
}
