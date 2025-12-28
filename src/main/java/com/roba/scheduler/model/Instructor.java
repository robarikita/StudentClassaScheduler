package com.roba.scheduler.model;

public class Instructor {
    private int instructorId;
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String officeNumber;
    private String phone;
    private int maxCoursesPerSemester;
    private User user; // For joined queries

    // Constructors
    public Instructor() {}

    public Instructor(String firstName, String lastName, String department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
    }

    // Getters and Setters
    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(String officeNumber) { this.officeNumber = officeNumber; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getMaxCoursesPerSemester() { return maxCoursesPerSemester; }
    public void setMaxCoursesPerSemester(int maxCoursesPerSemester) {
        this.maxCoursesPerSemester = maxCoursesPerSemester;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getDisplayInfo() {
        return String.format("%s %s - %s", firstName, lastName, department);
    }
}