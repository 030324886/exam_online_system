package com.stu.backendserver.controller;

import com.stu.backendserver.common.Result;
import com.stu.backendserver.service.WrongQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wrong")
public class WrongQuestionController {

    @Autowired
    private WrongQuestionService wrongQuestionService;

    // 错题自动入库接口
    @PostMapping("/add")
    public Result<Boolean> addWrong(@RequestParam Long userId, @RequestParam Long questionId) {
        try {
            return Result.success(wrongQuestionService.addOrUpdateWrong(userId, questionId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}