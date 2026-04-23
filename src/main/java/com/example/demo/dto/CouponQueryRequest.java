package com.example.demo.dto;

import lombok.Data;

@Data
public class CouponQueryRequest {
    private String name;
    private String status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
