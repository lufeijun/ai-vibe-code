package com.example.demo.dto;

import com.example.demo.entity.Role;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserWithRolesDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String city;
    private Boolean isEmployed;
    private LocalDate hireDate;
    private LocalDate resignationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Role> roles;
}
