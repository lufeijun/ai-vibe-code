package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class PermissionSortUpdateRequest {
    private List<PermissionSortItem> items;

    @Data
    public static class PermissionSortItem {
        private Long id;
        private Integer sortOrder;
    }
}
