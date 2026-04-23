package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("users")
@KeySequence(value = "users_id_seq", dbType = DbType.POSTGRE_SQL)
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String city;

    @TableField("is_employed")
    private Boolean isEmployed;

    @TableField("hire_date")
    private LocalDate hireDate;

    @TableField("resignation_date")
    private LocalDate resignationDate;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
