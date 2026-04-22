package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.dto.ActivityQueryRequest;
import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Activity;
import com.example.demo.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping("/list")
    public ApiResponse<IPage<Activity>> list(@RequestBody ActivityQueryRequest request) {
        IPage<Activity> page = activityService.getActivityPage(request);
        return ApiResponse.success(page);
    }

    @GetMapping("/{id}")
    public ApiResponse<Activity> getById(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return ApiResponse.error("活动不存在");
        }
        return ApiResponse.success(activity);
    }

    @PostMapping("/create")
    public ApiResponse<Activity> create(@RequestBody Activity activity) {
        activityService.save(activity);
        return ApiResponse.success(activity);
    }

    @PostMapping("/update")
    public ApiResponse<Activity> update(@RequestBody Activity activity) {
        activityService.updateById(activity);
        return ApiResponse.success(activity);
    }

    @PostMapping("/delete/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        activityService.removeById(id);
        return ApiResponse.success(null);
    }
}
