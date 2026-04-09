package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.PermissionTreeDTO;
import com.example.demo.entity.Permission;
import java.util.List;

public interface PermissionService extends IService<Permission> {

    List<Permission> getPermissionsByUserId(Long userId);

    List<Permission> getPermissionsByRoleId(Long roleId);

    List<PermissionTreeDTO> getPermissionTree();

    List<String> getPermissionCodesByUserId(Long userId);
}
