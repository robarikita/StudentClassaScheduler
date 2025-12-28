package com.roba.scheduler.model;

import java.sql.Date;

public class Semester {
    private int semesterId;
    private String semesterCode;
    private String semesterName;
    private Date startDate;
    private Date endDate;
    private Date registrationStart;
    private Date registrationEnd;
    private boolean isActive;

    // Constructors
    public Semester() {}

    public Semester(String semesterCode, String semesterName, Date startDate, Date endDate) {
        this.semesterCode = semesterCode;
        this.semesterName = semesterName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public int getSemesterId() { return semesterId; }
    public void setSemesterId(int semesterId) { this.semesterId = semesterId; }

    public String getSemesterCode() { return semesterCode; }
    public void setSemesterCode(String semesterCode) { this.semesterCode = semesterCode; }

    public String getSemesterName() { return semesterName; }
    public void setSemesterName(String semesterName) { this.semesterName = semesterName; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Date getRegistrationStart() { return registrationStart; }
    public void setRegistrationStart(Date registrationStart) { this.registrationStart = registrationStart; }

    public Date getRegistrationEnd() { return registrationEnd; }
    public void setRegistrationEnd(Date registrationEnd) { this.registrationEnd = registrationEnd; }

    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }

    // Helper method
    public boolean isRegistrationOpen() {
        Date today = new Date(System.currentTimeMillis());
        return today.after(registrationStart) && today.before(registrationEnd);
    }
}