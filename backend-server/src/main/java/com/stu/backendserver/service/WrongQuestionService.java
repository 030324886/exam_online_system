package com.stu.backendserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stu.backendserver.entity.WrongQuestion;

public interface WrongQuestionService extends IService<WrongQuestion> {
    // 自动入库错题：存在则次数+1，不存在则新增
    boolean addOrUpdateWrong(Long userId, Long questionId);
}