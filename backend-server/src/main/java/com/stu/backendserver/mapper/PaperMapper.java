package com.stu.backendserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stu.backendserver.entity.Paper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaperMapper extends BaseMapper<Paper> {
}
