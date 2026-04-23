package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("coupons")
@KeySequence(value = "coupons_id_seq", dbType = DbType.POSTGRE_SQL)
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("min_amount")
    private BigDecimal minAmount;

    @TableField("total_quantity")
    private Integer totalQuantity;

    @TableField("used_quantity")
    private Integer usedQuantity;

    @TableField("limit_per_customer")
    private Integer limitPerCustomer;

    @TableField("valid_start")
    private LocalDate validStart;

    @TableField("valid_end")
    private LocalDate validEnd;

    private String status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
