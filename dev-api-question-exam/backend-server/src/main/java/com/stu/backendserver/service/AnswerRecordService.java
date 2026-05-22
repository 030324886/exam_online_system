package com.stu.backendserver.service;
import com.stu.backendserver.entity.AnswerRecord;
import com.stu.backendserver.dto.AnswerSubmitDTO;
import java.util.List;

public interface AnswerRecordService {
    List<AnswerRecord> submitAnswers(AnswerSubmitDTO dto);
}