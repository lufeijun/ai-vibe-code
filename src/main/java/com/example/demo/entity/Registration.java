package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("registrations")
public class Registration {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("customer_id")
    private Long customerId;

    @TableField("activity_id")
    private Long activityId;

    @TableField("registration_time")
    private LocalDateTime registrationTime;

    private String status;

    private String remarks;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
