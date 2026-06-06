package com.trevix.property_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.trevix.property_management.dto.request.UserCreateRequest;
import com.trevix.property_management.dto.request.UserUpdateRequest;
import com.trevix.property_management.dto.response.UserResponse;
import com.trevix.property_management.entity.User;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.exception.AppException;
import com.trevix.property_management.mapper.UserMapper;
import com.trevix.property_management.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent())
            throw new AppException(ErrorCode.DUPLICATE_RESOURCE, "User with this email already exists");

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + userId));

        userMapper.updateEntity(user, request);

        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse getUserById(UUID userId) {
        return userMapper.toResponse(
            userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + userId))
        );
    }

    public UserResponse getUserByEmail(String email) {
        return userMapper.toResponse(
            userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with email: " + email))
        );
    }

    public List<UserResponse> getAllUsers() {
        return userMapper.toResponseList(userRepository.findAll());
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + userId);

        userRepository.deleteById(userId);
    }

    @Transactional
    public void softDeleteUser(UUID userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + userId));

        userRepository.softDelete(userId, OffsetDateTime.now());
    }

    @Transactional
    public void activateUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + userId));

        user.setIsActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + userId));

        user.setIsActive(false);
        userRepository.save(user);
    }

    public long countActiveUsers() {
        return userRepository.countActiveByRole(null);
    }
}