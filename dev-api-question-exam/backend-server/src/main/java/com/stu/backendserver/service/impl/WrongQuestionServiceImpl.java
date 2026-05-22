package com.stu.backendserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stu.backendserver.entity.Question;
import com.stu.backendserver.entity.WrongQuestion;
import com.stu.backendserver.mapper.QuestionMapper;
import com.stu.backendserver.mapper.WrongQuestionMapper;
import com.stu.backendserver.service.WrongQuestionService;
import com.stu.backendserver.vo.WrongKnowledgeStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            return baseMapper.incrementWrongCount(userId, questionId) > 0;
        } else {
            Question question = questionMapper.selectById(questionId);
            if (question == null) throw new RuntimeException("题目不存在");

            WrongQuestion wq = new WrongQuestion();
            wq.setUserId(userId);
            wq.setQuestionId(questionId);
            wq.setKnowledgePoint(question.getKnowledgePoint());
            wq.setWrongCount(1);
            return save(wq);
        }
    }

    // ====================== 第四周核心代码：按知识点统计错题 ======================
    @Override
    public List<WrongKnowledgeStatsVO> statsWrongByKnowledge(Long userId) {
        // 1. 查询该学生所有错题
        List<WrongQuestion> wrongList = list(new QueryWrapper<WrongQuestion>().eq("user_id", userId));

        // 2. 按知识点分组统计
        Map<String, Integer> countMap = new HashMap<>();
        for (WrongQuestion wq : wrongList) {
            String kp = wq.getKnowledgePoint();
            if (kp == null || kp.isBlank()) continue;

            countMap.put(kp, countMap.getOrDefault(kp, 0) + wq.getWrongCount());
        }

        // 3. 封装返回
        List<WrongKnowledgeStatsVO> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            WrongKnowledgeStatsVO vo = new WrongKnowledgeStatsVO();
            vo.setKnowledgePoint(entry.getKey());
            vo.setWrongCount(entry.getValue());
            result.add(vo);
        }
        return result;
    }
}