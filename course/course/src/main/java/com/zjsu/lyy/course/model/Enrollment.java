package com.zjsu.lyy.course.model;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Enrollment {
    @JsonIgnore
    private String id;
    
    private String courseId;
    private String studentId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
}