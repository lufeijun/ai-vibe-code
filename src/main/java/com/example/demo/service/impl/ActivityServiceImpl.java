package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.ActivityQueryRequest;
import com.example.demo.entity.Activity;
import com.example.demo.mapper.ActivityMapper;
import com.example.demo.service.ActivityService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Override
    public IPage<Activity> getActivityPage(ActivityQueryRequest request) {
        Page<Activity> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getName())) {
            wrapper.like(Activity::getName, request.getName());
        }
        if (StringUtils.hasText(request.getStatus())) {
            wrapper.eq(Activity::getStatus, request.getStatus());
        }

        wrapper.orderByDesc(Activity::getCreatedAt);
        return this.page(page, wrapper);
    }
}
