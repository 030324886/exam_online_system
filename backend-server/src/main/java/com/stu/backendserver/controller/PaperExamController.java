package com.stu.backendserver.controller;

import com.stu.backendserver.common.ApiResponse;
import com.stu.backendserver.dto.CreatePaperRequest;
import com.stu.backendserver.dto.ObjectiveGradeRequest;
import com.stu.backendserver.dto.ObjectiveGradeResult;
import com.stu.backendserver.dto.PaperCreateResult;
import com.stu.backendserver.dto.RandomCreatePaperRequest;
import com.stu.backendserver.service.PaperExamService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/papers")
public class PaperExamController {

    private final PaperExamService paperExamService;

    public PaperExamController(PaperExamService paperExamService) {
        this.paperExamService = paperExamService;
    }

    @PostMapping("/manual")
    public ApiResponse<PaperCreateResult> createManualPaper(@RequestBody CreatePaperRequest request) {
        return ApiResponse.ok("manual-paper-created", paperExamService.createPaperManual(request));
    }

    @PostMapping("/random")
    public ApiResponse<PaperCreateResult> createRandomPaper(@RequestBody RandomCreatePaperRequest request) {
        return ApiResponse.ok("random-paper-created", paperExamService.createPaperRandom(request));
    }

    @PostMapping("/objective/grade")
    public ApiResponse<ObjectiveGradeResult> gradeObjective(@RequestBody ObjectiveGradeRequest request) {
        return ApiResponse.ok("objective-graded", paperExamService.gradeObjectiveQuestions(request));
    }
}
