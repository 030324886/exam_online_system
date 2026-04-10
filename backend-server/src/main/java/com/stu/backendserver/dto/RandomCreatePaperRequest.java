package com.stu.backendserver.dto;

import java.util.List;

public class RandomCreatePaperRequest {
    private String name;
    private Integer passScore;
    private Long createdBy;
    private List<RandomRuleItem> rules;

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

    public List<RandomRuleItem> getRules() {
        return rules;
    }

    public void setRules(List<RandomRuleItem> rules) {
        this.rules = rules;
    }
}
