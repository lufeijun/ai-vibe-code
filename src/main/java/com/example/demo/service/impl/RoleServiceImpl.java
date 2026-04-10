package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.PermissionDTO;
import com.example.demo.dto.RoleDTO;
import com.example.demo.dto.RoleQueryRequest;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.RolePermission;
import com.example.demo.entity.UserRole;
import com.example.demo.mapper.PermissionMapper;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.mapper.RolePermissionMapper;
import com.example.demo.mapper.UserRoleMapper;
import com.example.demo.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return roleMapper.findRolesByUserId(userId);
    }

    @Override
    public RoleDTO getRoleDetail(Long roleId) {
        Role role = getById(roleId);
        if (role == null) {
            return null;
        }

        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(role, roleDTO);

        List<Permission> permissions = permissionMapper.findPermissionsByRoleId(roleId);
        List<PermissionDTO> permissionDTOs = permissions.stream().map(p -> {
            PermissionDTO dto = new PermissionDTO();
            BeanUtils.copyProperties(p, dto);
            return dto;
        }).collect(Collectors.toList());

        roleDTO.setPermissions(permissionDTOs);
        roleDTO.setPermissionIds(permissions.stream().map(Permission::getId).collect(Collectors.toList()));

        return roleDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.deleteByRoleId(roleId);

        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.batchInsert(roleId, permissionIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        userRoleMapper.deleteByUserId(userId);

        if (roleIds != null && !roleIds.isEmpty()) {
            userRoleMapper.batchInsert(userId, roleIds);
        }
    }

    @Override
    public IPage<Role> getRoleList(RoleQueryRequest request) {

        Page<Role> resultPage = new Page<>(request.getPageNum(), request.getPageSize());

    
        // Lambda 条件构造器
        LambdaQueryWrapper<Role> dataWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getName())) {
            dataWrapper.like(Role::getName, request.getName());
        }
        if (StringUtils.hasText(request.getCode())) {
            dataWrapper.like(Role::getCode, request.getCode());
        }
        if (request.getEnabled() != null) {
            dataWrapper.eq(Role::getEnabled, request.getEnabled());
        }

        dataWrapper.orderByDesc(Role::getId);

        System.out.println("dataWrapper: ==============");


        return roleMapper.selectPage(resultPage, dataWrapper);
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.findPermissionsByRoleId(roleId);
    }

}
