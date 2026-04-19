package com.stu.backendserver.dto;

import java.util.List;

public class ManualGradeRequest {
    private Long submissionId;
    private Long teacherId;
    private String teacherName;
    private List<ManualGradeItem> items;

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public List<ManualGradeItem> getItems() {
        return items;
    }

    public void setItems(List<ManualGradeItem> items) {
        this.items = items;
    }
}
