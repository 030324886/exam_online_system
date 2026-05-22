package com.stu.backendserver.controller;

import com.stu.backendserver.common.ApiResponse;
import com.stu.backendserver.entity.ExamAnswer;
import com.stu.backendserver.entity.ExamSubmission;
import com.stu.backendserver.mapper.ExamAnswerMapper;
import com.stu.backendserver.mapper.ExamSubmissionMapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam")
public class ExamRecordController {

    private final ExamSubmissionMapper examSubmissionMapper;
    private final ExamAnswerMapper examAnswerMapper;

    public ExamRecordController(ExamSubmissionMapper examSubmissionMapper,
                                ExamAnswerMapper examAnswerMapper) {
        this.examSubmissionMapper = examSubmissionMapper;
        this.examAnswerMapper = examAnswerMapper;
    }

    @GetMapping("/record/list")
    public ApiResponse<List<ExamSubmission>> listExamRecords() {
        return ApiResponse.ok(examSubmissionMapper.selectList(null));
    }

    @GetMapping("/record/detail/{id}")
    public ApiResponse<Map<String, Object>> getExamRecordDetail(@PathVariable Long id) {
        ExamSubmission record = examSubmissionMapper.selectById(id);
        List<ExamAnswer> answers = examAnswerMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamAnswer>()
                        .eq(ExamAnswer::getSubmissionId, id)
        );
        Map<String, Object> result = new HashMap<>();
        result.put("record", record);
        result.put("answers", answers);
        return ApiResponse.ok(result);
    }
}
