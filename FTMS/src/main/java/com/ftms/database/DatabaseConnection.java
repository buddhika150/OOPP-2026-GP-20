package com.ftms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/faculty_technology_management";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private DatabaseConnection() {}

    // Return a new connection each time: DAOs close their own connection.
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new IllegalStateException("Cannot connect to the FTMS database", e);
        }
    }
}