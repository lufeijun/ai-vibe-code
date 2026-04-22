package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.FollowUp;
import com.example.demo.service.FollowUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/followup")
@RequiredArgsConstructor
public class FollowUpController {

    private final FollowUpService followUpService;

    @PostMapping("/create")
    public ApiResponse<FollowUp> create(@RequestBody FollowUp followUp) {
        followUpService.save(followUp);
        return ApiResponse.success(followUp);
    }

    @PostMapping("/update")
    public ApiResponse<FollowUp> update(@RequestBody FollowUp followUp) {
        followUpService.updateById(followUp);
        return ApiResponse.success(followUp);
    }

    @GetMapping("/{id}")
    public ApiResponse<FollowUp> getById(@PathVariable Long id) {
        FollowUp followUp = followUpService.getById(id);
        if (followUp == null) {
            return ApiResponse.error("跟进记录不存在");
        }
        return ApiResponse.success(followUp);
    }
}
