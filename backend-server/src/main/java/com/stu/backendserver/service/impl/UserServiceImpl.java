package com.stu.backendserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stu.backendserver.entity.User;
import com.stu.backendserver.mapper.UserMapper;
import com.stu.backendserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        wrapper.eq("password", user.getPassword());
        return userMapper.selectOne(wrapper);
    }
}