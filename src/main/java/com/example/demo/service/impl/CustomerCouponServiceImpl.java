package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.CustomerCoupon;
import com.example.demo.mapper.CustomerCouponMapper;
import com.example.demo.service.CustomerCouponService;
import org.springframework.stereotype.Service;

@Service
public class CustomerCouponServiceImpl extends ServiceImpl<CustomerCouponMapper, CustomerCoupon> implements CustomerCouponService {
}
