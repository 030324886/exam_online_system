package com.stu.backendserver.controller;

import com.stu.backendserver.common.ApiResponse;
import com.stu.backendserver.dto.ExamSubmitRequest;
import com.stu.backendserver.dto.ExamSubmitResult;
import com.stu.backendserver.dto.ManualGradeRequest;
import com.stu.backendserver.dto.ManualGradeResult;
import com.stu.backendserver.service.PaperExamService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/submissions")
public class ExamWorkflowController {

    private final PaperExamService paperExamService;

    public ExamWorkflowController(PaperExamService paperExamService) {
        this.paperExamService = paperExamService;
    }

    @PostMapping("/submit")
    public ApiResponse<ExamSubmitResult> submitExam(@RequestBody ExamSubmitRequest request) {
        return ApiResponse.ok("exam-submitted", paperExamService.submitExam(request));
    }

    @PostMapping("/manual-grade")
    public ApiResponse<ManualGradeResult> manualGrade(@RequestBody ManualGradeRequest request) {
        return ApiResponse.ok("manual-grade-completed", paperExamService.manualGrade(request));
    }
}
