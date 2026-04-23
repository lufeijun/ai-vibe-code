package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("customers")
@KeySequence(value = "customers_id_seq", dbType = DbType.POSTGRE_SQL)
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
