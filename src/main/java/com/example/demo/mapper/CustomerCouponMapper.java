package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.CustomerCoupon;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerCouponMapper extends BaseMapper<CustomerCoupon> {
}
