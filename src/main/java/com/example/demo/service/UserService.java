package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserQueryRequest;
import com.example.demo.dto.UserWithRolesDTO;
import com.example.demo.entity.User;

import java.util.List;

public interface UserService {
    User register(RegisterRequest request);

    User login(LoginRequest request);

    User getById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    IPage<UserWithRolesDTO> getUserList(UserQueryRequest request);
}
