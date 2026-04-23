package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Activity;
import com.example.demo.entity.Coupon;
import com.example.demo.entity.CustomerCoupon;
import com.example.demo.service.ActivityService;
import com.example.demo.service.CouponService;
import com.example.demo.service.CustomerCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/customer-coupon")
@RequiredArgsConstructor
public class CustomerCouponController {

    private final CustomerCouponService customerCouponService;
    private final CouponService couponService;
    private final ActivityService activityService;

    @PostMapping("/list/{customerId}")
    public ApiResponse<List<CustomerCoupon>> listByCustomer(@PathVariable Long customerId) {
        List<CustomerCoupon> list = customerCouponService.list(
            new LambdaQueryWrapper<CustomerCoupon>()
                .eq(CustomerCoupon::getCustomerId, customerId)
                .orderByDesc(CustomerCoupon::getCreatedAt)
        );
        return ApiResponse.success(list);
    }

    @PostMapping("/available/{customerId}/{activityId}")
    public ApiResponse<List<CustomerCoupon>> getAvailable(
            @PathVariable Long customerId,
            @PathVariable Long activityId) {

        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            return ApiResponse.error("活动不存在");
        }

        BigDecimal activityFee = activity.getFee() != null ? activity.getFee() : BigDecimal.ZERO;
        LocalDate today = LocalDate.now();

        List<CustomerCoupon> customerCoupons = customerCouponService.list(
            new LambdaQueryWrapper<CustomerCoupon>()
                .eq(CustomerCoupon::getCustomerId, customerId)
                .eq(CustomerCoupon::getStatus, "未使用")
        );

        List<CustomerCoupon> availableList = new ArrayList<>();
        for (CustomerCoupon cc : customerCoupons) {
            Coupon coupon = couponService.getById(cc.getCouponId());
            if (coupon != null
                    && "启用".equals(coupon.getStatus())
                    && !today.isBefore(coupon.getValidStart())
                    && !today.isAfter(coupon.getValidEnd())
                    && activityFee.compareTo(coupon.getMinAmount()) >= 0) {
                availableList.add(cc);
            }
        }

        return ApiResponse.success(availableList);
    }
}
