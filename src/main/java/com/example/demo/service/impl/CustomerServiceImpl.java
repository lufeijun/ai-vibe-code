package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.CustomerQueryRequest;
import com.example.demo.entity.Customer;
import com.example.demo.mapper.CustomerMapper;
import com.example.demo.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public IPage<Customer> getCustomerPage(CustomerQueryRequest request) {
        Page<Customer> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getPhone())) {
            wrapper.like(Customer::getPhone, request.getPhone());
        }
        if (StringUtils.hasText(request.getName())) {
            wrapper.like(Customer::getName, request.getName());
        }
        if (StringUtils.hasText(request.getRegion())) {
            wrapper.like(Customer::getRegion, request.getRegion());
        }

        wrapper.orderByDesc(Customer::getCreatedAt);
        return this.page(page, wrapper);
    }

    @Override
    public Customer getCustomerByPhone(String phone) {
        return this.getOne(new LambdaQueryWrapper<Customer>().eq(Customer::getPhone, phone));
    }
}
