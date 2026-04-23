package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.CouponQueryRequest;
import com.example.demo.entity.Coupon;
import com.example.demo.mapper.CouponMapper;
import com.example.demo.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    @Override
    public IPage<Coupon> getCouponPage(CouponQueryRequest request) {
        Page<Coupon> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getName())) {
            wrapper.like(Coupon::getName, request.getName());
        }
        if (StringUtils.hasText(request.getStatus())) {
            wrapper.eq(Coupon::getStatus, request.getStatus());
        }

        wrapper.orderByDesc(Coupon::getCreatedAt);
        return this.page(page, wrapper);
    }
}
