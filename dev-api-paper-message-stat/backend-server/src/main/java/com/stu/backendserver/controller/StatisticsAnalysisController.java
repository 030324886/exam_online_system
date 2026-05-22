package com.stu.backendserver.controller;

import com.stu.backendserver.common.ApiResponse;
import com.stu.backendserver.dto.ClassScoreStatsResult;
import com.stu.backendserver.dto.WeakKnowledgeReportResult;
import com.stu.backendserver.service.StatisticsAnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsAnalysisController {

    private final StatisticsAnalysisService statisticsAnalysisService;

    public StatisticsAnalysisController(StatisticsAnalysisService statisticsAnalysisService) {
        this.statisticsAnalysisService = statisticsAnalysisService;
    }

    @GetMapping("/class-score")
    public ApiResponse<ClassScoreStatsResult> getClassScoreStats(@RequestParam(required = false) String className,
                                                                 @RequestParam(required = false) Long paperId) {
        return ApiResponse.ok(
                "class-score-statistics-ready",
                statisticsAnalysisService.getClassScoreStats(className, paperId)
        );
    }

    @GetMapping("/weak-knowledge")
    public ApiResponse<WeakKnowledgeReportResult> getWeakKnowledgeReport(@RequestParam(required = false) String className,
                                                                         @RequestParam(required = false) Long paperId) {
        return ApiResponse.ok(
                "weak-knowledge-report-ready",
                statisticsAnalysisService.getWeakKnowledgeReport(className, paperId)
        );
    }
}
