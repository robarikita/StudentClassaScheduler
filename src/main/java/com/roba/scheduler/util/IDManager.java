package com.roba.scheduler.util;

import java.sql.*;

public class IDManager {

    /**
     * Get next available ID for any table (reuses deleted IDs)
     * Finds gaps in the ID sequence and returns the smallest available ID
     */
    public static int getNextAvailableId(String tableName, String idColumn) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Method 1: Find the smallest missing ID
            String findGapSql = String.format("""
                SELECT MIN(%s + 1) AS next_id
                FROM %s t1
                WHERE NOT EXISTS (
                    SELECT 1 FROM %s t2
                    WHERE t2.%s = t1.%s + 1
                )
                AND %s + 1 > 0
                """, idColumn, tableName, tableName, idColumn, idColumn, idColumn);

            stmt = conn.createStatement();
            rs = stmt.executeQuery(findGapSql);

            int nextId = 1; // Default if table is empty

            if (rs.next()) {
                nextId = rs.getInt("next_id");
                if (rs.wasNull() || nextId == 0) {
                    // No gaps found, get max ID + 1
                    nextId = getMaxId(conn, tableName, idColumn) + 1;
                }
            }

            // If nextId is still 0 or negative, set to 1
            if (nextId <= 0) {
                nextId = 1;
            }

            conn.commit();
            return nextId;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return 1; // Fallback to ID 1
        } finally {
            // Close resources
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }} catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Get maximum ID in table
     */
    private static int getMaxId(Connection conn, String tableName, String idColumn) throws SQLException {
        String sql = String.format("SELECT COALESCE(MAX(%s), 0) as max_id FROM %s", idColumn, tableName);

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("max_id");
            }
            return 0;
        }
    }

    /**
     * Set auto-increment to next available ID + 1
     * This ensures MySQL's auto_increment doesn't conflict with our reused IDs
     */
    public static void fixAutoIncrement(String tableName, String idColumn) {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Get next available ID
            int nextId = getNextAvailableId(tableName, idColumn);

            // Set auto_increment to max(id) + 1 to avoid conflicts
            String getMaxSql = String.format("SELECT COALESCE(MAX(%s), 0) + 1 as next_auto FROM %s",
                                             idColumn, tableName);

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(getMaxSql);

            int nextAuto = 1;
            if (rs.next()) {
                nextAuto = rs.getInt("next_auto");
            }

            // Set auto_increment
            String alterSql = String.format("ALTER TABLE %s AUTO_INCREMENT = %d", tableName, nextAuto);
            stmt.execute(alterSql);

            System.out.printf("Table %s: Next available ID=%d, Auto_increment set to=%d%n",
                              tableName, nextId, nextAuto);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Compact IDs (reassign sequential IDs)
     * Use this when you want to completely reorganize IDs
     */
    public static void compactTableIds(String tableName, String idColumn) {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Create temporary table
            String tempTable = tableName + "_temp";
            String createTemp = String.format("CREATE TABLE %s LIKE %s", tempTable, tableName);
            String alterTemp = String.format("ALTER TABLE %s AUTO_INCREMENT = 1", tempTable);

            stmt = conn.createStatement();
            stmt.execute(createTemp);
            stmt.execute(alterTemp);

            // Copy data with new sequential IDs
            String copyData = String.format(
                "INSERT INTO %s SELECT NULL, t.* FROM (SELECT * FROM %s WHERE 1=2) t",
                tempTable, tableName);

            // We need to get all columns except the ID column
            String getColumns = String.format(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = '%s' AND COLUMN_NAME != '%s'",
                tableName, idColumn);

            ResultSet rs = stmt.executeQuery(getColumns);
            StringBuilder columns = new StringBuilder();
            while (rs.next()) {
                if (columns.length() > 0) columns.append(", ");
                columns.append(rs.getString("COLUMN_NAME"));
            }

            if (columns.length() > 0) {
                String insertData = String.format(
                    "INSERT INTO %s (%s) SELECT %s FROM %s ORDER BY %s",
                    tempTable, columns.toString(), columns.toString(), tableName, idColumn);

                stmt.execute(insertData);
            }

            // Replace original table
            String rename1 = String.format("RENAME TABLE %s TO %s_old", tableName, tableName);
            String rename2 = String.format("RENAME TABLE %s TO %s", tempTable, tableName);
            String dropOld = String.format("DROP TABLE %s_old", tableName);

            stmt.execute(rename1);
            stmt.execute(rename2);
            stmt.execute(dropOld);

            conn.commit();
            System.out.println("Compacted IDs for table: " + tableName);

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }} catch (SQLException e) { e.printStackTrace(); }
        }
    }
}