package com.roba.scheduler.dao;

import com.roba.scheduler.model.Instructor;
import com.roba.scheduler.util.DatabaseConnection;
import com.roba.scheduler.util.IDManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {


    // Get all instructors
    public List<Instructor> getAllInstructors() {
        List<Instructor> instructors = new ArrayList<>();
        // UPDATED: Now shows ALL instructors without any filtering
        String sql = """
            SELECT i.*, u.username
            FROM instructors i
            LEFT JOIN users u ON i.user_id = u.user_id
            ORDER BY i.last_name, i.first_name
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                instructors.add(extractInstructorFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return instructors;
    }

    // Get instructor by ID
    public Instructor getInstructorById(int instructorId) {
        String sql = """
            SELECT i.*, u.username
            FROM instructors i
            LEFT JOIN users u ON i.user_id = u.user_id
            WHERE i.instructor_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractInstructorFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get instructor by user ID
    public Instructor getInstructorByUserId(int userId) {
        String sql = """
            SELECT i.*, u.username
            FROM instructors i
            LEFT JOIN users u ON i.user_id = u.user_id
            WHERE i.user_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractInstructorFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Update instructor
    public boolean updateInstructor(Instructor instructor) {
        String sql = "UPDATE instructors SET first_name = ?, last_name = ?, email = ?, department = ?, office_number = ?, phone = ?, max_courses_per_semester = ? WHERE instructor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, instructor.getFirstName());
            stmt.setString(2, instructor.getLastName());
            stmt.setString(3, instructor.getEmail());
            stmt.setString(4, instructor.getDepartment());
            stmt.setString(5, instructor.getOfficeNumber());
            stmt.setString(6, instructor.getPhone());
            stmt.setInt(7, instructor.getMaxCoursesPerSemester());
            stmt.setInt(8, instructor.getInstructorId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Get available instructors for a time slot
    public List<Instructor> getAvailableInstructors(int slotId, int semesterId) {
        List<Instructor> instructors = new ArrayList<>();
        String sql = """
            SELECT i.*, u.username FROM instructors i
            LEFT JOIN users u ON i.user_id = u.user_id
            WHERE i.instructor_id NOT IN (
                SELECT sc.instructor_id FROM scheduled_classes sc
                WHERE sc.slot_id = ? AND sc.semester_id = ? AND sc.is_active = TRUE
            )
            ORDER BY i.last_name, i.first_name
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, slotId);
            stmt.setInt(2, semesterId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                instructors.add(extractInstructorFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return instructors;
    }

    // Helper method to extract instructor from ResultSet
    private Instructor extractInstructorFromResultSet(ResultSet rs) throws SQLException {
        Instructor instructor = new Instructor();
        instructor.setInstructorId(rs.getInt("instructor_id"));
        instructor.setUserId(rs.getInt("user_id"));
        instructor.setFirstName(rs.getString("first_name"));
        instructor.setLastName(rs.getString("last_name"));
        instructor.setEmail(rs.getString("email"));
        instructor.setDepartment(rs.getString("department"));
        instructor.setOfficeNumber(rs.getString("office_number"));
        instructor.setPhone(rs.getString("phone"));
        instructor.setMaxCoursesPerSemester(rs.getInt("max_courses_per_semester"));
        return instructor;
    }

    // Delete instructor and fix IDs
    public boolean deleteInstructor(int instructorId) {
        String sql = "DELETE FROM instructors WHERE instructor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            boolean success = stmt.executeUpdate() > 0;

            // Fix auto-increment after delete
            if (success) {
                IDManager.fixAutoIncrement("instructors", "instructor_id");
            }

            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



// Add new instructor with smart ID reuse
public boolean addInstructor(Instructor instructor) {
    // Get next available ID (reuse deleted IDs)
    int nextId = IDManager.getNextAvailableId("instructors", "instructor_id");

    String sql = "INSERT INTO instructors (instructor_id, user_id, first_name, last_name, email, department, office_number, phone, max_courses_per_semester) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, nextId);

        // Handle user_id - get as int, check if > 0
        int userId = instructor.getUserId(); // This returns int (0 if not set)
        if (userId > 0) {
            stmt.setInt(2, userId);
        } else {
            stmt.setNull(2, Types.INTEGER);
        }

        stmt.setString(3, instructor.getFirstName());
        stmt.setString(4, instructor.getLastName());
        stmt.setString(5, instructor.getEmail());
        stmt.setString(6, instructor.getDepartment());
        stmt.setString(7, instructor.getOfficeNumber());
        stmt.setString(8, instructor.getPhone());
        stmt.setInt(9, instructor.getMaxCoursesPerSemester());

        boolean success = stmt.executeUpdate() > 0;

        // Fix auto-increment to avoid conflicts
        if (success) {
            IDManager.fixAutoIncrement("instructors", "instructor_id");
        }

        return success;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
}