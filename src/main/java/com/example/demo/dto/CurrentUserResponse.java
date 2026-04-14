package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserResponse {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private List<String> roles;
    private List<String> permissions;
    private List<PermissionTreeDTO> permissionTree;
}
