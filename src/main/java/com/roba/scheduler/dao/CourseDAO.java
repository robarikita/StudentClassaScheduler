package com.roba.scheduler.dao;

import com.roba.scheduler.model.Course;
import com.roba.scheduler.util.DatabaseConnection;
import com.roba.scheduler.util.IDManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {



    // Get all courses
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_id";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));
                course.setDescription(rs.getString("description"));
                course.setCredits(rs.getInt("credits"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    // Get course by ID (for EditCourseServlet)
    public Course getCourseById(int courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        Course course = null;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));
                course.setDescription(rs.getString("description"));
                course.setCredits(rs.getInt("credits"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return course;
    }
// Add new course with smart ID reuse
    public boolean addCourse(Course course) {
        // Get next available ID (reuse deleted IDs)
        int nextId = IDManager.getNextAvailableId("courses", "course_id");

        String sql = "INSERT INTO courses (course_id, course_code, course_name, description, credits) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nextId);
            stmt.setString(2, course.getCourseCode());
            stmt.setString(3, course.getCourseName());
            stmt.setString(4, course.getDescription());
            stmt.setInt(5, course.getCredits());

            boolean success = stmt.executeUpdate() > 0;

            // Fix auto-increment to avoid conflicts
            if (success) {
                IDManager.fixAutoIncrement("courses", "course_id");
            }

            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete course and fix IDs
    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            boolean success = stmt.executeUpdate() > 0;

            // Fix auto-increment after delete
            if (success) {
                IDManager.fixAutoIncrement("courses", "course_id");
            }

            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update course (for EditCourseServlet)
public boolean updateCourse(Course course) {
    String sql = "UPDATE courses SET course_code = ?, course_name = ?, description = ?, credits = ? WHERE course_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, course.getCourseCode());
        stmt.setString(2, course.getCourseName());
        stmt.setString(3, course.getDescription());
        stmt.setInt(4, course.getCredits());
        stmt.setInt(5, course.getCourseId());

        return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

}