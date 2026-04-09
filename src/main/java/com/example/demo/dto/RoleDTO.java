package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> permissionIds;
    private List<PermissionDTO> permissions;
}
