package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private List<Long> permissionIds;
    private List<PermissionDTO> permissions;
}
