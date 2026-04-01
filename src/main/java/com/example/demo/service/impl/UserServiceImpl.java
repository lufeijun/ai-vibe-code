package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserQueryRequest;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public User register(RegisterRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        if (existsByPhone(request.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setCity(request.getCity());
        user.setIsEmployed(request.getIsEmployed() != null ? request.getIsEmployed() : true);
        user.setHireDate(request.getHireDate());
        user.setResignationDate(request.getResignationDate());

        userMapper.insert(user);
        return user;
    }

    @Override
    public User login(LoginRequest request) {
        if (!StringUtils.hasText(request.getAccount()) || !StringUtils.hasText(request.getPassword())) {
            throw new RuntimeException("账号和密码不能为空");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getAccount())
               .or()
               .eq(User::getEmail, request.getAccount())
               .or()
               .eq(User::getPhone, request.getAccount());

        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return user;
    }

    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<User> getUserList(UserQueryRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getUsername())) {
            wrapper.like(User::getUsername, request.getUsername());
        }
        if (StringUtils.hasText(request.getEmail())) {
            wrapper.like(User::getEmail, request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone())) {
            wrapper.like(User::getPhone, request.getPhone());
        }

        wrapper.orderByDesc(User::getId);

        return userMapper.selectList(wrapper);
    }
}
