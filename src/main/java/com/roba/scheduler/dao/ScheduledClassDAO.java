package com.roba.scheduler.dao;

import com.roba.scheduler.model.*;
import com.roba.scheduler.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduledClassDAO {

    // Create new scheduled class
    public boolean addScheduledClass(ScheduledClass scheduledClass) {
        String sql = """
            INSERT INTO scheduled_classes
            (course_id, instructor_id, room_id, slot_id, semester_id, section_number, max_capacity, current_enrollment, is_active)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, scheduledClass.getCourse().getCourseId());
            if (scheduledClass.getInstructor() != null) {
                stmt.setInt(2, scheduledClass.getInstructor().getInstructorId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            if (scheduledClass.getClassroom() != null) {
                stmt.setInt(3, scheduledClass.getClassroom().getRoomId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setInt(4, scheduledClass.getTimeSlot().getSlotId());
            stmt.setInt(5, scheduledClass.getSemester().getSemesterId());
            stmt.setString(6, scheduledClass.getSectionNumber());
            stmt.setInt(7, scheduledClass.getMaxCapacity());
            stmt.setInt(8, scheduledClass.getCurrentEnrollment());
            stmt.setBoolean(9, scheduledClass.getIsActive());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all scheduled classes with details
    public List<ScheduledClass> getAllScheduledClasses() {
        List<ScheduledClass> scheduledClasses = new ArrayList<>();
        String sql = """
            SELECT sc.*,
                   c.course_code, c.course_name, c.description, c.credits,
                   i.first_name as instr_first, i.last_name as instr_last, i.department,
                   r.room_number, r.building, r.capacity, r.room_type,
                   t.day_of_week, t.start_time, t.end_time, t.slot_name,
                   s.semester_code, s.semester_name, s.start_date, s.end_date
            FROM scheduled_classes sc
            LEFT JOIN courses c ON sc.course_id = c.course_id
            LEFT JOIN instructors i ON sc.instructor_id = i.instructor_id
            LEFT JOIN classrooms r ON sc.room_id = r.room_id
            LEFT JOIN time_slots t ON sc.slot_id = t.slot_id
            LEFT JOIN semesters s ON sc.semester_id = s.semester_id
            WHERE sc.is_active = TRUE
            ORDER BY s.start_date DESC, t.day_of_week, t.start_time
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                scheduledClasses.add(extractScheduledClassFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return scheduledClasses;
    }

    // Get scheduled classes by semester
    public List<ScheduledClass> getScheduledClassesBySemester(int semesterId) {
        List<ScheduledClass> scheduledClasses = new ArrayList<>();
        String sql = """
            SELECT sc.*,
                   c.course_code, c.course_name, c.description, c.credits,
                   i.first_name as instr_first, i.last_name as instr_last, i.department,
                   r.room_number, r.building, r.capacity, r.room_type,
                   t.day_of_week, t.start_time, t.end_time, t.slot_name,
                   s.semester_code, s.semester_name, s.start_date, s.end_date
            FROM scheduled_classes sc
            LEFT JOIN courses c ON sc.course_id = c.course_id
            LEFT JOIN instructors i ON sc.instructor_id = i.instructor_id
            LEFT JOIN classrooms r ON sc.room_id = r.room_id
            LEFT JOIN time_slots t ON sc.slot_id = t.slot_id
            LEFT JOIN semesters s ON sc.semester_id = s.semester_id
            WHERE sc.semester_id = ? AND sc.is_active = TRUE
            ORDER BY t.day_of_week, t.start_time
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, semesterId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                scheduledClasses.add(extractScheduledClassFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return scheduledClasses;
    }

    // Get scheduled class by ID
    public ScheduledClass getScheduledClassById(int scheduleId) {
        String sql = """
            SELECT sc.*,
                   c.course_code, c.course_name, c.description, c.credits,
                   i.first_name as instr_first, i.last_name as instr_last, i.department,
                   r.room_number, r.building, r.capacity, r.room_type,
                   t.day_of_week, t.start_time, t.end_time, t.slot_name,
                   s.semester_code, s.semester_name, s.start_date, s.end_date
            FROM scheduled_classes sc
            LEFT JOIN courses c ON sc.course_id = c.course_id
            LEFT JOIN instructors i ON sc.instructor_id = i.instructor_id
            LEFT JOIN classrooms r ON sc.room_id = r.room_id
            LEFT JOIN time_slots t ON sc.slot_id = t.slot_id
            LEFT JOIN semesters s ON sc.semester_id = s.semester_id
            WHERE sc.schedule_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, scheduleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractScheduledClassFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Update scheduled class
    public boolean updateScheduledClass(ScheduledClass scheduledClass) {
        String sql = """
            UPDATE scheduled_classes
            SET course_id = ?, instructor_id = ?, room_id = ?, slot_id = ?,
                semester_id = ?, section_number = ?, max_capacity = ?,
                current_enrollment = ?, is_active = ?
            WHERE schedule_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, scheduledClass.getCourse().getCourseId());
            if (scheduledClass.getInstructor() != null) {
                stmt.setInt(2, scheduledClass.getInstructor().getInstructorId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            if (scheduledClass.getClassroom() != null) {
                stmt.setInt(3, scheduledClass.getClassroom().getRoomId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setInt(4, scheduledClass.getTimeSlot().getSlotId());
            stmt.setInt(5, scheduledClass.getSemester().getSemesterId());
            stmt.setString(6, scheduledClass.getSectionNumber());
            stmt.setInt(7, scheduledClass.getMaxCapacity());
            stmt.setInt(8, scheduledClass.getCurrentEnrollment());
            stmt.setBoolean(9, scheduledClass.getIsActive());
            stmt.setInt(10, scheduledClass.getScheduleId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete scheduled class (soft delete)
    public boolean deleteScheduledClass(int scheduleId) {
        String sql = "UPDATE scheduled_classes SET is_active = FALSE WHERE schedule_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, scheduleId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Increment enrollment count
    public boolean incrementEnrollment(int scheduleId) {
        String sql = "UPDATE scheduled_classes SET current_enrollment = current_enrollment + 1 WHERE schedule_id = ? AND current_enrollment < max_capacity";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, scheduleId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Decrement enrollment count
    public boolean decrementEnrollment(int scheduleId) {
        String sql = "UPDATE scheduled_classes SET current_enrollment = current_enrollment - 1 WHERE schedule_id = ? AND current_enrollment > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, scheduleId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check for conflicts
    public boolean hasRoomConflict(int roomId, int slotId, int semesterId, int excludeScheduleId) {
        String sql = """
            SELECT COUNT(*) as count FROM scheduled_classes
            WHERE room_id = ? AND slot_id = ? AND semester_id = ?
            AND is_active = TRUE AND schedule_id != ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.setInt(2, slotId);
            stmt.setInt(3, semesterId);
            stmt.setInt(4, excludeScheduleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasInstructorConflict(int instructorId, int slotId, int semesterId, int excludeScheduleId) {
        String sql = """
            SELECT COUNT(*) as count FROM scheduled_classes
            WHERE instructor_id = ? AND slot_id = ? AND semester_id = ?
            AND is_active = TRUE AND schedule_id != ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            stmt.setInt(2, slotId);
            stmt.setInt(3, semesterId);
            stmt.setInt(4, excludeScheduleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Helper method to extract scheduled class from ResultSet
    private ScheduledClass extractScheduledClassFromResultSet(ResultSet rs) throws SQLException {
        ScheduledClass scheduledClass = new ScheduledClass();
        scheduledClass.setScheduleId(rs.getInt("schedule_id"));
        scheduledClass.setSectionNumber(rs.getString("section_number"));
        scheduledClass.setMaxCapacity(rs.getInt("max_capacity"));
        scheduledClass.setCurrentEnrollment(rs.getInt("current_enrollment"));
        scheduledClass.setIsActive(rs.getBoolean("is_active"));

        // Course
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setDescription(rs.getString("description"));
        course.setCredits(rs.getInt("credits"));
        scheduledClass.setCourse(course);

        // Instructor (if exists)
        if (rs.getInt("instructor_id") != 0) {
            Instructor instructor = new Instructor();
            instructor.setInstructorId(rs.getInt("instructor_id"));
            instructor.setFirstName(rs.getString("instr_first"));
            instructor.setLastName(rs.getString("instr_last"));
            instructor.setDepartment(rs.getString("department"));
            scheduledClass.setInstructor(instructor);
        }

        // Classroom (if exists)
        if (rs.getInt("room_id") != 0) {
            Classroom classroom = new Classroom();
            classroom.setRoomId(rs.getInt("room_id"));
            classroom.setRoomNumber(rs.getString("room_number"));
            classroom.setBuilding(rs.getString("building"));
            classroom.setCapacity(rs.getInt("capacity"));
            classroom.setRoomType(rs.getString("room_type"));
            scheduledClass.setClassroom(classroom);
        }

        // Time slot
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setSlotId(rs.getInt("slot_id"));
        timeSlot.setDayOfWeek(rs.getString("day_of_week"));
        timeSlot.setStartTime(rs.getTime("start_time"));
        timeSlot.setEndTime(rs.getTime("end_time"));
        timeSlot.setSlotName(rs.getString("slot_name"));
        scheduledClass.setTimeSlot(timeSlot);

        // Semester
        Semester semester = new Semester();
        semester.setSemesterId(rs.getInt("semester_id"));
        semester.setSemesterCode(rs.getString("semester_code"));
        semester.setSemesterName(rs.getString("semester_name"));
        semester.setStartDate(rs.getDate("start_date"));
        semester.setEndDate(rs.getDate("end_date"));
        scheduledClass.setSemester(semester);

        return scheduledClass;
    }
    // Add this method to your existing ScheduledClassDAO class:

public List<Map<String, Object>> getAvailableClassesForStudent(int studentId) {
    List<Map<String, Object>> classes = new ArrayList<>();
    String sql = "SELECT sc.schedule_id, c.course_code, c.course_name, c.description, " +
                 "CONCAT(i.first_name, ' ', i.last_name) as instructor_name, " +
                 "cl.room_number, cl.building, cl.capacity, " +
                 "ts.day_of_week, TIME_FORMAT(ts.start_time, '%H:%i') as start_time, " +
                 "TIME_FORMAT(ts.end_time, '%H:%i') as end_time, " +
                 "sc.section_number, sc.max_capacity, sc.current_enrollment, " +
                 "s.semester_name, s.semester_code " +
                 "FROM scheduled_classes sc " +
                 "JOIN courses c ON sc.course_id = c.course_id " +
                 "LEFT JOIN instructors i ON sc.instructor_id = i.instructor_id " +
                 "JOIN classrooms cl ON sc.room_id = cl.room_id " +
                 "JOIN time_slots ts ON sc.slot_id = ts.slot_id " +
                 "JOIN semesters s ON sc.semester_id = s.semester_id " +
                 "WHERE sc.is_active = TRUE AND s.is_active = TRUE " +
                 "AND sc.current_enrollment < sc.max_capacity " +
                 "AND sc.schedule_id NOT IN ( " +
                 "    SELECT schedule_id FROM student_schedule " +
                 "    WHERE student_id = ? AND status IN ('Enrolled', 'Waitlisted') " +
                 ") " +
                 "ORDER BY c.course_code, sc.section_number, ts.day_of_week, ts.start_time";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, studentId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Map<String, Object> classInfo = new HashMap<>();
            classInfo.put("scheduleId", rs.getInt("schedule_id"));
            classInfo.put("courseCode", rs.getString("course_code"));
            classInfo.put("courseName", rs.getString("course_name"));
            classInfo.put("description", rs.getString("description"));
            classInfo.put("instructorName", rs.getString("instructor_name"));
            classInfo.put("roomNumber", rs.getString("room_number"));
            classInfo.put("building", rs.getString("building"));
            classInfo.put("dayOfWeek", rs.getString("day_of_week"));
            classInfo.put("startTime", rs.getString("start_time"));
            classInfo.put("endTime", rs.getString("end_time"));
            classInfo.put("sectionNumber", rs.getString("section_number"));
            classInfo.put("maxCapacity", rs.getInt("max_capacity"));
            classInfo.put("currentEnrollment", rs.getInt("current_enrollment"));
            classInfo.put("semesterName", rs.getString("semester_name"));
            classInfo.put("semesterCode", rs.getString("semester_code"));
            classes.add(classInfo);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return classes;
}

public void updateEnrollmentCount(int scheduleId, int change) {
    String sql = "UPDATE scheduled_classes SET current_enrollment = current_enrollment + ? WHERE schedule_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, change);
        pstmt.setInt(2, scheduleId);
        pstmt.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}
public boolean isClassFull(int scheduleId) {
    String sql = "SELECT current_enrollment, max_capacity FROM scheduled_classes WHERE schedule_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, scheduleId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int currentEnrollment = rs.getInt("current_enrollment");
            int maxCapacity = rs.getInt("max_capacity");
            return currentEnrollment >= maxCapacity;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return true; // Default to full if error
}

}