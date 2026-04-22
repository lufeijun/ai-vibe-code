package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserUpdateRequest {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String city;
    private Boolean isEmployed;
    private LocalDate hireDate;
    private LocalDate resignationDate;
    private List<Long> roleIds;
}
