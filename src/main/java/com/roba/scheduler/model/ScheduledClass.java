package com.roba.scheduler.model;

public class ScheduledClass {
    private int scheduleId;
    private Course course;
    private Instructor instructor;
    private Classroom classroom;
    private TimeSlot timeSlot;
    private Semester semester;
    private String sectionNumber;
    private int maxCapacity;
    private int currentEnrollment;
    private boolean isActive;

    // Constructors
    public ScheduledClass() {}

    public ScheduledClass(Course course, Semester semester, TimeSlot timeSlot) {
        this.course = course;
        this.semester = semester;
        this.timeSlot = timeSlot;
    }

    // Getters and Setters
    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Instructor getInstructor() { return instructor; }
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }

    public Classroom getClassroom() { return classroom; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }

    public TimeSlot getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }

    public Semester getSemester() { return semester; }
    public void setSemester(Semester semester) { this.semester = semester; }

    public String getSectionNumber() { return sectionNumber; }
    public void setSectionNumber(String sectionNumber) { this.sectionNumber = sectionNumber; }

    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }

    public int getCurrentEnrollment() { return currentEnrollment; }
    public void setCurrentEnrollment(int currentEnrollment) { this.currentEnrollment = currentEnrollment; }

    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }

    // Helper methods
    public boolean hasSeatsAvailable() {
        return currentEnrollment < maxCapacity;
    }

    public int getAvailableSeats() {
        return maxCapacity - currentEnrollment;
    }

    public String getClassDisplay() {
        return String.format("%s-%s: %s",
            course.getCourseCode(), sectionNumber, course.getCourseName());
    }

    public String getFullInfo() {
        return String.format("%s-%s: %s | %s | %s | %s",
            course.getCourseCode(), sectionNumber, course.getCourseName(),
            instructor != null ? instructor.getFullName() : "TBA",
            classroom != null ? classroom.getFullRoomName() : "TBA",
            timeSlot != null ? timeSlot.getDisplayTime() : "TBA");
    }
}