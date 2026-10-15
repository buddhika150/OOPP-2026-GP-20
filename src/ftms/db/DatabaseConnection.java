package ftms.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Shared Database Connection utility class.
 * Follows the Singleton pattern to provide thread-safe database connections.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/faculty_technology_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Default local MySQL password, configurable

    private static Connection connection = null;
    private static boolean driverAttempted = false;

    private DatabaseConnection() {
        // Private constructor for singleton
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            DriverManager.setLoginTimeout(2);
            if (!driverAttempted) {
                driverAttempted = true;
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            }
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
