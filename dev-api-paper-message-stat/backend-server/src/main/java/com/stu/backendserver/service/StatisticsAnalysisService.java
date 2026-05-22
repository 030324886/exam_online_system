package com.stu.backendserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stu.backendserver.dto.ClassScoreStatsResult;
import com.stu.backendserver.dto.WeakKnowledgeItem;
import com.stu.backendserver.dto.WeakKnowledgeReportResult;
import com.stu.backendserver.entity.ExamAnswer;
import com.stu.backendserver.entity.ExamSubmission;
import com.stu.backendserver.entity.Paper;
import com.stu.backendserver.entity.Question;
import com.stu.backendserver.mapper.ExamAnswerMapper;
import com.stu.backendserver.mapper.ExamSubmissionMapper;
import com.stu.backendserver.mapper.PaperMapper;
import com.stu.backendserver.mapper.QuestionMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatisticsAnalysisService {

    private static final List<String> OBJECTIVE_TYPES = Arrays.asList("SINGLE", "MULTIPLE", "JUDGE");

    private final ExamSubmissionMapper examSubmissionMapper;
    private final ExamAnswerMapper examAnswerMapper;
    private final QuestionMapper questionMapper;
    private final PaperMapper paperMapper;

    public StatisticsAnalysisService(ExamSubmissionMapper examSubmissionMapper,
                                     ExamAnswerMapper examAnswerMapper,
                                     QuestionMapper questionMapper,
                                     PaperMapper paperMapper) {
        this.examSubmissionMapper = examSubmissionMapper;
        this.examAnswerMapper = examAnswerMapper;
        this.questionMapper = questionMapper;
        this.paperMapper = paperMapper;
    }

    public ClassScoreStatsResult getClassScoreStats(String className, Long paperId) {
        List<ExamSubmission> submissions = querySubmissions(className, paperId);
        if (submissions.isEmpty()) {
            throw new IllegalArgumentException("No submission data found for statistics");
        }

        List<ExamSubmission> gradedSubmissions = submissions.stream()
                .filter(item -> item.getTotalScore() != null)
                .collect(Collectors.toList());
        if (gradedSubmissions.isEmpty()) {
            throw new IllegalArgumentException("No graded submissions found");
        }

        Paper paper = getPaper(paperId);
        int gradedCount = gradedSubmissions.size();
        int totalScore = gradedSubmissions.stream().mapToInt(item -> safeInt(item.getTotalScore())).sum();
        int highestScore = gradedSubmissions.stream().mapToInt(item -> safeInt(item.getTotalScore())).max().orElse(0);
        int lowestScore = gradedSubmissions.stream().mapToInt(item -> safeInt(item.getTotalScore())).min().orElse(0);
        long passedCount = gradedSubmissions.stream()
                .filter(item -> safeInt(item.getTotalScore()) >= safeInt(paper.getPassScore()))
                .count();

        ClassScoreStatsResult result = new ClassScoreStatsResult();
        result.setClassName(resolveClassName(className, submissions));
        result.setPaperId(paperId);
        result.setSubmissionCount(submissions.size());
        result.setGradedCount(gradedCount);
        result.setAverageScore(round((double) totalScore / gradedCount));
        result.setHighestScore(highestScore);
        result.setLowestScore(lowestScore);
        result.setPassRate(round((double) passedCount * 100 / gradedCount));
        return result;
    }

    public WeakKnowledgeReportResult getWeakKnowledgeReport(String className, Long paperId) {
        List<ExamSubmission> submissions = querySubmissions(className, paperId);
        if (submissions.isEmpty()) {
            throw new IllegalArgumentException("No submission data found for analysis");
        }

        List<Long> submissionIds = submissions.stream().map(ExamSubmission::getId).collect(Collectors.toList());
        List<ExamAnswer> answers = examAnswerMapper.selectList(
                new LambdaQueryWrapper<ExamAnswer>().in(ExamAnswer::getSubmissionId, submissionIds)
        );
        if (answers.isEmpty()) {
            throw new IllegalArgumentException("No answer data found for analysis");
        }

        Set<Long> questionIds = answers.stream().map(ExamAnswer::getQuestionId).collect(Collectors.toSet());
        Map<Long, Question> questionMap = questionMapper.selectBatchIds(questionIds).stream()
                .collect(Collectors.toMap(Question::getId, question -> question, (left, right) -> left));

        Map<String, Integer> wrongCountMap = new HashMap<>();
        Map<String, Integer> totalAttemptMap = new HashMap<>();
        int totalWrongAnswers = 0;
        for (ExamAnswer answer : answers) {
            Question question = questionMap.get(answer.getQuestionId());
            String knowledgePoint = question == null || !StringUtils.hasText(question.getKnowledgePoint())
                    ? "UNCLASSIFIED"
                    : question.getKnowledgePoint();

            totalAttemptMap.merge(knowledgePoint, 1, Integer::sum);
            if (isWrongAnswer(answer, question)) {
                wrongCountMap.merge(knowledgePoint, 1, Integer::sum);
                totalWrongAnswers++;
            }
        }

        List<WeakKnowledgeItem> items = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : totalAttemptMap.entrySet()) {
            int wrongCount = wrongCountMap.getOrDefault(entry.getKey(), 0);
            WeakKnowledgeItem item = new WeakKnowledgeItem();
            item.setKnowledgePoint(entry.getKey());
            item.setTotalAttempts(entry.getValue());
            item.setWrongCount(wrongCount);
            item.setWrongRate(round((double) wrongCount * 100 / entry.getValue()));
            items.add(item);
        }

        items.sort(Comparator.comparing(WeakKnowledgeItem::getWrongRate).reversed()
                .thenComparing(WeakKnowledgeItem::getWrongCount, Comparator.reverseOrder()));

        WeakKnowledgeReportResult result = new WeakKnowledgeReportResult();
        result.setClassName(resolveClassName(className, submissions));
        result.setPaperId(paperId);
        result.setTotalQuestionsAnalyzed(questionIds.size());
        result.setTotalWrongAnswers(totalWrongAnswers);
        result.setItems(items);
        return result;
    }

    private List<ExamSubmission> querySubmissions(String className, Long paperId) {
        LambdaQueryWrapper<ExamSubmission> wrapper = new LambdaQueryWrapper<>();
        if (paperId != null) {
            wrapper.eq(ExamSubmission::getPaperId, paperId);
        }
        if (StringUtils.hasText(className)) {
            wrapper.eq(ExamSubmission::getClassName, className);
        }
        return examSubmissionMapper.selectList(wrapper);
    }

    private Paper getPaper(Long paperId) {
        if (paperId == null) {
            Paper paper = new Paper();
            paper.setPassScore(60);
            return paper;
        }
        Paper paper = paperMapper.selectById(paperId);
        if (paper == null) {
            throw new IllegalArgumentException("Paper not found: " + paperId);
        }
        return paper;
    }

    private String resolveClassName(String className, List<ExamSubmission> submissions) {
        if (StringUtils.hasText(className)) {
            return className;
        }
        return submissions.stream()
                .map(ExamSubmission::getClassName)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse("ALL_CLASSES");
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private boolean isWrongAnswer(ExamAnswer answer, Question question) {
        if (question == null) {
            return safeInt(answer.getIsCorrect()) == 0;
        }
        if (OBJECTIVE_TYPES.contains(normalize(question.getType()))) {
            return safeInt(answer.getIsCorrect()) == 0;
        }
        return safeInt(answer.getScore()) <= 0;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }
}
