package com.roba.scheduler.dao;

import com.roba.scheduler.model.Classroom;
import com.roba.scheduler.util.DatabaseConnection;
import com.roba.scheduler.util.IDManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassroomDAO {


    // Get all classrooms
    public List<Classroom> getAllClassrooms() {
        List<Classroom> classrooms = new ArrayList<>();
        // REMOVED: WHERE is_active = TRUE - Now shows all classrooms
        String sql = "SELECT * FROM classrooms ORDER BY building, room_number";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                classrooms.add(extractClassroomFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return classrooms;
    }

    // Get classroom by ID
    public Classroom getClassroomById(int roomId) {
        String sql = "SELECT * FROM classrooms WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractClassroomFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Update classroom
    public boolean updateClassroom(Classroom classroom) {
        String sql = "UPDATE classrooms SET room_number = ?, building = ?, capacity = ?, room_type = ?, has_projector = ?, has_computers = ?, is_active = ? WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, classroom.getRoomNumber());
            stmt.setString(2, classroom.getBuilding());
            stmt.setInt(3, classroom.getCapacity());
            stmt.setString(4, classroom.getRoomType());
            stmt.setBoolean(5, classroom.getHasProjector());
            stmt.setBoolean(6, classroom.getHasComputers());
            stmt.setBoolean(7, classroom.getIsActive());
            stmt.setInt(8, classroom.getRoomId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE classroom permanently (HARD DELETE - NEW METHOD)
    public boolean deleteClassroomPermanently(int roomId) {
        String sql = "DELETE FROM classrooms WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Find available classrooms for a time slot and capacity
    public List<Classroom> getAvailableClassrooms(int slotId, int semesterId, int minCapacity) {
        List<Classroom> classrooms = new ArrayList<>();
        String sql = """
            SELECT c.* FROM classrooms c
            WHERE c.is_active = TRUE
            AND c.capacity >= ?
            AND c.room_id NOT IN (
                SELECT sc.room_id FROM scheduled_classes sc
                WHERE sc.slot_id = ? AND sc.semester_id = ? AND sc.is_active = TRUE
            )
            ORDER BY c.building, c.room_number
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, minCapacity);
            stmt.setInt(2, slotId);
            stmt.setInt(3, semesterId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                classrooms.add(extractClassroomFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return classrooms;
    }

    // Helper method to extract classroom from ResultSet
    private Classroom extractClassroomFromResultSet(ResultSet rs) throws SQLException {
        Classroom classroom = new Classroom();
        classroom.setRoomId(rs.getInt("room_id"));
        classroom.setRoomNumber(rs.getString("room_number"));
        classroom.setBuilding(rs.getString("building"));
        classroom.setCapacity(rs.getInt("capacity"));
        classroom.setRoomType(rs.getString("room_type"));
        classroom.setHasProjector(rs.getBoolean("has_projector"));
        classroom.setHasComputers(rs.getBoolean("has_computers"));
        classroom.setIsActive(rs.getBoolean("is_active"));
        return classroom;
    }
        // Add new classroom with smart ID reuse
    public boolean addClassroom(Classroom classroom) {
        // Get next available ID (reuse deleted IDs)
        int nextId = IDManager.getNextAvailableId("classrooms", "room_id");

        String sql = "INSERT INTO classrooms (room_id, room_number, building, capacity, room_type, has_projector, has_computers, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nextId);
            stmt.setString(2, classroom.getRoomNumber());
            stmt.setString(3, classroom.getBuilding());
            stmt.setInt(4, classroom.getCapacity());
            stmt.setString(5, classroom.getRoomType());
            stmt.setBoolean(6, classroom.getHasProjector());
            stmt.setBoolean(7, classroom.getHasComputers());
            stmt.setBoolean(8, true); // Always active when newly created

            boolean success = stmt.executeUpdate() > 0;

            // Fix auto-increment to avoid conflicts
            if (success) {
                IDManager.fixAutoIncrement("classrooms", "room_id");
            }

            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete classroom and fix IDs
    public boolean deleteClassroom(int roomId) {
        String sql = "DELETE FROM classrooms WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            boolean success = pstmt.executeUpdate() > 0;

            // Fix auto-increment after delete
            if (success) {
                IDManager.fixAutoIncrement("classrooms", "room_id");
            }

            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}