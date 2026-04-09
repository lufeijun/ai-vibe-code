package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.PermissionTreeDTO;
import com.example.demo.entity.Permission;
import com.example.demo.mapper.PermissionMapper;
import com.example.demo.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public List<Permission> getPermissionsByUserId(Long userId) {
        return permissionMapper.findPermissionsByUserId(userId);
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.findPermissionsByRoleId(roleId);
    }

    @Override
    public List<PermissionTreeDTO> getPermissionTree() {
        List<Permission> allPermissions = permissionMapper.findAllEnabled();
        return buildTree(allPermissions, 0L);
    }

    @Override
    public List<String> getPermissionCodesByUserId(Long userId) {
        List<Permission> permissions = getPermissionsByUserId(userId);
        return permissions.stream()
                .map(Permission::getCode)
                .filter(code -> code != null && !code.isEmpty())
                .collect(Collectors.toList());
    }

    private List<PermissionTreeDTO> buildTree(List<Permission> permissions, Long parentId) {
        List<PermissionTreeDTO> tree = new ArrayList<>();

        for (Permission permission : permissions) {
            if (parentId.equals(permission.getParentId())) {
                PermissionTreeDTO dto = new PermissionTreeDTO();
                BeanUtils.copyProperties(permission, dto);
                dto.setChildren(buildTree(permissions, permission.getId()));
                tree.add(dto);
            }
        }

        return tree;
    }
}
