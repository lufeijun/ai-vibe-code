package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserQueryRequest;
import com.example.demo.entity.User;

import java.util.List;

public interface UserService {
    User register(RegisterRequest request);

    User login(LoginRequest request);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> getUserList(UserQueryRequest request);
}
