package com.stu.backendserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stu.backendserver.entity.Question;
import java.util.List;

public interface QuestionService extends IService<Question> {
    // 根据知识点查询题目
    List<Question> listByKnowledge(String knowledgePoint);
}