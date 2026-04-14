package com.stu.backendserver.controller;

import com.stu.backendserver.common.Result;
import com.stu.backendserver.entity.Question;
import com.stu.backendserver.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // 新增题目
    @PostMapping
    public Result<Boolean> add(@RequestBody Question question) {
        return Result.success(questionService.save(question));
    }

    // 删除题目
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(questionService.removeById(id));
    }

    // 修改题目
    @PutMapping
    public Result<Boolean> update(@RequestBody Question question) {
        return Result.success(questionService.updateById(question));
    }

    // 查询所有题目
    @GetMapping("/list")
    public Result<List<Question>> list() {
        return Result.success(questionService.list());
    }

    // 根据知识点查询题目
    @GetMapping("/knowledge/{point}")
    public Result<List<Question>> listByKnowledge(@PathVariable String point) {
        return Result.success(questionService.listByKnowledge(point));
    }
}