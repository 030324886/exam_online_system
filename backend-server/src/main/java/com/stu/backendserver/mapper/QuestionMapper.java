package com.stu.backendserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stu.backendserver.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    @Select("SELECT id, type, stem, correct_answer FROM question WHERE type = #{type} ORDER BY RAND() LIMIT #{count}")
    List<Question> selectRandomByType(@Param("type") String type, @Param("count") Integer count);
}
