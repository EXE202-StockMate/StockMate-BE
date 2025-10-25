package com.stock_mate.BE.repository;

import com.stock_mate.BE.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,String>,
        JpaSpecificationExecutor<Customer> {
}
