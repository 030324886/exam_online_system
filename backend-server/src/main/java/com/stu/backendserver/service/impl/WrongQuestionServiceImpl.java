package com.stu.backendserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stu.backendserver.entity.Question;
import com.stu.backendserver.entity.WrongQuestion;
import com.stu.backendserver.mapper.QuestionMapper;
import com.stu.backendserver.mapper.WrongQuestionMapper;
import com.stu.backendserver.service.WrongQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WrongQuestionServiceImpl extends ServiceImpl<WrongQuestionMapper, WrongQuestion> implements WrongQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public boolean addOrUpdateWrong(Long userId, Long questionId) {
        QueryWrapper<WrongQuestion> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("question_id", questionId);
        WrongQuestion exist = getOne(wrapper);

        if (exist != null) {
            // 已存在，错题次数+1
            return baseMapper.incrementWrongCount(userId, questionId) > 0;
        } else {
            // 不存在，新增
            Question question = questionMapper.selectById(questionId);
            if (question == null) {
                throw new RuntimeException("题目不存在");
            }
            WrongQuestion wq = new WrongQuestion();
            wq.setUserId(userId);
            wq.setQuestionId(questionId);
            wq.setKnowledgePoint(question.getKnowledgePoint());
            wq.setWrongCount(1);
            return save(wq);
        }
    }
}