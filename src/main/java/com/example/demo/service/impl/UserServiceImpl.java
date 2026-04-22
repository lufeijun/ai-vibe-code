package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserQueryRequest;
import com.example.demo.dto.UserUpdateRequest;
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
    public User getById(Long id) {
        return userMapper.selectById(id);
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
        // 创建分页对象
        Page<User> userPage = new Page<>(request.getPageNum(), request.getPageSize());

        // 构建查询条件
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
        if (request.getIsEmployed() != null) {
            dataWrapper.eq(User::getIsEmployed, request.getIsEmployed());
        }

        dataWrapper.orderByDesc(User::getId);

        // 使用 MyBatis-Plus 分页查询
        IPage<User> resultPage = userMapper.selectPage(userPage, dataWrapper);

        // 转换 User 为 UserWithRolesDTO
        List<UserWithRolesDTO> dtoList = resultPage.getRecords().stream().map(user -> {
            UserWithRolesDTO dto = new UserWithRolesDTO();
            BeanUtils.copyProperties(user, dto);
            return dto;
        }).collect(Collectors.toList());

        // 批量查询所有用户的角色
        if (!dtoList.isEmpty()) {
            List<Long> userIds = dtoList.stream().map(UserWithRolesDTO::getId).collect(Collectors.toList());

            // 获取所有用户-角色映射
            List<UserRole> userRoles = userRoleMapper.findByUserIds(userIds);

            if (!CollectionUtils.isEmpty(userRoles)) {
                // 获取所有唯一的角色ID
                Set<Long> roleIds = userRoles.stream()
                        .map(UserRole::getRoleId)
                        .collect(Collectors.toSet());

                // 批量查询所有角色
                List<Role> roles = roleService.listByIds(roleIds);
                Map<Long, Role> roleMap = roles.stream()
                        .collect(Collectors.toMap(Role::getId, r -> r));

                // 按用户ID分组角色
                Map<Long, List<Role>> userRolesMap = new HashMap<>();
                for (UserRole ur : userRoles) {
                    Role role = roleMap.get(ur.getRoleId());
                    if (role != null) {
                        userRolesMap.computeIfAbsent(ur.getUserId(), k -> new ArrayList<>()).add(role);
                    }
                }

                // 为每个 DTO 设置角色
                for (UserWithRolesDTO dto : dtoList) {
                    dto.setRoles(userRolesMap.getOrDefault(dto.getId(), Collections.emptyList()));
                }
            } else {
                // 没有找到角色，设置空列表
                for (UserWithRolesDTO dto : dtoList) {
                    dto.setRoles(Collections.emptyList());
                }
            }
        }

        // 创建结果分页对象
        Page<UserWithRolesDTO> dtoPage = new Page<>();
        dtoPage.setRecords(dtoList);
        dtoPage.setTotal(resultPage.getTotal());
        dtoPage.setSize(resultPage.getSize());
        dtoPage.setCurrent(resultPage.getCurrent());
        // dtoPage.setPages(resultPage.getPages());

        return dtoPage;
    }

    @Override
    public User createUser(UserCreateRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (StringUtils.hasText(request.getEmail()) && existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        if (StringUtils.hasText(request.getPhone()) && existsByPhone(request.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword() != null ? request.getPassword() : "123456"));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setCity(request.getCity());
        user.setIsEmployed(request.getIsEmployed() != null ? request.getIsEmployed() : true);
        user.setHireDate(request.getHireDate());
        user.setResignationDate(request.getResignationDate());

        userMapper.insert(user);

        // 分配角色
        if (!CollectionUtils.isEmpty(request.getRoleIds())) {
            assignRoles(user.getId(), request.getRoleIds());
        }

        return user;
    }

    @Override
    public User updateUser(UserUpdateRequest request) {
        User user = getById(request.getId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户名唯一性（排除当前用户）
        if (StringUtils.hasText(request.getUsername())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, request.getUsername()).ne(User::getId, request.getId());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("用户名已被其他用户使用");
            }
            user.setUsername(request.getUsername());
        }

        // 检查邮箱唯一性（排除当前用户）
        if (StringUtils.hasText(request.getEmail())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, request.getEmail()).ne(User::getId, request.getId());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
            user.setEmail(request.getEmail());
        }

        // 检查手机号唯一性（排除当前用户）
        if (StringUtils.hasText(request.getPhone())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, request.getPhone()).ne(User::getId, request.getId());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("手机号已被其他用户使用");
            }
            user.setPhone(request.getPhone());
        }

        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getIsEmployed() != null) {
            user.setIsEmployed(request.getIsEmployed());
        }
        if (request.getHireDate() != null) {
            user.setHireDate(request.getHireDate());
        }
        if (request.getResignationDate() != null) {
            user.setResignationDate(request.getResignationDate());
        }

        userMapper.updateById(user);

        // 更新角色
        if (request.getRoleIds() != null) {
            assignRoles(user.getId(), request.getRoleIds());
        }

        return user;
    }

    @Override
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 先删除用户原有角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);

        // 添加新角色
        if (!CollectionUtils.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    public void changePassword(Long userId, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!StringUtils.hasText(newPassword)) {
            throw new RuntimeException("密码不能为空");
        }

        if (newPassword.length() < 6) {
            throw new RuntimeException("密码长度不能少于6位");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }
}
