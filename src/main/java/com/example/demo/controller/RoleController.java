package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AssignPermissionRequest;
import com.example.demo.dto.AssignRoleRequest;
import com.example.demo.dto.RoleDTO;
import com.example.demo.entity.Role;
import com.example.demo.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/list")
    public ApiResponse<List<Role>> list() {
        List<Role> roles = roleService.list(
                new LambdaQueryWrapper<Role>().eq(Role::getEnabled, true)
        );
        return ApiResponse.success(roles);
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleDTO> getDetail(@PathVariable Long id) {
        RoleDTO roleDTO = roleService.getRoleDetail(id);
        if (roleDTO == null) {
            return ApiResponse.error("角色不存在");
        }
        return ApiResponse.success(roleDTO);
    }

    @PostMapping
    public ApiResponse<Role> create(@Valid @RequestBody Role role) {
        roleService.save(role);
        return ApiResponse.success(role);
    }

    @PutMapping("/{id}")
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
