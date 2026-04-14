package com.stu.backendserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("question")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer type; // 1-单选 2-多选 3-填空 4-简答
    private String content; // 题目内容

    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<String> options; // 选项，JSON数组

    private String answer; // 正确答案
    private String analysis; // 答案解析
    private String knowledgePoint; // 知识点
    private Integer difficulty; // 难度1-5
    private Long creatorId; // 出题人ID
    private LocalDateTime createTime;
}