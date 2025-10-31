package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.LoginRequest;
import com.stock_mate.BE.dto.response.UserResponse;
import com.stock_mate.BE.entity.User;
import com.stock_mate.BE.enums.UserStatus;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.UserMapper;
import com.stock_mate.BE.repository.UserRepository;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.ProviderNotFoundException;
import java.time.LocalDate;
import java.util.function.Function;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class AuthService extends BaseSpecificationService<User, UserResponse> {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Override
    protected JpaSpecificationExecutor<User> getRepository() {
        return userRepository;
    }

    @Override
    protected Function<User, UserResponse> getMapper() {
        return userMapper::toDto;
    }

    @Override
    protected Specification<User> buildSpecification(String searchTerm) {
        return null;
    }

    /**
     *BASIC LOGIN - chỉ kiểm tra email + password, không có token
     */
    @Transactional
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản");
        }
        if (!user.getPassword().equals(request.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "Mật khẩu không đúng");
        }
        return userMapper.toDto(user);
    }

    @Transactional
    public Boolean logout(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy tài khoản");
        }
        return true;
    }
}
