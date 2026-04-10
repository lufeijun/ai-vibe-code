package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AssignPermissionRequest;
import com.example.demo.dto.AssignRoleRequest;
import com.example.demo.dto.RoleDTO;
import com.example.demo.dto.RoleQueryRequest;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;


    @PostMapping("/list")
    public ApiResponse<IPage<Role>> list(@RequestBody RoleQueryRequest request) {
        try {
            IPage<Role> page = roleService.getRoleList(request);
            return ApiResponse.success(page);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleDTO> getDetail(@PathVariable Long id) {
        RoleDTO roleDTO = roleService.getRoleDetail(id);
        if (roleDTO == null) {
            return ApiResponse.error("角色不存在");
        }
        return ApiResponse.success(roleDTO);
    }

    @PostMapping("/get/permissions")
    public ApiResponse<List<Permission>> getPermissionsByRoleId(@Valid @RequestBody AssignPermissionRequest request) {
        if (request.getRoleId() == null) {
            return ApiResponse.error("角色ID不能为空");
        }
        List<Permission> permissions = roleService.getPermissionsByRoleId(request.getRoleId());
        return ApiResponse.success(permissions);
    }

    @PostMapping("/add")
    public ApiResponse<Role> create(@RequestBody Role role) {
        // 校验 name 必填
        if (!StringUtils.hasText(role.getName())) {
            return ApiResponse.error("角色名称不能为空");
        }

        // 如果 code 没有传，自动生成随机字符串
        if (!StringUtils.hasText(role.getCode())) {
            role.setCode(UUID.randomUUID().toString().replace("-", ""));
        }

        // enabled 默认为空，不做处理

        roleService.save(role);
        return ApiResponse.success(role);
    }

    @PostMapping("update/{id}")
    public ApiResponse<Role> update(@PathVariable Long id, @Valid @RequestBody Role role) {
        role.setId(id);
        roleService.updateById(role);
        return ApiResponse.success(role);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/assign-permissions")
    public ApiResponse<Void> assignPermissions(@Valid @RequestBody AssignPermissionRequest request) {
        roleService.assignPermissions(request.getRoleId(), request.getPermissionIds());
        return ApiResponse.success(null);
    }

    @PostMapping("/assign-roles")
    public ApiResponse<Void> assignRolesToUser(@Valid @RequestBody AssignRoleRequest request) {
        roleService.assignRolesToUser(request.getUserId(), request.getRoleIds());
        return ApiResponse.success(null);
    }
}
