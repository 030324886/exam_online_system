package com.stu.backendserver.controller;

import com.stu.backendserver.common.Result;
import com.stu.backendserver.service.WrongQuestionService;
import com.stu.backendserver.vo.WrongKnowledgeStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/wrong")
public class WrongQuestionController {

    @Autowired
    private WrongQuestionService wrongQuestionService;

    @PostMapping("/add")
    public Result<Boolean> addWrong(@RequestParam Long userId, @RequestParam Long questionId) {
        try {
            return Result.success(wrongQuestionService.addOrUpdateWrong(userId, questionId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 按知识点统计错题接口
    @GetMapping("/stats/knowledge/{userId}")
    public Result<List<WrongKnowledgeStatsVO>> statsWrong(@PathVariable Long userId) {
        return Result.success(wrongQuestionService.statsWrongByKnowledge(userId));
    }
}