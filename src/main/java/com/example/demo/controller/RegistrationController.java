package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Activity;
import com.example.demo.entity.Coupon;
import com.example.demo.entity.CustomerCoupon;
import com.example.demo.entity.FollowUp;
import com.example.demo.entity.Registration;
import com.example.demo.service.ActivityService;
import com.example.demo.service.CouponService;
import com.example.demo.service.CustomerCouponService;
import com.example.demo.service.FollowUpService;
import com.example.demo.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final ActivityService activityService;
    private final FollowUpService followUpService;
    private final CouponService couponService;
    private final CustomerCouponService customerCouponService;

    @PostMapping("/create")
    @Transactional
    public ApiResponse<Registration> create(@RequestBody Registration registration) {
        Activity activity = activityService.getById(registration.getActivityId());
        if (activity == null) {
            return ApiResponse.error("活动不存在");
        }

        LocalDate today = LocalDate.now();
        if (today.isBefore(activity.getRegistrationStart()) || today.isAfter(activity.getRegistrationEnd())) {
            return ApiResponse.error("不在报名时间范围内");
        }

        registration.setRegistrationTime(LocalDateTime.now());
        registration.setStatus("已报名");
        registrationService.save(registration);

        if (registration.getCustomerCouponId() != null) {
            CustomerCoupon customerCoupon = customerCouponService.getById(registration.getCustomerCouponId());
            if (customerCoupon != null && "未使用".equals(customerCoupon.getStatus())) {
                customerCoupon.setStatus("已使用");
                customerCoupon.setUsedTime(LocalDateTime.now());
                customerCoupon.setRegistrationId(registration.getId());
                customerCouponService.updateById(customerCoupon);

                Coupon coupon = couponService.getById(customerCoupon.getCouponId());
                if (coupon != null) {
                    coupon.setUsedQuantity(coupon.getUsedQuantity() + 1);
                    couponService.updateById(coupon);
                }
            }
        }

        // 重新从数据库查询以确保获取正确的自增ID
        Registration savedRegistration = registrationService.getById(registration.getId());
        return ApiResponse.success(savedRegistration != null ? savedRegistration : registration);
    }

    @PostMapping("/update")
    public ApiResponse<Registration> update(@RequestBody Registration registration) {
        registrationService.updateById(registration);
        return ApiResponse.success(registration);
    }

    @GetMapping("/{id}")
    public ApiResponse<Registration> getById(@PathVariable Long id) {
        Registration registration = registrationService.getById(id);
        if (registration == null) {
            return ApiResponse.error("报名记录不存在");
        }
        return ApiResponse.success(registration);
    }

    @GetMapping("/{id}/followups")
    public ApiResponse<List<FollowUp>> getFollowUps(@PathVariable Long id) {
        List<FollowUp> followUps = followUpService.list(
            new LambdaQueryWrapper<FollowUp>().eq(FollowUp::getRegistrationId, id).orderByDesc(FollowUp::getCreatedAt)
        );
        return ApiResponse.success(followUps);
    }
}
