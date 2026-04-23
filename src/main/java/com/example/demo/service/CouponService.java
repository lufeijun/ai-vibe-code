package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.CouponQueryRequest;
import com.example.demo.entity.Coupon;

public interface CouponService extends IService<Coupon> {
    IPage<Coupon> getCouponPage(CouponQueryRequest request);
}
