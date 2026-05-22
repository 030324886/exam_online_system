package com.stu.backendserver.controller;

import com.stu.backendserver.common.Result;
import com.stu.backendserver.dto.AnswerSubmitDTO;
import com.stu.backendserver.entity.AnswerRecord;
import com.stu.backendserver.service.AnswerRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/answer")
public class AnswerController {

    @Autowired
    private AnswerRecordService answerRecordService;

    /**
     * 学生提交答案 → 自动判分 → 保存记录 → 得分入库
     * 第三周核心接口
     */
    @PostMapping("/submit")
    public Result<List<AnswerRecord>> submit(@RequestBody AnswerSubmitDTO dto) {
        List<AnswerRecord> list = answerRecordService.submitAnswers(dto);
        return Result.success(list);
    }
}