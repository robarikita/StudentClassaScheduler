package com.roba.scheduler.dao;

import com.roba.scheduler.model.Student;
import com.roba.scheduler.util.DatabaseConnection;
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