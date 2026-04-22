package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.CustomerQueryRequest;
import com.example.demo.entity.Customer;

public interface CustomerService extends IService<Customer> {
    IPage<Customer> getCustomerPage(CustomerQueryRequest request);
    Customer getCustomerByPhone(String phone);
}
