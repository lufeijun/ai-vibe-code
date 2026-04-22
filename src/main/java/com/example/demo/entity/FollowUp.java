package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("follow_ups")
public class FollowUp {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("registration_id")
    private Long registrationId;

    @TableField("follow_up_time")
    private LocalDateTime followUpTime;

    @TableField("follower_id")
    private Long followerId;

    private String content;

    private String method;

    @TableField("next_follow_up")
    private LocalDateTime nextFollowUp;

    private String status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
