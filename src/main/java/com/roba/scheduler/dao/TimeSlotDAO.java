package com.roba.scheduler.dao;

import com.roba.scheduler.model.TimeSlot;
import com.roba.scheduler.util.DatabaseConnection;
import com.roba.scheduler.util.IDManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAO {


    // Get all time slots
    public List<TimeSlot> getAllTimeSlots() {
        List<TimeSlot> timeSlots = new ArrayList<>();
        // NO FILTER - shows all time slots
        String sql = "SELECT * FROM time_slots ORDER BY day_of_week, start_time";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TimeSlot slot = new TimeSlot();
                slot.setSlotId(rs.getInt("slot_id"));
                slot.setDayOfWeek(rs.getString("day_of_week"));
                slot.setStartTime(rs.getTime("start_time"));
                slot.setEndTime(rs.getTime("end_time"));
                slot.setSlotName(rs.getString("slot_name"));
                timeSlots.add(slot);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return timeSlots;
    }

    // Get time slot by ID
    public TimeSlot getTimeSlotById(int slotId) {
        String sql = "SELECT * FROM time_slots WHERE slot_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, slotId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                TimeSlot slot = new TimeSlot();
                slot.setSlotId(rs.getInt("slot_id"));
                slot.setDayOfWeek(rs.getString("day_of_week"));
                slot.setStartTime(rs.getTime("start_time"));
                slot.setEndTime(rs.getTime("end_time"));
                slot.setSlotName(rs.getString("slot_name"));
                return slot;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Get time slots by day
    public List<TimeSlot> getTimeSlotsByDay(String dayOfWeek) {
        List<TimeSlot> timeSlots = new ArrayList<>();
        String sql = "SELECT * FROM time_slots WHERE day_of_week = ? ORDER BY start_time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dayOfWeek);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TimeSlot slot = new TimeSlot();
                slot.setSlotId(rs.getInt("slot_id"));
                slot.setDayOfWeek(rs.getString("day_of_week"));
                slot.setStartTime(rs.getTime("start_time"));
                slot.setEndTime(rs.getTime("end_time"));
                slot.setSlotName(rs.getString("slot_name"));
                timeSlots.add(slot);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return timeSlots;
    }
       // Create new time slot with smart ID reuse
    public boolean addTimeSlot(TimeSlot timeSlot) {
        // Get next available ID (reuse deleted IDs)
        int nextId = IDManager.getNextAvailableId("time_slots", "slot_id");

        String sql = "INSERT INTO time_slots (slot_id, day_of_week, start_time, end_time, slot_name) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nextId);
            stmt.setString(2, timeSlot.getDayOfWeek());
            stmt.setTime(3, timeSlot.getStartTime());
            stmt.setTime(4, timeSlot.getEndTime());
            stmt.setString(5, timeSlot.getSlotName());

            boolean success = stmt.executeUpdate() > 0;

            // Fix auto-increment to avoid conflicts
            if (success) {
                IDManager.fixAutoIncrement("time_slots", "slot_id");
            }

            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete time slot and fix IDs
    public boolean deleteTimeSlot(int slotId) {
        String sql = "DELETE FROM time_slots WHERE slot_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, slotId);
            boolean success = stmt.executeUpdate() > 0;

            // Fix auto-increment after delete
            if (success) {
                IDManager.fixAutoIncrement("time_slots", "slot_id");
            }

            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}