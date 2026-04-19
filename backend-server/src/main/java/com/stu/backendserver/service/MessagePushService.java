package com.stu.backendserver.service;

import com.stu.backendserver.dto.MessagePayload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessagePushService {

    public static final String TEACHER_SUBMIT_NOTICE_TOPIC = "/topic/teacher/submit-notice";
    public static final String STUDENT_GRADE_NOTICE_QUEUE = "/queue/exam-graded";
    private final SimpMessagingTemplate messagingTemplate;

    public MessagePushService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToTopic(String destination, MessagePayload payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }

    public void sendToUser(String userId, String destination, MessagePayload payload) {
        messagingTemplate.convertAndSendToUser(userId, destination, payload);
    }

    public void notifyTeacherStudentSubmitted(Long paperId,
                                              String paperName,
                                              Long submissionId,
                                              Long studentId,
                                              String studentName,
                                              String className) {
        Map<String, Object> data = new HashMap<>();
        data.put("paperId", paperId);
        data.put("paperName", paperName);
        data.put("submissionId", submissionId);
        data.put("studentId", studentId);
        data.put("studentName", studentName);
        data.put("className", className);

        MessagePayload payload = new MessagePayload();
        payload.setType("STUDENT_SUBMIT");
        payload.setTitle("Student Submitted Exam");
        payload.setContent(buildTeacherNoticeContent(paperName, studentName, className));
        payload.setTimestamp(LocalDateTime.now());
        payload.setData(data);
        sendToTopic(TEACHER_SUBMIT_NOTICE_TOPIC, payload);
    }

    public void notifyStudentGraded(Long studentId,
                                    Long submissionId,
                                    Long paperId,
                                    String paperName,
                                    Integer totalScore,
                                    String teacherName) {
        Map<String, Object> data = new HashMap<>();
        data.put("submissionId", submissionId);
        data.put("paperId", paperId);
        data.put("paperName", paperName);
        data.put("totalScore", totalScore);
        data.put("teacherName", teacherName);

        MessagePayload payload = new MessagePayload();
        payload.setType("SUBMISSION_GRADED");
        payload.setTitle("Exam Graded");
        payload.setContent(buildStudentNoticeContent(paperName, totalScore, teacherName));
        payload.setTimestamp(LocalDateTime.now());
        payload.setData(data);
        sendToUser(String.valueOf(studentId), STUDENT_GRADE_NOTICE_QUEUE, payload);
    }

    private String buildTeacherNoticeContent(String paperName, String studentName, String className) {
        String safePaperName = paperName == null ? "Unnamed Paper" : paperName;
        String safeStudentName = studentName == null ? "Unknown Student" : studentName;
        String safeClassName = className == null ? "Unknown Class" : className;
        return safeStudentName + " from " + safeClassName + " submitted " + safePaperName;
    }

    private String buildStudentNoticeContent(String paperName, Integer totalScore, String teacherName) {
        String safePaperName = paperName == null ? "your exam" : paperName;
        String safeTeacherName = teacherName == null ? "teacher" : teacherName;
        String safeScore = totalScore == null ? "-" : String.valueOf(totalScore);
        return safeTeacherName + " graded " + safePaperName + ", total score: " + safeScore;
    }
}
