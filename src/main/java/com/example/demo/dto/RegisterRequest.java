package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String city;
    private Boolean isEmployed;
    private LocalDate hireDate;
    private LocalDate resignationDate;
}
