package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.UserRequest;
import com.stock_mate.BE.dto.response.UserResponse;
import com.stock_mate.BE.entity.Role;
import com.stock_mate.BE.entity.User;
import com.stock_mate.BE.enums.UserStatus;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.UserMapper;
import com.stock_mate.BE.repository.RoleRepository;
import com.stock_mate.BE.repository.UserRepository;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.ProviderNotFoundException;
import java.time.LocalDate;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserService extends BaseSpecificationService<User, UserResponse> {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserMapper userMapper;
    @Autowired
    private final CloudinaryService cloudinaryService;

    @Autowired
    private RoleRepository roleRepository;

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
        return (root, query, cb) -> {
            //if searchTerm is null => no condition
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String search = searchTerm.trim();

            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("userID")), searchPattern),
                    cb.like(cb.lower(root.get("fullName")), searchPattern),
                    cb.like(cb.lower(root.get("phoneNumber")), searchPattern),
                    cb.like(cb.lower(root.get("email")), searchPattern),
                    cb.like(cb.lower(root.get("role")), searchPattern),
                    cb.like(cb.lower(root.get("manager")), searchPattern)
            );
        };
    }

    @Transactional
    public UserResponse updateUserImage(String userId, MultipartFile imageFile) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy nhân viên với ID: " + userId));

        // Delete old image if exists
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            // Extract public ID from the existing image URL
            String oldPublicId = cloudinaryService.extractPublicIdFromUrl(user.getImage());
            if (oldPublicId != null) {
                cloudinaryService.deleteImage(oldPublicId);
            }
        }

        // Upload new image
        String folder = "users/" + userId;
        String imageUrl = cloudinaryService.uploadImageWithFolder(imageFile, folder);

        // Update user entity
        user.setImage(imageUrl);
        user.setUpdateDate(LocalDate.now());

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByFullName(request.getFullName()) != null) {
            throw new RuntimeException("Nhân viên này đã tồn tại");
        }
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Nhân viên này đã tồn tại");
        }
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()) != null) {
            throw new RuntimeException("Nhân viên này đã tồn tại");
        }
        User user = new User();
        if (request.getRoleName() == null) {
            throw new RuntimeException("Hãy nhập một chức vụ");
        }
        Role role = roleRepository.findById(request.getRoleName())
                .orElseThrow(() -> new ProviderNotFoundException("Không tìm thấy chức vụ" + request.getRoleName()));
        user.setRole(role);
        if (request.getManagerID() != null) {
            User manager = userRepository.findById(request.getManagerID())
                    .orElseThrow(() -> new ProviderNotFoundException("Không tìm thấy nhân viên với ID: " + request.getManagerID()));
            user.setManager(manager);
        }
        if (request.getUserStatus() != null) {
            user.setStatus(UserStatus.valueOf(request.getUserStatus()));
        } else {
            user.setStatus(UserStatus.ACTIVE);
        }
        user.setFullName(request.getFullName());
        if (request.getEmail() != null && !request.getEmail().matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            throw new IllegalArgumentException("Email không đúng format");
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Số điện thoại không đúng format");
        }
        if (request.getPassword() != null && request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Hãy nhập mật khẩu ít nhất 6 ký tự");
        }
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setCreateDate(LocalDate.now());
        user.setUpdateDate(LocalDate.now());
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(String id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy nhân viên với ID: " + id));

        if (request.getEmail() != null && !request.getEmail().matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            throw new AppException(ErrorCode.INVALID_EMAIL_FORMAT, "Email không đúng format");
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().matches("^0\\d{9}$")) {
            throw new AppException(ErrorCode.INVALID_PHONE_FORMAT, "Số điện thoại không đúng format");
        }
        if (request.getPassword() != null && request.getPassword().length() < 6) {
            throw new AppException(ErrorCode.PASSWORD_LENGTH, "Hãy nhập mật khẩu ít nhất 6 ký tự");
        }
        if (request.getFullName() != null && !request.getFullName().isEmpty()
            && !request.getFullName().equals(user.getFullName())) {
            user.setFullName(request.getFullName());
        }
        if (!request.getEmail().equals(user.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (!request.getPhoneNumber().equals(user.getPhoneNumber())) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (!request.getPassword().equals(user.getPassword())) {
            user.setPassword(request.getPassword());
        }
        if (request.getRoleName() != null && !request.getRoleName().equals(user.getRole())) {
            Role role = roleRepository.findById(request.getRoleName())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Không tìm thấy chức vụ: " + request.getRoleName()));
            user.setRole(role);
        }
        if (request.getManagerID() != null && !request.getManagerID().equals(user.getManager())) {
            User manager = userRepository.findById(request.getManagerID())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy nhân viên với ID: " + request.getManagerID()));
            user.setManager(manager);
        }
        if (request.getUserStatus() != null && !request.getUserStatus().equals(user.getStatus())) {
            user.setStatus(UserStatus.valueOf(request.getUserStatus()));
        }
        user.setUpdateDate(LocalDate.now());
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy nhân viên với ID: " + id));
        return userMapper.toDto(user);
    }

    @Transactional
    public boolean deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy nhân viên với ID: " + id);
        }
        userRepository.deleteById(id);
        return true;
    }

    @Transactional
    public boolean push(int i) {
        return true;
    }
}
