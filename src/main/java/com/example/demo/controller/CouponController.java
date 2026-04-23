package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CouponQueryRequest;
import com.example.demo.entity.Coupon;
import com.example.demo.entity.CustomerCoupon;
import com.example.demo.entity.Customer;
import com.example.demo.service.CouponService;
import com.example.demo.service.CustomerCouponService;
import com.example.demo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final CustomerCouponService customerCouponService;
    private final CustomerService customerService;

    @PostMapping("/list")
    public ApiResponse<IPage<Coupon>> list(@RequestBody CouponQueryRequest request) {
        IPage<Coupon> page = couponService.getCouponPage(request);
        return ApiResponse.success(page);
    }

    @GetMapping("/{id}")
    public ApiResponse<Coupon> getById(@PathVariable Long id) {
        Coupon coupon = couponService.getById(id);
        if (coupon == null) {
            return ApiResponse.error("优惠券不存在");
        }
        return ApiResponse.success(coupon);
    }

    @PostMapping("/create")
    public ApiResponse<Coupon> create(@RequestBody Coupon coupon) {
        coupon.setUsedQuantity(0);
        couponService.save(coupon);
        // 重新从数据库查询以确保获取正确的自增ID
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getCode, coupon.getCode());
        Coupon savedCoupon = couponService.getOne(wrapper);
        return ApiResponse.success(savedCoupon != null ? savedCoupon : coupon);
    }

    @PostMapping("/update")
    public ApiResponse<Coupon> update(@RequestBody Coupon coupon) {
        couponService.updateById(coupon);
        return ApiResponse.success(coupon);
    }

    @PostMapping("/delete/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        couponService.removeById(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/issue/{couponId}/{customerId}")
    @Transactional
    public ApiResponse<CustomerCoupon> issue(@PathVariable Long couponId, @PathVariable Long customerId) {
        Coupon coupon = couponService.getById(couponId);
        if (coupon == null) {
            return ApiResponse.error("优惠券不存在");
        }
        if (!"启用".equals(coupon.getStatus())) {
            return ApiResponse.error("优惠券已禁用");
        }

        Customer customer = customerService.getById(customerId);
        if (customer == null) {
            return ApiResponse.error("客户不存在");
        }

        long received = customerCouponService.count(
            new LambdaQueryWrapper<CustomerCoupon>()
                .eq(CustomerCoupon::getCustomerId, customerId)
                .eq(CustomerCoupon::getCouponId, couponId)
        );
        if (received >= coupon.getLimitPerCustomer()) {
            return ApiResponse.error("已超过限领次数");
        }

        if (coupon.getUsedQuantity() >= coupon.getTotalQuantity()) {
            return ApiResponse.error("优惠券已发放完");
        }

        CustomerCoupon customerCoupon = new CustomerCoupon();
        customerCoupon.setCustomerId(customerId);
        customerCoupon.setCouponId(couponId);
        customerCoupon.setStatus("未使用");
        customerCoupon.setReceivedTime(LocalDateTime.now());
        customerCouponService.save(customerCoupon);

        return ApiResponse.success(customerCoupon);
    }

    @PostMapping("/receive/{couponId}/{customerId}")
    public ApiResponse<CustomerCoupon> receive(@PathVariable Long couponId, @PathVariable Long customerId) {
        return issue(couponId, customerId);
    }
}
