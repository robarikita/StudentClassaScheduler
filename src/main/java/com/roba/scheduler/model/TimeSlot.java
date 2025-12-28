package com.roba.scheduler.model;

import java.sql.Time;

public class TimeSlot {
    private int slotId;
    private String dayOfWeek;
    private Time startTime;
    private Time endTime;
    private String slotName;

    // Constructors
    public TimeSlot() {}

    public TimeSlot(String dayOfWeek, Time startTime, Time endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public int getSlotId() { return slotId; }
    public void setSlotId(int slotId) { this.slotId = slotId; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }

    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }

    public String getSlotName() { return slotName; }
    public void setSlotName(String slotName) { this.slotName = slotName; }

    // Helper method for display
    public String getDisplayTime() {
        return String.format("%s %s - %s",
            dayOfWeek.substring(0, 3),
            startTime.toString().substring(0, 5),
            endTime.toString().substring(0, 5));
    }

    public String getFullDisplay() {
        return String.format("%s: %s (%s - %s)",
            slotName, dayOfWeek,
            startTime.toString().substring(0, 5),
            endTime.toString().substring(0, 5));
    }
}