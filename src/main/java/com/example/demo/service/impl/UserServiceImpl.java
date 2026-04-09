package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserQueryRequest;
import com.example.demo.dto.UserWithRolesDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserRoleMapper;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final UserRoleMapper userRoleMapper;

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
    public IPage<UserWithRolesDTO> getUserList(UserQueryRequest request) {
        // Create wrapper for count query (without ORDER BY)
        LambdaQueryWrapper<User> countWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getUsername())) {
            countWrapper.like(User::getUsername, request.getUsername());
        }
        if (StringUtils.hasText(request.getEmail())) {
            countWrapper.like(User::getEmail, request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone())) {
            countWrapper.like(User::getPhone, request.getPhone());
        }

        // Manually query total count (without ORDER BY)
        long total = userMapper.selectCount(countWrapper);

        // Create wrapper for data query (with ORDER BY and LIMIT)
        LambdaQueryWrapper<User> dataWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getUsername())) {
            dataWrapper.like(User::getUsername, request.getUsername());
        }
        if (StringUtils.hasText(request.getEmail())) {
            dataWrapper.like(User::getEmail, request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone())) {
            dataWrapper.like(User::getPhone, request.getPhone());
        }

        dataWrapper.orderByDesc(User::getId);

        // Calculate offset
        int offset = (request.getPageNum() - 1) * request.getPageSize();

        // Query data with limit
        dataWrapper.last("LIMIT " + request.getPageSize() + " OFFSET " + offset);
        List<User> userList = userMapper.selectList(dataWrapper);

        // Convert User to UserWithRolesDTO
        List<UserWithRolesDTO> dtoList = userList.stream().map(user -> {
            UserWithRolesDTO dto = new UserWithRolesDTO();
            BeanUtils.copyProperties(user, dto);
            return dto;
        }).collect(Collectors.toList());

        // Batch query roles for all users
        if (!dtoList.isEmpty()) {
            List<Long> userIds = dtoList.stream().map(UserWithRolesDTO::getId).collect(Collectors.toList());

            // Get all user-role mappings
            List<UserRole> userRoles = userRoleMapper.findByUserIds(userIds);

            if (!CollectionUtils.isEmpty(userRoles)) {
                // Get all unique role ids
                Set<Long> roleIds = userRoles.stream()
                        .map(UserRole::getRoleId)
                        .collect(Collectors.toSet());

                // Batch query all roles
                List<Role> roles = roleService.listByIds(roleIds);
                Map<Long, Role> roleMap = roles.stream()
                        .collect(Collectors.toMap(Role::getId, r -> r));

                // Group roles by user id
                Map<Long, List<Role>> userRolesMap = new HashMap<>();
                for (UserRole ur : userRoles) {
                    Role role = roleMap.get(ur.getRoleId());
                    if (role != null) {
                        userRolesMap.computeIfAbsent(ur.getUserId(), k -> new ArrayList<>()).add(role);
                    }
                }

                // Set roles for each DTO
                for (UserWithRolesDTO dto : dtoList) {
                    dto.setRoles(userRolesMap.getOrDefault(dto.getId(), Collections.emptyList()));
                }
            } else {
                // No roles found, set empty list
                for (UserWithRolesDTO dto : dtoList) {
                    dto.setRoles(Collections.emptyList());
                }
            }
        }

        // Calculate total pages
        long pages = total / request.getPageSize();
        if (total % request.getPageSize() != 0) {
            pages++;
        }

        // Create result page
        Page<UserWithRolesDTO> resultPage = new Page<>();
        resultPage.setRecords(dtoList);
        resultPage.setTotal(total);
        resultPage.setSize(request.getPageSize());
        resultPage.setCurrent(request.getPageNum());
        resultPage.setPages(pages);

        System.out.println("========== Debug Pagination Info ==========");
        System.out.println("total: " + total);
        System.out.println("size: " + request.getPageSize());
        System.out.println("current: " + request.getPageNum());
        System.out.println("pages: " + pages);
        System.out.println("records.size(): " + dtoList.size());

        return resultPage;
    }
}
