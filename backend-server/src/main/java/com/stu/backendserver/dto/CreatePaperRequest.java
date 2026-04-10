package com.stu.backendserver.dto;

import java.util.List;

public class CreatePaperRequest {
    private String name;
    private Integer passScore;
    private Long createdBy;
    private List<PaperQuestionItem> questions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPassScore() {
        return passScore;
    }

    public void setPassScore(Integer passScore) {
        this.passScore = passScore;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public List<PaperQuestionItem> getQuestions() {
        return questions;
    }

    public void setQuestions(List<PaperQuestionItem> questions) {
        this.questions = questions;
    }
}
