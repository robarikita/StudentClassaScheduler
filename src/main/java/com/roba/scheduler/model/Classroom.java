package com.roba.scheduler.model;

public class Classroom {
    private int roomId;
    private String roomNumber;
    private String building;
    private int capacity;
    private String roomType;
    private boolean hasProjector;
    private boolean hasComputers;
    private boolean isActive;

    // Constructors
    public Classroom() {}

    public Classroom(String roomNumber, String building, int capacity) {
        this.roomNumber = roomNumber;
        this.building = building;
        this.capacity = capacity;
    }

    // Getters and Setters
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public boolean getHasProjector() { return hasProjector; }
    public void setHasProjector(boolean hasProjector) { this.hasProjector = hasProjector; }

    public boolean getHasComputers() { return hasComputers; }
    public void setHasComputers(boolean hasComputers) { this.hasComputers = hasComputers; }

    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }

    // Helper methods
    public String getFullRoomName() {
        return building + " " + roomNumber;
    }

    public String getRoomInfo() {
        return String.format("%s %s (%s, Capacity: %d)",
            building, roomNumber, roomType, capacity);
    }
}