package com.stu.backendserver.service.impl;

import com.stu.backendserver.dto.AnswerSubmitDTO;
import com.stu.backendserver.entity.AnswerRecord;
import com.stu.backendserver.entity.Question;
import com.stu.backendserver.mapper.AnswerRecordMapper;
import com.stu.backendserver.mapper.QuestionMapper;
import com.stu.backendserver.service.AnswerRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnswerRecordServiceImpl implements AnswerRecordService {

    @Autowired
    private AnswerRecordMapper answerRecordMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public List<AnswerRecord> submitAnswers(AnswerSubmitDTO dto) {
        List<AnswerRecord> records = new ArrayList<>();

        for (var ans : dto.getAnswers()) {
            Long qid = ans.getQuestionId();
            Question question = questionMapper.selectById(qid);

            AnswerRecord record = new AnswerRecord();
            record.setUserId(dto.getUserId());
            record.setPaperId(dto.getPaperId());
            record.setQuestionId(qid);
            record.setAnswer(ans.getUserAnswer());
            record.setSubmitTime(LocalDateTime.now());

            boolean correct = false;
            if (question != null) {
                String userAns = ans.getUserAnswer() == null ? "" : ans.getUserAnswer().trim();
                String realAns = question.getAnswer() == null ? "" : question.getAnswer().trim();
                correct = userAns.equalsIgnoreCase(realAns);
            }

            record.setIsCorrect(correct ? 1 : 0);
            record.setScore(correct ? 1 : 0); // 答对1分，答错0分

            answerRecordMapper.insert(record);
            records.add(record);
        }
        return records;
    }
}