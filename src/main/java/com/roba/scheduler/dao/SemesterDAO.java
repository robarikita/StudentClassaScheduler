package com.roba.scheduler.dao;

import com.roba.scheduler.model.Semester;
import com.roba.scheduler.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SemesterDAO {

    // Add new semester
    public boolean addSemester(Semester semester) {
        String sql = "INSERT INTO semesters (semester_code, semester_name, start_date, end_date, registration_start, registration_end, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, semester.getSemesterCode());
            stmt.setString(2, semester.getSemesterName());
            stmt.setDate(3, semester.getStartDate());
            stmt.setDate(4, semester.getEndDate());
            stmt.setDate(5, semester.getRegistrationStart());
            stmt.setDate(6, semester.getRegistrationEnd());
            stmt.setBoolean(7, semester.getIsActive());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all semesters
    public List<Semester> getAllSemesters() {
        List<Semester> semesters = new ArrayList<>();
        String sql = "SELECT * FROM semesters ORDER BY start_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                semesters.add(extractSemesterFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return semesters;
    }

    // Get active semester
    public Semester getActiveSemester() {
        String sql = "SELECT * FROM semesters WHERE is_active = TRUE LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return extractSemesterFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get semester by ID
    public Semester getSemesterById(int semesterId) {
        String sql = "SELECT * FROM semesters WHERE semester_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, semesterId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractSemesterFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Update semester
    public boolean updateSemester(Semester semester) {
        String sql = "UPDATE semesters SET semester_code = ?, semester_name = ?, start_date = ?, end_date = ?, registration_start = ?, registration_end = ?, is_active = ? WHERE semester_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, semester.getSemesterCode());
            stmt.setString(2, semester.getSemesterName());
            stmt.setDate(3, semester.getStartDate());
            stmt.setDate(4, semester.getEndDate());
            stmt.setDate(5, semester.getRegistrationStart());
            stmt.setDate(6, semester.getRegistrationEnd());
            stmt.setBoolean(7, semester.getIsActive());
            stmt.setInt(8, semester.getSemesterId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Set active semester (deactivate others)
    public boolean setActiveSemester(int semesterId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // First, deactivate all semesters
            String deactivateSql = "UPDATE semesters SET is_active = FALSE";
            try (PreparedStatement deactivateStmt = conn.prepareStatement(deactivateSql)) {
                deactivateStmt.executeUpdate();
            }

            // Then activate the selected semester
            String activateSql = "UPDATE semesters SET is_active = TRUE WHERE semester_id = ?";
            try (PreparedStatement activateStmt = conn.prepareStatement(activateSql)) {
                activateStmt.setInt(1, semesterId);
                return activateStmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete semester
    public boolean deleteSemester(int semesterId) {
        String sql = "DELETE FROM semesters WHERE semester_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, semesterId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to extract semester from ResultSet
    private Semester extractSemesterFromResultSet(ResultSet rs) throws SQLException {
        Semester semester = new Semester();
        semester.setSemesterId(rs.getInt("semester_id"));
        semester.setSemesterCode(rs.getString("semester_code"));
        semester.setSemesterName(rs.getString("semester_name"));
        semester.setStartDate(rs.getDate("start_date"));
        semester.setEndDate(rs.getDate("end_date"));
        semester.setRegistrationStart(rs.getDate("registration_start"));
        semester.setRegistrationEnd(rs.getDate("registration_end"));
        semester.setIsActive(rs.getBoolean("is_active"));
        return semester;
    }
}