package com.stu.backendserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("answer_record")
public class AnswerRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;       // 学生ID
    private Long paperId;      // 试卷ID
    private Long questionId;   // 题目ID
    private String answer;     // 学生答案
    private Integer isCorrect; // 0错误 1正确
    private Integer score;     // 本题得分
    private LocalDateTime submitTime;
}