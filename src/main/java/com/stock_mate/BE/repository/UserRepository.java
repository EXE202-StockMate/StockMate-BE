package com.stock_mate.BE.repository;

import com.stock_mate.BE.dto.response.UserResponse;
import com.stock_mate.BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    User findByUsername(String username);
}