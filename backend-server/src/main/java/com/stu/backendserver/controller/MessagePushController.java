package com.stu.backendserver.controller;

import com.stu.backendserver.common.ApiResponse;
import com.stu.backendserver.dto.BroadcastMessageRequest;
import com.stu.backendserver.dto.MessagePayload;
import com.stu.backendserver.dto.StudentSubmitNoticeRequest;
import com.stu.backendserver.service.MessagePushService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessagePushController {

    private static final String TEACHER_SUBMIT_NOTICE_TOPIC = "/topic/teacher/submit-notice";
    private final MessagePushService messagePushService;

    public MessagePushController(MessagePushService messagePushService) {
        this.messagePushService = messagePushService;
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.ok("message-push-service-ready", "OK");
    }

    @PostMapping("/student-submit")
    public ApiResponse<MessagePayload> studentSubmitNotice(@RequestBody StudentSubmitNoticeRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("examId", request.getExamId());
        data.put("examName", request.getExamName());
        data.put("studentId", request.getStudentId());
        data.put("studentName", request.getStudentName());
        data.put("className", request.getClassName());

        MessagePayload payload = new MessagePayload();
        payload.setType("STUDENT_SUBMIT");
        payload.setTitle("学生已交卷");
        payload.setContent(buildSubmitContent(request));
        payload.setTimestamp(LocalDateTime.now());
        payload.setData(data);

        messagePushService.sendToTopic(TEACHER_SUBMIT_NOTICE_TOPIC, payload);
        return ApiResponse.ok("submit-notice-pushed", payload);
    }

    @PostMapping("/broadcast")
    public ApiResponse<MessagePayload> broadcast(@RequestBody BroadcastMessageRequest request) {
        String destination = StringUtils.hasText(request.getDestination()) ? request.getDestination() : "/topic/system";

        MessagePayload payload = new MessagePayload();
        payload.setType(StringUtils.hasText(request.getType()) ? request.getType() : "SYSTEM_NOTICE");
        payload.setTitle(request.getTitle());
        payload.setContent(request.getContent());
        payload.setTimestamp(LocalDateTime.now());
        payload.setData(request.getData());

        messagePushService.sendToTopic(destination, payload);
        return ApiResponse.ok("broadcast-pushed", payload);
    }

    private String buildSubmitContent(StudentSubmitNoticeRequest request) {
        String examName = StringUtils.hasText(request.getExamName()) ? request.getExamName() : "未命名试卷";
        String studentName = StringUtils.hasText(request.getStudentName()) ? request.getStudentName() : "未知学生";
        String className = StringUtils.hasText(request.getClassName()) ? request.getClassName() : "未知班级";
        return studentName + "（" + className + "）已提交《" + examName + "》";
    }
}
