package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("customers")
public class Customer {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String phone;

    private String name;

    private String hobby;

    private String region;

    @TableField("emergency_contact")
    private String emergencyContact;

    @TableField("referrer_id")
    private Long referrerId;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
