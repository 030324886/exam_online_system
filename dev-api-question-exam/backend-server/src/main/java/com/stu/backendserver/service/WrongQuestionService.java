package com.stu.backendserver.service;

import com.stu.backendserver.vo.WrongKnowledgeStatsVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stu.backendserver.entity.WrongQuestion;
import java.util.List;

public interface WrongQuestionService extends IService<WrongQuestion> {
    // 自动入库错题：存在则次数+1，不存在则新增
    boolean addOrUpdateWrong(Long userId, Long questionId);

    // 按知识点统计错题
    List<WrongKnowledgeStatsVO> statsWrongByKnowledge(Long userId);
}