package com.roba.scheduler.model;

import java.sql.Timestamp;

public class StudentSchedule {
    private int studentScheduleId;
    private Student student;
    private ScheduledClass scheduledClass;
    private Timestamp enrollmentDate;
    private String status;
    private String grade;

    // Constructors
    public StudentSchedule() {}

    public StudentSchedule(Student student, ScheduledClass scheduledClass) {
        this.student = student;
        this.scheduledClass = scheduledClass;
        this.status = "Enrolled";
    }

    // Getters and Setters
    public int getStudentScheduleId() { return studentScheduleId; }
    public void setStudentScheduleId(int studentScheduleId) { this.studentScheduleId = studentScheduleId; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public ScheduledClass getScheduledClass() { return scheduledClass; }
    public void setScheduledClass(ScheduledClass scheduledClass) { this.scheduledClass = scheduledClass; }

    public Timestamp getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Timestamp enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}