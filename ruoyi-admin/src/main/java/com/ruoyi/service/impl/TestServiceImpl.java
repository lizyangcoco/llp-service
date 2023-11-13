package com.ruoyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.data.TestEntity;
import com.ruoyi.mapper.TestMapper;
import com.ruoyi.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lizyang
 * @date Created in 2023/1/20 11:56
 * @description 类描述
 */
@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestMapper testMapper;


    @Override
    public List<TestEntity> getAllList() {
        LambdaQueryWrapper<TestEntity> wrapper = Wrappers.lambdaQuery(TestEntity.class);
        return testMapper.selectList(wrapper);
    }
}
