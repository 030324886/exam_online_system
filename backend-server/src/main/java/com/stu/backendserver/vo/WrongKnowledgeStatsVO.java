package com.stu.backendserver.vo;

import lombok.Data;

@Data
public class WrongKnowledgeStatsVO {
    private String knowledgePoint;  // 知识点
    private Integer wrongCount;     // 错题数量
}