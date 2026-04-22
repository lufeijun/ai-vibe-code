package com.example.demo.dto;

import lombok.Data;

@Data
public class CustomerQueryRequest {
    private String phone;
    private String name;
    private String region;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
