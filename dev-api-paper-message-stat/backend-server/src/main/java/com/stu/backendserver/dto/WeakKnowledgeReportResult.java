package com.stu.backendserver.dto;

import java.util.List;

public class WeakKnowledgeReportResult {
    private String className;
    private Long paperId;
    private Integer totalQuestionsAnalyzed;
    private Integer totalWrongAnswers;
    private List<WeakKnowledgeItem> items;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Integer getTotalQuestionsAnalyzed() {
        return totalQuestionsAnalyzed;
    }

    public void setTotalQuestionsAnalyzed(Integer totalQuestionsAnalyzed) {
        this.totalQuestionsAnalyzed = totalQuestionsAnalyzed;
    }

    public Integer getTotalWrongAnswers() {
        return totalWrongAnswers;
    }

    public void setTotalWrongAnswers(Integer totalWrongAnswers) {
        this.totalWrongAnswers = totalWrongAnswers;
    }

    public List<WeakKnowledgeItem> getItems() {
        return items;
    }

    public void setItems(List<WeakKnowledgeItem> items) {
        this.items = items;
    }
}
