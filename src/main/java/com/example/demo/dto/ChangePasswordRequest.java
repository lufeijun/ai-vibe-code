package com.example.demo.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private Long userId;
    private String newPassword;
}
