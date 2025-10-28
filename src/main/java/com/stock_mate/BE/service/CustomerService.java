package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.CustomerRequest;
import com.stock_mate.BE.dto.response.CustomerResponse;
import com.stock_mate.BE.entity.Customer;
import com.stock_mate.BE.mapper.CustomerMapper;
import com.stock_mate.BE.repository.CustomerRepository;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;


import java.nio.file.ProviderNotFoundException;
import java.time.LocalDate;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CustomerService extends BaseSpecificationService<Customer, CustomerResponse> {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerMapper customerMapper;

    @Override
    protected JpaSpecificationExecutor<Customer> getRepository() {
        return customerRepository;
    }

    @Override
    protected Function<Customer, CustomerResponse> getMapper() {
        return customerMapper::toResponse;
    }

    @Override
    protected Specification<Customer> buildSpecification(String searchTerm) {
        return (root, query, cb) -> {
            //if searchTerm is null => no condition
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String search = searchTerm.trim();

            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("customerID")), searchPattern),
                    cb.like(cb.lower(root.get("customerName")), searchPattern),
                    cb.like(cb.lower(root.get("description")), searchPattern)
            );
        };
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        if(customerRepository.findByCustomerNameContainingIgnoreCase(request.getCustomerName()) != null){
            throw new RuntimeException("Khách hàng này đã tồn tại");
        }
        Customer customer = customerMapper.toEntity(request);
        customer.setCreateDate(LocalDate.now());
        customer.setUpdateDate(LocalDate.now());
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse updateCustomer(String id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ProviderNotFoundException("Không tìm thấy khách hàng với ID: " + id));
        if (request.getCustomerName() != null
            && !request.getCustomerName().trim().isEmpty()
            && !request.getCustomerName().equals(customer.getCustomerName())) {
            customer.setCustomerName(request.getCustomerName());
        }
        if (request.getDescription() != null
            && !request.getDescription().trim().isEmpty()
            && !request.getDescription().equals(customer.getDescription())) {
            customer.setDescription(request.getDescription());
        }
        customer.setUpdateDate(LocalDate.now());
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse getCustomerById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ProviderNotFoundException("Không tìm thấy khách hàng với ID: " + id));
        return customerMapper.toResponse(customer);
    }

    @Transactional
    public boolean deleteCustomer(String id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy khách hàng với ID: " + id);
        }
        customerRepository.deleteById(id);
        return true;
    }
}
