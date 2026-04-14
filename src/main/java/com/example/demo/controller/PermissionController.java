package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PermissionTreeDTO;
import com.example.demo.dto.PermissionSortUpdateRequest;
import com.example.demo.entity.Permission;
import com.example.demo.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/tree")
    public ApiResponse<List<PermissionTreeDTO>> getTree() {
        List<PermissionTreeDTO> tree = permissionService.getPermissionTree();
        return ApiResponse.success(tree);
    }

    @GetMapping("/list")
    public ApiResponse<List<Permission>> list() {
        List<Permission> permissions = permissionService.list(
                new LambdaQueryWrapper<Permission>()
                        .orderByAsc(Permission::getLevel)
                        .orderByAsc(Permission::getSortOrder)
        );
        return ApiResponse.success(permissions);
    }

    @PostMapping("/batch-update-sort")
    public ApiResponse<Void> batchUpdateSort(@RequestBody PermissionSortUpdateRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return ApiResponse.error("参数不能为空");
        }

        for (PermissionSortUpdateRequest.PermissionSortItem item : request.getItems()) {
            Permission permission = permissionService.getById(item.getId());
            if (permission != null) {
                permission.setSortOrder(item.getSortOrder());
                permissionService.updateById(permission);
            }
        }

        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<Permission> getById(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        if (permission == null) {
            return ApiResponse.error("权限不存在");
        }
        return ApiResponse.success(permission);
    }

    @PostMapping
    public ApiResponse<Permission> create(@Valid @RequestBody Permission permission) {
        permissionService.save(permission);
        return ApiResponse.success(permission);
    }

    @PutMapping("/{id}")
    public ApiResponse<Permission> update(@PathVariable Long id, @Valid @RequestBody Permission permission) {
        permission.setId(id);
        permissionService.updateById(permission);
        return ApiResponse.success(permission);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionService.removeById(id);
        return ApiResponse.success(null);
    }
}
