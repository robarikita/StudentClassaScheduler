package com.roba.scheduler.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // IMPORTANT: Update these with your MySQL credentials
    private static final String URL = "jdbc:mysql://localhost:3306/scheduler_db?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root"; // Change if different
    private static final String PASSWORD = "12345678"; // Your MySQL password here

    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load MySQL JDBC driver");
            e.printStackTrace();
            throw new RuntimeException("Failed to load MySQL JDBC driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connection established successfully.");
            return conn;
        } catch (SQLException e) {
            System.err.println("Failed to connect to database:");
            System.err.println("URL: " + URL);
            System.err.println("Username: " + USERNAME);
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    // Test connection
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Database connection test successful!");
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
        }
    }
}