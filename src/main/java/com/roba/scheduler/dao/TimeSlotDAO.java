package com.roba.scheduler.dao;

import com.roba.scheduler.model.TimeSlot;
import com.roba.scheduler.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAO {

    // Create new time slot
    public boolean addTimeSlot(TimeSlot timeSlot) {
        String sql = "INSERT INTO time_slots (day_of_week, start_time, end_time, slot_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, timeSlot.getDayOfWeek());
            stmt.setTime(2, timeSlot.getStartTime());
            stmt.setTime(3, timeSlot.getEndTime());
            stmt.setString(4, timeSlot.getSlotName());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all time slots
    public List<TimeSlot> getAllTimeSlots() {
        List<TimeSlot> timeSlots = new ArrayList<>();
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

    // Delete time slot
    public boolean deleteTimeSlot(int slotId) {
        String sql = "DELETE FROM time_slots WHERE slot_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, slotId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
}