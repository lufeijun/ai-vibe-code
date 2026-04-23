package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CustomerQueryRequest;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Registration;
import com.example.demo.service.CustomerService;
import com.example.demo.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final RegistrationService registrationService;

    @PostMapping("/list")
    public ApiResponse<IPage<Customer>> list(@RequestBody CustomerQueryRequest request) {
        IPage<Customer> page = customerService.getCustomerPage(request);
        return ApiResponse.success(page);
    }

    @GetMapping("/{id}")
    public ApiResponse<Customer> getById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        if (customer == null) {
            return ApiResponse.error("客户不存在");
        }
        return ApiResponse.success(customer);
    }

    @PostMapping("/create")
    public ApiResponse<Customer> create(@RequestBody Customer customer) {
        Customer existing = customerService.getCustomerByPhone(customer.getPhone());
        if (existing != null) {
            return ApiResponse.error("该手机号已存在");
        }
        customerService.save(customer);
        // 重新从数据库查询以确保获取正确的自增ID
        Customer savedCustomer = customerService.getById(customer.getId());
        return ApiResponse.success(savedCustomer != null ? savedCustomer : customer);
    }

    @PostMapping("/update")
    public ApiResponse<Customer> update(@RequestBody Customer customer) {
        customerService.updateById(customer);
        return ApiResponse.success(customer);
    }

    @PostMapping("/delete/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        customerService.removeById(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/search")
    public ApiResponse<List<Customer>> search(@RequestParam String phone) {
        List<Customer> customers = customerService.list(
            new LambdaQueryWrapper<Customer>().like(Customer::getPhone, phone)
        );
        return ApiResponse.success(customers);
    }

    @GetMapping("/{id}/registrations")
    public ApiResponse<List<Registration>> getRegistrations(@PathVariable Long id) {
        List<Registration> registrations = registrationService.list(
            new LambdaQueryWrapper<Registration>().eq(Registration::getCustomerId, id)
        );
        return ApiResponse.success(registrations);
    }
}
