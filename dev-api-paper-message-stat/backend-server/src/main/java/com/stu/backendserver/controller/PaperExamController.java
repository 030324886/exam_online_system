package com.stu.backendserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stu.backendserver.common.ApiResponse;
import com.stu.backendserver.dto.CreatePaperRequest;
import com.stu.backendserver.dto.ObjectiveGradeRequest;
import com.stu.backendserver.dto.ObjectiveGradeResult;
import com.stu.backendserver.dto.PaperCreateResult;
import com.stu.backendserver.dto.RandomCreatePaperRequest;
import com.stu.backendserver.entity.Paper;
import com.stu.backendserver.entity.PaperQuestion;
import com.stu.backendserver.mapper.PaperMapper;
import com.stu.backendserver.mapper.PaperQuestionMapper;
import com.stu.backendserver.mapper.QuestionMapper;
import com.stu.backendserver.service.PaperExamService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/papers")
public class PaperExamController {

    private final PaperExamService paperExamService;
    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;

    public PaperExamController(PaperExamService paperExamService,
                               PaperMapper paperMapper,
                               PaperQuestionMapper paperQuestionMapper,
                               QuestionMapper questionMapper) {
        this.paperExamService = paperExamService;
        this.paperMapper = paperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.questionMapper = questionMapper;
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

    @GetMapping("/list")
    public ApiResponse<List<Paper>> listPapers() {
        return ApiResponse.ok(paperMapper.selectList(null));
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<Map<String, Object>> getPaperDetail(@PathVariable Long id) {
        Paper paper = paperMapper.selectById(id);
        List<Map<String, Object>> questions = questionMapper.selectPaperQuestionsWithDetail(id);
        Map<String, Object> result = new HashMap<>();
        result.put("paper", paper);
        result.put("questions", questions);
        return ApiResponse.ok(result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deletePaper(@PathVariable Long id) {
        paperQuestionMapper.delete(new LambdaQueryWrapper<PaperQuestion>()
                .eq(PaperQuestion::getPaperId, id));
        return ApiResponse.ok(paperMapper.deleteById(id) > 0);
    }

    @PostMapping("/publish/{id}")
    public ApiResponse<Boolean> publishPaper(@PathVariable Long id) {
        Paper paper = paperMapper.selectById(id);
        return ApiResponse.ok(paper != null);
    }
}
