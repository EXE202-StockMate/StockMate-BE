package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.response.UserResponse;
import com.stock_mate.BE.mapper.UserMapper;
import com.stock_mate.BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getAllUsers() {
        var list = userRepository.findAll();
        return list.stream().map(userMapper::toDto).toList();
    }
}
