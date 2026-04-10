package com.stu.backendserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stu.backendserver.dto.CreatePaperRequest;
import com.stu.backendserver.dto.ObjectiveAnswerItem;
import com.stu.backendserver.dto.ObjectiveGradeRequest;
import com.stu.backendserver.dto.ObjectiveGradeResult;
import com.stu.backendserver.dto.ObjectiveQuestionResult;
import com.stu.backendserver.dto.PaperCreateResult;
import com.stu.backendserver.dto.PaperQuestionItem;
import com.stu.backendserver.dto.RandomCreatePaperRequest;
import com.stu.backendserver.dto.RandomRuleItem;
import com.stu.backendserver.entity.ExamAnswer;
import com.stu.backendserver.entity.ExamSubmission;
import com.stu.backendserver.entity.Paper;
import com.stu.backendserver.entity.PaperQuestion;
import com.stu.backendserver.entity.Question;
import com.stu.backendserver.mapper.ExamAnswerMapper;
import com.stu.backendserver.mapper.ExamSubmissionMapper;
import com.stu.backendserver.mapper.PaperMapper;
import com.stu.backendserver.mapper.PaperQuestionMapper;
import com.stu.backendserver.mapper.QuestionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaperExamService {

    private static final List<String> OBJECTIVE_TYPES = Arrays.asList("SINGLE", "MULTIPLE", "JUDGE");
    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;
    private final ExamSubmissionMapper examSubmissionMapper;
    private final ExamAnswerMapper examAnswerMapper;

    public PaperExamService(PaperMapper paperMapper,
                            PaperQuestionMapper paperQuestionMapper,
                            QuestionMapper questionMapper,
                            ExamSubmissionMapper examSubmissionMapper,
                            ExamAnswerMapper examAnswerMapper) {
        this.paperMapper = paperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.questionMapper = questionMapper;
        this.examSubmissionMapper = examSubmissionMapper;
        this.examAnswerMapper = examAnswerMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public PaperCreateResult createPaperManual(CreatePaperRequest request) {
        validateManualPaperRequest(request);
        Paper paper = insertPaper(request.getName(), request.getPassScore(), request.getCreatedBy());
        int totalScore = insertPaperQuestions(paper.getId(), request.getQuestions());
        paper.setTotalScore(totalScore);
        paper.setUpdatedTime(LocalDateTime.now());
        paperMapper.updateById(paper);
        return buildPaperCreateResult(paper.getId(), totalScore, request.getQuestions().size());
    }

    @Transactional(rollbackFor = Exception.class)
    public PaperCreateResult createPaperRandom(RandomCreatePaperRequest request) {
        validateRandomPaperRequest(request);
        List<PaperQuestionItem> selectedQuestions = new ArrayList<>();
        int sortNo = 1;
        for (RandomRuleItem rule : request.getRules()) {
            List<Question> randomQuestions = questionMapper.selectRandomByType(rule.getQuestionType(), rule.getCount());
            if (CollectionUtils.isEmpty(randomQuestions) || randomQuestions.size() < rule.getCount()) {
                throw new IllegalArgumentException("题型 " + rule.getQuestionType() + " 可用题目不足，期望 " + rule.getCount() + " 道");
            }
            for (Question question : randomQuestions) {
                PaperQuestionItem item = new PaperQuestionItem();
                item.setQuestionId(question.getId());
                item.setScore(rule.getScore());
                item.setSortNo(sortNo++);
                selectedQuestions.add(item);
            }
        }
        CreatePaperRequest manualRequest = new CreatePaperRequest();
        manualRequest.setName(request.getName());
        manualRequest.setPassScore(request.getPassScore());
        manualRequest.setCreatedBy(request.getCreatedBy());
        manualRequest.setQuestions(selectedQuestions);
        return createPaperManual(manualRequest);
    }

    @Transactional(rollbackFor = Exception.class)
    public ObjectiveGradeResult gradeObjectiveQuestions(ObjectiveGradeRequest request) {
        validateGradeRequest(request);
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, request.getPaperId())
        );
        if (CollectionUtils.isEmpty(paperQuestions)) {
            throw new IllegalArgumentException("试卷未关联题目，paperId=" + request.getPaperId());
        }

        Map<Long, Integer> questionScoreMap = paperQuestions.stream()
                .collect(Collectors.toMap(PaperQuestion::getQuestionId, PaperQuestion::getScore, (a, b) -> a));
        List<Long> questionIds = new ArrayList<>(questionScoreMap.keySet());
        List<Question> questions = questionMapper.selectBatchIds(questionIds);
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q, (a, b) -> a));

        ExamSubmission submission = new ExamSubmission();
        submission.setPaperId(request.getPaperId());
        submission.setStudentId(request.getStudentId());
        submission.setStatus("SUBMITTED");
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setObjectiveScore(0);
        submission.setTotalScore(0);
        examSubmissionMapper.insert(submission);

        Map<Long, String> answerMap = request.getAnswers().stream()
                .collect(Collectors.toMap(ObjectiveAnswerItem::getQuestionId, ObjectiveAnswerItem::getAnswer, (a, b) -> b));

        int objectiveScore = 0;
        List<ObjectiveQuestionResult> details = new ArrayList<>();
        for (Long questionId : questionIds) {
            Question question = questionMap.get(questionId);
            if (question == null) {
                continue;
            }
            String questionType = normalize(question.getType());
            if (!OBJECTIVE_TYPES.contains(questionType)) {
                continue;
            }
            String studentAnswer = answerMap.get(questionId);
            String correctAnswer = question.getCorrectAnswer();
            boolean correct = compareAnswer(questionType, studentAnswer, correctAnswer);
            int itemScore = correct ? questionScoreMap.getOrDefault(questionId, 0) : 0;
            objectiveScore += itemScore;

            ExamAnswer answer = new ExamAnswer();
            answer.setSubmissionId(submission.getId());
            answer.setQuestionId(questionId);
            answer.setAnswerText(studentAnswer);
            answer.setIsCorrect(correct ? 1 : 0);
            answer.setScore(itemScore);
            examAnswerMapper.insert(answer);

            ObjectiveQuestionResult detail = new ObjectiveQuestionResult();
            detail.setQuestionId(questionId);
            detail.setStudentAnswer(studentAnswer);
            detail.setCorrectAnswer(correctAnswer);
            detail.setIsCorrect(correct ? 1 : 0);
            detail.setScore(itemScore);
            details.add(detail);
        }

        submission.setObjectiveScore(objectiveScore);
        submission.setTotalScore(objectiveScore);
        examSubmissionMapper.updateById(submission);

        ObjectiveGradeResult result = new ObjectiveGradeResult();
        result.setSubmissionId(submission.getId());
        result.setPaperId(request.getPaperId());
        result.setStudentId(request.getStudentId());
        result.setObjectiveScore(objectiveScore);
        result.setTotalScore(objectiveScore);
        result.setDetails(details.stream()
                .sorted(Comparator.comparing(ObjectiveQuestionResult::getQuestionId))
                .collect(Collectors.toList()));
        return result;
    }

    private Paper insertPaper(String name, Integer passScore, Long createdBy) {
        Paper paper = new Paper();
        paper.setName(name);
        paper.setPassScore(passScore);
        paper.setCreatedBy(createdBy);
        paper.setTotalScore(0);
        paper.setCreatedTime(LocalDateTime.now());
        paper.setUpdatedTime(LocalDateTime.now());
        paperMapper.insert(paper);
        return paper;
    }

    private int insertPaperQuestions(Long paperId, List<PaperQuestionItem> items) {
        int totalScore = 0;
        for (PaperQuestionItem item : items) {
            PaperQuestion relation = new PaperQuestion();
            relation.setPaperId(paperId);
            relation.setQuestionId(item.getQuestionId());
            relation.setScore(item.getScore());
            relation.setSortNo(item.getSortNo());
            paperQuestionMapper.insert(relation);
            totalScore += item.getScore();
        }
        return totalScore;
    }

    private PaperCreateResult buildPaperCreateResult(Long paperId, int totalScore, int count) {
        PaperCreateResult result = new PaperCreateResult();
        result.setPaperId(paperId);
        result.setTotalScore(totalScore);
        result.setQuestionCount(count);
        return result;
    }

    private void validateManualPaperRequest(CreatePaperRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("试卷名称不能为空");
        }
        if (request.getPassScore() == null || request.getPassScore() < 0) {
            throw new IllegalArgumentException("及格分不合法");
        }
        if (request.getCreatedBy() == null) {
            throw new IllegalArgumentException("创建人不能为空");
        }
        if (CollectionUtils.isEmpty(request.getQuestions())) {
            throw new IllegalArgumentException("试卷至少关联 1 道题");
        }
        for (PaperQuestionItem item : request.getQuestions()) {
            if (item.getQuestionId() == null || item.getScore() == null || item.getScore() < 0) {
                throw new IllegalArgumentException("题目关联参数不合法");
            }
            if (item.getSortNo() == null || item.getSortNo() <= 0) {
                throw new IllegalArgumentException("题目排序号必须大于 0");
            }
        }
    }

    private void validateRandomPaperRequest(RandomCreatePaperRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("试卷名称不能为空");
        }
        if (request.getPassScore() == null || request.getPassScore() < 0) {
            throw new IllegalArgumentException("及格分不合法");
        }
        if (request.getCreatedBy() == null) {
            throw new IllegalArgumentException("创建人不能为空");
        }
        if (CollectionUtils.isEmpty(request.getRules())) {
            throw new IllegalArgumentException("随机组卷规则不能为空");
        }
        for (RandomRuleItem rule : request.getRules()) {
            if (!StringUtils.hasText(rule.getQuestionType()) || rule.getCount() == null || rule.getScore() == null) {
                throw new IllegalArgumentException("随机组卷规则参数不完整");
            }
            if (rule.getCount() <= 0 || rule.getScore() < 0) {
                throw new IllegalArgumentException("随机组卷规则数值不合法");
            }
            rule.setQuestionType(normalize(rule.getQuestionType()));
        }
    }

    private void validateGradeRequest(ObjectiveGradeRequest request) {
        if (request == null || request.getPaperId() == null || request.getStudentId() == null) {
            throw new IllegalArgumentException("paperId/studentId 不能为空");
        }
        if (CollectionUtils.isEmpty(request.getAnswers())) {
            throw new IllegalArgumentException("作答内容不能为空");
        }
    }

    private boolean compareAnswer(String type, String studentAnswer, String correctAnswer) {
        String normalizedStudent = normalize(studentAnswer);
        String normalizedCorrect = normalize(correctAnswer);
        if ("MULTIPLE".equals(type)) {
            return normalizeMulti(normalizedStudent).equals(normalizeMulti(normalizedCorrect));
        }
        return normalizedStudent.equals(normalizedCorrect);
    }

    private String normalizeMulti(String raw) {
        if (!StringUtils.hasText(raw)) {
            return "";
        }
        return Arrays.stream(raw.split(","))
                .map(this::normalize)
                .filter(StringUtils::hasText)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.replace(" ", "").trim().toUpperCase(Locale.ROOT);
    }
}
