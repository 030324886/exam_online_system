package com.stu.backendserver.dto;

import lombok.Data;
import java.util.List;

@Data
public class AnswerSubmitDTO {
    private Long userId;
    private Long paperId;
    private List<QuestionAnswer> answers;

    // 改成 public static
    @Data
    public static class QuestionAnswer {
        private Long questionId;
        private String userAnswer;
    }
}