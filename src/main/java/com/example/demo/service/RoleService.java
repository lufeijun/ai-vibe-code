package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.RoleDTO;
import com.example.demo.dto.RoleQueryRequest;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import java.util.List;

public interface RoleService extends IService<Role> {

    List<Role> getRolesByUserId(Long userId);

    RoleDTO getRoleDetail(Long roleId);

    void assignPermissions(Long roleId, List<Long> permissionIds);

    void assignRolesToUser(Long userId, List<Long> roleIds);

    IPage<Role> getRoleList(RoleQueryRequest request);

    List<Permission> getPermissionsByRoleId(Long roleId);
}
