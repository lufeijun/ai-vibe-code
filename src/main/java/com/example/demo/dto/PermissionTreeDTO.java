package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class PermissionTreeDTO {
    private Long id;
    private Long parentId;
    private String name;
    private String code;
    private String type;
    private Integer level;
    private String path;
    private String icon;
    private Integer sortOrder;
    private Boolean enabled;
    private List<PermissionTreeDTO> children;
}
