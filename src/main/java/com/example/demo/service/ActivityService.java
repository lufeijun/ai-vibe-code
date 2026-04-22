package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.ActivityQueryRequest;
import com.example.demo.entity.Activity;

public interface ActivityService extends IService<Activity> {
    IPage<Activity> getActivityPage(ActivityQueryRequest request);
}
