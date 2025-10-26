package com.stock_mate.BE.service;

import com.stock_mate.BE.entity.Customer;
import com.stock_mate.BE.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.ProviderNotFoundException;

@Service
@RequiredArgsConstructor
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    public Customer findById(String userID){
        return customerRepository.findById(userID)
                .orElseThrow(() -> new ProviderNotFoundException("Customer not found"));
    }

}
