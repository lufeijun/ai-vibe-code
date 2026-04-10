package com.example.demo.dto;

import lombok.Data;

@Data
public class RoleQueryRequest {
    private String name;
    private String code;
    private Boolean enabled;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
