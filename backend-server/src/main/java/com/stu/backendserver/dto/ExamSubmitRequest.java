package com.stu.backendserver.dto;

import java.util.List;

public class ExamSubmitRequest {
    private Long paperId;
    private Long studentId;
    private String studentName;
    private String className;
    private List<ExamSubmitAnswerItem> answers;

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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<ExamSubmitAnswerItem> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ExamSubmitAnswerItem> answers) {
        this.answers = answers;
    }
}
