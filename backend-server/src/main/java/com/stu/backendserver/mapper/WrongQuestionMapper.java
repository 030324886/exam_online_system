package com.stu.backendserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stu.backendserver.entity.WrongQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface WrongQuestionMapper extends BaseMapper<WrongQuestion> {
    // 错题次数+1
    @Update("UPDATE wrong_question SET wrong_count = wrong_count + 1 WHERE user_id = #{userId} AND question_id = #{questionId}")
    int incrementWrongCount(@Param("userId") Long userId, @Param("questionId") Long questionId);
}