package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("customer_coupons")
@KeySequence(value = "customer_coupons_id_seq", dbType = DbType.POSTGRE_SQL)
public class CustomerCoupon {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("customer_id")
    private Long customerId;

    @TableField("coupon_id")
    private Long couponId;

    private String status;

    @TableField("received_time")
    private LocalDateTime receivedTime;

    @TableField("used_time")
    private LocalDateTime usedTime;

    @TableField("registration_id")
    private Long registrationId;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
