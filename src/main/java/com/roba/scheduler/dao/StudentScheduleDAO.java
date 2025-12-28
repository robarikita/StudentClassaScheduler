package com.roba.scheduler.dao;

import com.roba.scheduler.model.StudentSchedule;
import com.roba.scheduler.util.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class StudentScheduleDAO {

    // Existing methods from previous phases + new methods for Phase 6

    // EXISTING METHODS (from Phase 1-5) - KEEP THESE

    public boolean addStudentSchedule(StudentSchedule schedule) {
        // Your existing implementation
        return false;
    }

    public boolean deleteStudentSchedule(int studentScheduleId) {
        // Your existing implementation
        return false;
    }

    public StudentSchedule getStudentScheduleById(int studentScheduleId) {
        // Your existing implementation
        return null;
    }

    public List<StudentSchedule> getAllStudentSchedules() {
        // Your existing implementation
        return new ArrayList<>();
    }

    // NEW METHODS FOR PHASE 6 - ADD THESE IF NOT ALREADY PRESENT

    public List<Map<String, Object>> getStudentSchedule(int studentId) {
        List<Map<String, Object>> schedule = new ArrayList<>();
        String sql = "SELECT ss.student_schedule_id, ss.status, " +
                     "c.course_code, c.course_name, c.description, " +
                     "CONCAT(i.first_name, ' ', i.last_name) as instructor_name, " +
                     "ts.day_of_week, TIME_FORMAT(ts.start_time, '%H:%i') as start_time, " +
                     "TIME_FORMAT(ts.end_time, '%H:%i') as end_time, " +
                     "cl.building, cl.room_number, sc.section_number " +
                     "FROM student_schedule ss " +
                     "JOIN scheduled_classes sc ON ss.schedule_id = sc.schedule_id " +
                     "JOIN courses c ON sc.course_id = c.course_id " +
                     "LEFT JOIN instructors i ON sc.instructor_id = i.instructor_id " +
                     "JOIN time_slots ts ON sc.slot_id = ts.slot_id " +
                     "JOIN classrooms cl ON sc.room_id = cl.room_id " +
                     "WHERE ss.student_id = ? AND ss.status = 'Enrolled' " +
                     "ORDER BY FIELD(ts.day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'), " +
                     "ts.start_time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("studentScheduleId", rs.getInt("student_schedule_id"));
                item.put("courseCode", rs.getString("course_code"));
                item.put("courseName", rs.getString("course_name"));
                item.put("instructorName", rs.getString("instructor_name"));
                item.put("dayOfWeek", rs.getString("day_of_week"));
                item.put("startTime", rs.getString("start_time"));
                item.put("endTime", rs.getString("end_time"));
                item.put("building", rs.getString("building"));
                item.put("roomNumber", rs.getString("room_number"));
                item.put("sectionNumber", rs.getString("section_number"));
                item.put("status", rs.getString("status"));
                schedule.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedule;
    }

    public boolean hasScheduleConflict(int studentId, int scheduleId) {
        String sql = "SELECT COUNT(*) as conflict_count " +
                     "FROM student_schedule ss " +
                     "JOIN scheduled_classes sc1 ON ss.schedule_id = sc1.schedule_id " +
                     "JOIN scheduled_classes sc2 ON sc2.schedule_id = ? " +
                     "JOIN time_slots ts1 ON sc1.slot_id = ts1.slot_id " +
                     "JOIN time_slots ts2 ON sc2.slot_id = ts2.slot_id " +
                     "WHERE ss.student_id = ? AND ss.status = 'Enrolled' " +
                     "AND ts1.day_of_week = ts2.day_of_week " +
                     "AND ((ts1.start_time <= ts2.end_time AND ts1.end_time >= ts2.start_time) " +
                     "OR (ts2.start_time <= ts1.end_time AND ts2.end_time >= ts1.start_time))";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, scheduleId);
            pstmt.setInt(2, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("conflict_count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isAlreadyEnrolled(int studentId, int scheduleId) {
        String sql = "SELECT COUNT(*) FROM student_schedule " +
                     "WHERE student_id = ? AND schedule_id = ? AND status = 'Enrolled'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, scheduleId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean enrollStudent(int studentId, int scheduleId) {
        String sql = "INSERT INTO student_schedule (student_id, schedule_id, status) " +
                     "VALUES (?, ?, 'Enrolled')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, scheduleId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            // Check for duplicate entry
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry error code
                return false;
            }
            e.printStackTrace();
        }
        return false;
    }

    public boolean dropClass(int studentScheduleId) {
        String sql = "UPDATE student_schedule SET status = 'Dropped' " +
                     "WHERE student_schedule_id = ? AND status = 'Enrolled'";

        try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, studentScheduleId);
        return pstmt.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

public int getScheduleIdFromStudentSchedule(int studentScheduleId) {
    String sql = "SELECT schedule_id FROM student_schedule WHERE student_schedule_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, studentScheduleId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("schedule_id");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
}

}