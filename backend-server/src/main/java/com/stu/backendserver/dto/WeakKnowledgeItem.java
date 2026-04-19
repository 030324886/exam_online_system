package com.stu.backendserver.dto;

public class WeakKnowledgeItem {
    private String knowledgePoint;
    private Integer wrongCount;
    private Integer totalAttempts;
    private Double wrongRate;

    public String getKnowledgePoint() {
        return knowledgePoint;
    }

    public void setKnowledgePoint(String knowledgePoint) {
        this.knowledgePoint = knowledgePoint;
    }

    public Integer getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }

    public Integer getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(Integer totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public Double getWrongRate() {
        return wrongRate;
    }

    public void setWrongRate(Double wrongRate) {
        this.wrongRate = wrongRate;
    }
}
