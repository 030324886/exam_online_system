package com.stu.backendserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stu.backendserver.entity.Question;
import com.stu.backendserver.mapper.QuestionMapper;
import com.stu.backendserver.service.QuestionService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Override
    public List<Question> listByKnowledge(String knowledgePoint) {
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.eq("knowledge_point", knowledgePoint);
        return list(wrapper);
    }
}