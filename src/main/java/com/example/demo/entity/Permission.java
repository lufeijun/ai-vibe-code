package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("permissions")
@KeySequence(value = "permissions_id_seq", dbType = DbType.POSTGRE_SQL)
public class Permission {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("parent_id")
    private Long parentId;

    private String name;

    private String code;

    private String type;

    private Integer level;

    private String path;

    private String icon;

    @TableField("sort_order")
    private Integer sortOrder;

    private Boolean enabled;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
