package com.stu.backendserver.dto;

import java.util.List;

public class ObjectiveGradeResult {
    private Long submissionId;
    private Long paperId;
    private Long studentId;
    private Integer objectiveScore;
    private Integer totalScore;
    private List<ObjectiveQuestionResult> details;

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getObjectiveScore() {
        return objectiveScore;
    }

    public void setObjectiveScore(Integer objectiveScore) {
        this.objectiveScore = objectiveScore;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public List<ObjectiveQuestionResult> getDetails() {
        return details;
    }

    public void setDetails(List<ObjectiveQuestionResult> details) {
        this.details = details;
    }
}
