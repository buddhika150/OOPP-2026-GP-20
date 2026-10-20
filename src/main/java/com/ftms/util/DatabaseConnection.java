package com.ftms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Shared Database Connection utility class for Faculty Tracking Management System (FTMS).
 * Encapsulates JDBC connection lifecycle and implements the Singleton pattern
 * to provide centralized, thread-safe database connections.
 *
 * Maps to schema: faculty_technology_management (db.sql)
 */
public class DatabaseConnection {

    private static String dbUrl = "jdbc:mysql://localhost:3306/faculty_technology_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String dbUser = "root";
    private static String dbPassword = ""; // Default local MySQL password

    private static Connection connection = null;
    private static boolean driverLoaded = false;

    private DatabaseConnection() {
        // Private constructor for singleton
    }

    /**
     * Attempts to register the MySQL JDBC Driver.
     */
    private static synchronized void loadDriver() {
        if (!driverLoaded) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    driverLoaded = true;
                } catch (ClassNotFoundException ex) {
                    System.err.println("[DatabaseConnection] MySQL JDBC Driver not found on classpath: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Allows dynamically updating connection parameters (e.g. for custom port, host, or credentials).
     */
    public static synchronized void configure(String url, String user, String password) {
        closeConnection();
        dbUrl = url;
        dbUser = user;
        dbPassword = password;
    }

    /**
     * Returns an active JDBC connection. If the existing connection is null or closed,
     * creates and returns a new connection.
     *
     * @return active java.sql.Connection
     * @throws SQLException if a database access error occurs or server is unreachable
     */
    public static synchronized Connection getConnection() throws SQLException {
        loadDriver();
        if (connection == null || connection.isClosed()) {
            DriverManager.setLoginTimeout(3); // 3 seconds timeout to prevent GUI hanging
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        }
        return connection;
    }

    /**
     * Tests if the database is currently reachable and can establish a valid connection.
     *
     * @return true if connection succeeds, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed() && conn.isValid(2);
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Closes the active singleton connection if open.
     */
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("[DatabaseConnection] Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static String getDbUser() {
        return dbUser;
    }
}
