package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.FollowUp;
import com.example.demo.mapper.FollowUpMapper;
import com.example.demo.service.FollowUpService;
import org.springframework.stereotype.Service;

@Service
public class FollowUpServiceImpl extends ServiceImpl<FollowUpMapper, FollowUp> implements FollowUpService {
}
