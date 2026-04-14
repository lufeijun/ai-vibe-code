package com.example.demo.dto;

import lombok.Data;

@Data
public class UserQueryRequest {
    private String username;
    private String email;
    private String phone;
    private Boolean isEmployed;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
