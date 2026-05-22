package com.stu.backendserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stu.backendserver.dto.CreatePaperRequest;
import com.stu.backendserver.dto.ExamSubmitAnswerItem;
import com.stu.backendserver.dto.ExamSubmitRequest;
import com.stu.backendserver.dto.ExamSubmitResult;
import com.stu.backendserver.dto.ManualGradeItem;
import com.stu.backendserver.dto.ManualGradeRequest;
import com.stu.backendserver.dto.ManualGradeResult;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PaperExamService {

    private static final List<String> OBJECTIVE_TYPES = Arrays.asList("SINGLE", "MULTIPLE", "JUDGE");
    private static final Map<String, String> TYPE_MAP = new HashMap<>();
    static {
        TYPE_MAP.put("1", "SINGLE");
        TYPE_MAP.put("2", "MULTIPLE");
        TYPE_MAP.put("3", "JUDGE");
        TYPE_MAP.put("4", "SHORT");
    }

    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;
    private final ExamSubmissionMapper examSubmissionMapper;
    private final ExamAnswerMapper examAnswerMapper;
    private final MessagePushService messagePushService;

    public PaperExamService(PaperMapper paperMapper,
                            PaperQuestionMapper paperQuestionMapper,
                            QuestionMapper questionMapper,
                            ExamSubmissionMapper examSubmissionMapper,
                            ExamAnswerMapper examAnswerMapper,
                            MessagePushService messagePushService) {
        this.paperMapper = paperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.questionMapper = questionMapper;
        this.examSubmissionMapper = examSubmissionMapper;
        this.examAnswerMapper = examAnswerMapper;
        this.messagePushService = messagePushService;
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
                throw new IllegalArgumentException("Insufficient questions for type " + rule.getQuestionType());
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
        List<PaperQuestion> paperQuestions = getPaperQuestions(request.getPaperId());
        Map<Long, Integer> questionScoreMap = paperQuestions.stream()
                .collect(Collectors.toMap(PaperQuestion::getQuestionId, PaperQuestion::getScore, (left, right) -> left));
        Map<Long, Question> questionMap = getQuestionMap(questionScoreMap.keySet());
        Map<Long, String> answerMap = request.getAnswers().stream()
                .collect(Collectors.toMap(ObjectiveAnswerItem::getQuestionId, ObjectiveAnswerItem::getAnswer, (left, right) -> right));

        ExamSubmission submission = new ExamSubmission();
        submission.setPaperId(request.getPaperId());
        submission.setStudentId(request.getStudentId());
        submission.setStudentName(null);
        submission.setClassName(null);
        submission.setObjectiveScore(0);
        submission.setSubjectiveScore(0);
        submission.setTotalScore(0);
        submission.setStatus("SUBMITTED");
        submission.setSubmittedAt(LocalDateTime.now());
        examSubmissionMapper.insert(submission);

        int objectiveScore = 0;
        List<ObjectiveQuestionResult> details = new ArrayList<>();
        for (PaperQuestion paperQuestion : paperQuestions) {
            Long questionId = paperQuestion.getQuestionId();
            Question question = questionMap.get(questionId);
            if (question == null || !isObjective(question.getType())) {
                continue;
            }

            String studentAnswer = answerMap.get(questionId);
            String correctAnswer = question.getCorrectAnswer();
            boolean correct = compareAnswer(question.getType(), studentAnswer, correctAnswer);
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
            detail.setScore(itemScore);
            detail.setIsCorrect(correct ? 1 : 0);
            details.add(detail);
        }

        submission.setObjectiveScore(objectiveScore);
        submission.setSubjectiveScore(0);
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

    @Transactional(rollbackFor = Exception.class)
    public ExamSubmitResult submitExam(ExamSubmitRequest request) {
        validateSubmitRequest(request);
        Paper paper = getPaper(request.getPaperId());
        List<PaperQuestion> paperQuestions = getPaperQuestions(request.getPaperId());
        Map<Long, Integer> questionScoreMap = paperQuestions.stream()
                .collect(Collectors.toMap(PaperQuestion::getQuestionId, PaperQuestion::getScore, (left, right) -> left));
        Map<Long, Question> questionMap = getQuestionMap(questionScoreMap.keySet());
        Map<Long, String> answerMap = request.getAnswers().stream()
                .collect(Collectors.toMap(ExamSubmitAnswerItem::getQuestionId, ExamSubmitAnswerItem::getAnswer, (left, right) -> right));

        validateAnswerScope(answerMap.keySet(), questionScoreMap.keySet());

        ExamSubmission submission = new ExamSubmission();
        submission.setPaperId(request.getPaperId());
        submission.setStudentId(request.getStudentId());
        submission.setStudentName(request.getStudentName());
        submission.setClassName(request.getClassName());
        submission.setObjectiveScore(0);
        submission.setSubjectiveScore(0);
        submission.setTotalScore(0);
        submission.setStatus("SUBMITTED");
        submission.setSubmittedAt(LocalDateTime.now());
        examSubmissionMapper.insert(submission);

        int objectiveScore = 0;
        int pendingReviewCount = 0;
        for (PaperQuestion paperQuestion : paperQuestions) {
            Long questionId = paperQuestion.getQuestionId();
            Question question = questionMap.get(questionId);
            if (question == null) {
                continue;
            }

            String studentAnswer = answerMap.get(questionId);
            ExamAnswer answer = new ExamAnswer();
            answer.setSubmissionId(submission.getId());
            answer.setQuestionId(questionId);
            answer.setAnswerText(studentAnswer);

            if (isObjective(question.getType())) {
                boolean correct = compareAnswer(question.getType(), studentAnswer, question.getCorrectAnswer());
                int itemScore = correct ? paperQuestion.getScore() : 0;
                answer.setIsCorrect(correct ? 1 : 0);
                answer.setScore(itemScore);
                objectiveScore += itemScore;
            } else {
                answer.setIsCorrect(0);
                answer.setScore(0);
                pendingReviewCount++;
            }
            examAnswerMapper.insert(answer);
        }

        submission.setObjectiveScore(objectiveScore);
        submission.setSubjectiveScore(0);
        submission.setTotalScore(objectiveScore);
        submission.setStatus(pendingReviewCount > 0 ? "REVIEWING" : "GRADED");
        examSubmissionMapper.updateById(submission);

        messagePushService.notifyTeacherStudentSubmitted(
                paper.getId(),
                paper.getName(),
                submission.getId(),
                request.getStudentId(),
                request.getStudentName(),
                request.getClassName()
        );

        ExamSubmitResult result = new ExamSubmitResult();
        result.setSubmissionId(submission.getId());
        result.setPaperId(request.getPaperId());
        result.setStudentId(request.getStudentId());
        result.setObjectiveScore(objectiveScore);
        result.setTotalScore(objectiveScore);
        result.setPendingReviewCount(pendingReviewCount);
        result.setStatus(submission.getStatus());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public ManualGradeResult manualGrade(ManualGradeRequest request) {
        validateManualGradeRequest(request);

        ExamSubmission submission = examSubmissionMapper.selectById(request.getSubmissionId());
        if (submission == null) {
            throw new IllegalArgumentException("Submission not found: " + request.getSubmissionId());
        }

        List<ExamAnswer> answers = examAnswerMapper.selectList(
                new LambdaQueryWrapper<ExamAnswer>().eq(ExamAnswer::getSubmissionId, request.getSubmissionId())
        );
        if (CollectionUtils.isEmpty(answers)) {
            throw new IllegalArgumentException("Submission has no answers");
        }

        Map<Long, ExamAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(ExamAnswer::getQuestionId, answer -> answer, (left, right) -> left));
        Map<Long, Integer> scoreMap = request.getItems().stream()
                .collect(Collectors.toMap(ManualGradeItem::getQuestionId, ManualGradeItem::getScore, (left, right) -> right));

        validateAnswerScope(scoreMap.keySet(), answerMap.keySet());

        Map<Long, Question> questionMap = getQuestionMap(answerMap.keySet());
        int subjectiveScore = 0;
        for (Map.Entry<Long, Integer> entry : scoreMap.entrySet()) {
            Question question = questionMap.get(entry.getKey());
            if (question == null) {
                throw new IllegalArgumentException("Question not found: " + entry.getKey());
            }
            if (isObjective(question.getType())) {
                throw new IllegalArgumentException("Objective question does not require manual grading: " + entry.getKey());
            }

            ExamAnswer answer = answerMap.get(entry.getKey());
            answer.setScore(entry.getValue());
            examAnswerMapper.updateById(answer);
        }

        for (ExamAnswer answer : answers) {
            Question question = questionMap.get(answer.getQuestionId());
            if (question != null && !isObjective(question.getType())) {
                Integer score = answerMap.get(answer.getQuestionId()).getScore();
                subjectiveScore += score == null ? 0 : score;
            }
        }

        int totalScore = safeInt(submission.getObjectiveScore()) + subjectiveScore;
        submission.setSubjectiveScore(subjectiveScore);
        submission.setTotalScore(totalScore);
        submission.setStatus("GRADED");
        examSubmissionMapper.updateById(submission);

        Paper paper = getPaper(submission.getPaperId());
        messagePushService.notifyStudentGraded(
                submission.getStudentId(),
                submission.getId(),
                submission.getPaperId(),
                paper.getName(),
                totalScore,
                request.getTeacherName()
        );

        ManualGradeResult result = new ManualGradeResult();
        result.setSubmissionId(submission.getId());
        result.setStudentId(submission.getStudentId());
        result.setObjectiveScore(safeInt(submission.getObjectiveScore()));
        result.setSubjectiveScore(subjectiveScore);
        result.setTotalScore(totalScore);
        result.setStatus(submission.getStatus());
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

    private Paper getPaper(Long paperId) {
        Paper paper = paperMapper.selectById(paperId);
        if (paper == null) {
            throw new IllegalArgumentException("Paper not found: " + paperId);
        }
        return paper;
    }

    private List<PaperQuestion> getPaperQuestions(Long paperId) {
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>()
                        .eq(PaperQuestion::getPaperId, paperId)
                        .orderByAsc(PaperQuestion::getSortNo)
        );
        if (CollectionUtils.isEmpty(paperQuestions)) {
            throw new IllegalArgumentException("Paper has no linked questions: " + paperId);
        }
        return paperQuestions;
    }

    private Map<Long, Question> getQuestionMap(Set<Long> questionIds) {
        if (CollectionUtils.isEmpty(questionIds)) {
            return new HashMap<>();
        }
        return questionMapper.selectBatchIds(questionIds).stream()
                .collect(Collectors.toMap(Question::getId, question -> question, (left, right) -> left));
    }

    private void validateAnswerScope(Set<Long> submittedQuestionIds, Set<Long> paperQuestionIds) {
        for (Long questionId : submittedQuestionIds) {
            if (!paperQuestionIds.contains(questionId)) {
                throw new IllegalArgumentException("Question does not belong to paper: " + questionId);
            }
        }
    }

    private void validateManualPaperRequest(CreatePaperRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("Paper name is required");
        }
        if (request.getPassScore() == null || request.getPassScore() < 0) {
            throw new IllegalArgumentException("Invalid pass score");
        }
        if (request.getCreatedBy() == null) {
            throw new IllegalArgumentException("createdBy is required");
        }
        if (CollectionUtils.isEmpty(request.getQuestions())) {
            throw new IllegalArgumentException("At least one question is required");
        }
        for (PaperQuestionItem item : request.getQuestions()) {
            if (item.getQuestionId() == null || item.getScore() == null || item.getScore() < 0) {
                throw new IllegalArgumentException("Invalid paper question item");
            }
            if (item.getSortNo() == null || item.getSortNo() <= 0) {
                throw new IllegalArgumentException("sortNo must be greater than 0");
            }
        }
    }

    private void validateRandomPaperRequest(RandomCreatePaperRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("Paper name is required");
        }
        if (request.getPassScore() == null || request.getPassScore() < 0) {
            throw new IllegalArgumentException("Invalid pass score");
        }
        if (request.getCreatedBy() == null) {
            throw new IllegalArgumentException("createdBy is required");
        }
        if (CollectionUtils.isEmpty(request.getRules())) {
            throw new IllegalArgumentException("Random rules are required");
        }
        for (RandomRuleItem rule : request.getRules()) {
            if (!StringUtils.hasText(rule.getQuestionType()) || rule.getCount() == null || rule.getScore() == null) {
                throw new IllegalArgumentException("Incomplete random rule");
            }
            if (rule.getCount() <= 0 || rule.getScore() < 0) {
                throw new IllegalArgumentException("Invalid random rule value");
            }
            rule.setQuestionType(normalize(rule.getQuestionType()));
        }
    }

    private void validateGradeRequest(ObjectiveGradeRequest request) {
        if (request == null || request.getPaperId() == null || request.getStudentId() == null) {
            throw new IllegalArgumentException("paperId and studentId are required");
        }
        if (CollectionUtils.isEmpty(request.getAnswers())) {
            throw new IllegalArgumentException("answers are required");
        }
    }

    private void validateSubmitRequest(ExamSubmitRequest request) {
        if (request == null || request.getPaperId() == null || request.getStudentId() == null) {
            throw new IllegalArgumentException("paperId and studentId are required");
        }
        if (CollectionUtils.isEmpty(request.getAnswers())) {
            throw new IllegalArgumentException("answers are required");
        }
    }

    private void validateManualGradeRequest(ManualGradeRequest request) {
        if (request == null || request.getSubmissionId() == null) {
            throw new IllegalArgumentException("submissionId is required");
        }
        if (CollectionUtils.isEmpty(request.getItems())) {
            throw new IllegalArgumentException("manual grade items are required");
        }
        for (ManualGradeItem item : request.getItems()) {
            if (item.getQuestionId() == null || item.getScore() == null || item.getScore() < 0) {
                throw new IllegalArgumentException("Invalid manual grade item");
            }
        }
    }

    private boolean compareAnswer(String type, String studentAnswer, String correctAnswer) {
        String normalizedType = normalize(type);
        String normalizedStudent = normalize(studentAnswer);
        String normalizedCorrect = normalize(correctAnswer);
        if ("MULTIPLE".equals(normalizedType)) {
            return normalizeMulti(normalizedStudent).equals(normalizeMulti(normalizedCorrect));
        }
        return normalizedStudent.equals(normalizedCorrect);
    }

    private boolean isObjective(String type) {
        return OBJECTIVE_TYPES.contains(normalize(type));
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
        String normalized = value.replace(" ", "").trim().toUpperCase(Locale.ROOT);
        String mapped = TYPE_MAP.get(normalized);
        return mapped != null ? mapped : normalized;
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
