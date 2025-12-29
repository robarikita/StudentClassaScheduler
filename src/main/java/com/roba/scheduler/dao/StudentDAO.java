package com.roba.scheduler.dao;

import com.roba.scheduler.model.Student;
import com.roba.scheduler.util.DatabaseConnection;
import com.roba.scheduler.util.IDManager;

import java.sql.*;

public class StudentDAO {

    public Student getStudentByUserId(int userId) {
        String sql = "SELECT * FROM students WHERE user_id = ?";
        Student student = null;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setUserId(rs.getInt("user_id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setEmail(rs.getString("email"));
                student.setMajor(rs.getString("major"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }
      // Add new student with smart ID reuse
    public boolean addStudent(Student student) {
        // Get next available ID (reuse deleted IDs)
        int nextId = IDManager.getNextAvailableId("students", "student_id");

        String sql = "INSERT INTO students (student_id, user_id, first_name, last_name, email, major) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nextId);
            stmt.setInt(2, student.getUserId());
            stmt.setString(3, student.getFirstName());
            stmt.setString(4, student.getLastName());
            stmt.setString(5, student.getEmail());
            stmt.setString(6, student.getMajor());

            boolean success = stmt.executeUpdate() > 0;

            // Fix auto-increment to avoid conflicts
            if (success) {
                IDManager.fixAutoIncrement("students", "student_id");
            }

            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete student and fix IDs
    public boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            boolean success = stmt.executeUpdate() > 0;

            // Fix auto-increment after delete
            if (success) {
                IDManager.fixAutoIncrement("students", "student_id");
            }

            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // In StudentDAO.java, add this method if you want to keep the original code
    public Student login(String username, String password) {
        // This would join users and students table
        String sql = "SELECT s.* FROM students s " +
                "INNER JOIN users u ON s.user_id = u.user_id " +
                "WHERE u.username = ? AND u.password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setUserId(rs.getInt("user_id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setEmail(rs.getString("email"));
                student.setMajor(rs.getString("major"));
                return student;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}