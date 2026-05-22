package com.stu.backendserver.dto;

import java.util.List;

public class ObjectiveGradeRequest {
    private Long paperId;
    private Long studentId;
    private List<ObjectiveAnswerItem> answers;

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

    public List<ObjectiveAnswerItem> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ObjectiveAnswerItem> answers) {
        this.answers = answers;
    }
}
