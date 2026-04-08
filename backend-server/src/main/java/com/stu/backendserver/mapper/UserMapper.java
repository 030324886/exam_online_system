package com.stu.backendserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stu.backendserver.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}