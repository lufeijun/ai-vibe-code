package com.example.demo.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class AssignRoleRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private List<Long> roleIds;
}
