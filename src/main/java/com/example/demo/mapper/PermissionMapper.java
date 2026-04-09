package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("SELECT DISTINCT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "INNER JOIN user_roles ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.enabled = true " +
            "ORDER BY p.level, p.sort_order")
    List<Permission> findPermissionsByUserId(@Param("userId") Long userId);

    @Select("SELECT DISTINCT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.enabled = true " +
            "ORDER BY p.level, p.sort_order")
    List<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT * FROM permissions WHERE parent_id = #{parentId} AND enabled = true ORDER BY sort_order")
    List<Permission> findByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM permissions WHERE enabled = true ORDER BY level, sort_order")
    List<Permission> findAllEnabled();
}
