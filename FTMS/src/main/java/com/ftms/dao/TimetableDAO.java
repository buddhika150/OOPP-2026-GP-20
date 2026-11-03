
package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {

    public List<Object[]> getAll() throws SQLException {
        List<Object[]> rows = new ArrayList<>();

        String sql = "SELECT * FROM timetable ORDER BY Timetable_ID DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("Timetable_ID"),
                        rs.getString("Course_code"),
                        rs.getString("Department"),
                        rs.getString("Day"),
                        rs.getString("Time_slot")
                });
            }
        }
        return rows;
    }

    public List<String> getCourses() throws SQLException {
        List<String> courses = new ArrayList<>();

        String sql = "SELECT Course_code FROM course ORDER BY Course_code";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                courses.add(rs.getString("Course_code"));
            }
        }
        return courses;
    }

    public boolean add(String course, String department,
                       String day, String time) throws SQLException {

        String sql = "INSERT INTO timetable " +
                "(Course_code, Department, Day, Time_slot) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, course);
            ps.setString(2, department);
            ps.setString(3, day);
            ps.setString(4, time);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(int id, String course, String department,
                          String day, String time) throws SQLException {

        String sql = "UPDATE timetable SET Course_code=?, " +
                "Department=?, Day=?, Time_slot=? " +
                "WHERE Timetable_ID=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, course);
            ps.setString(2, department);
            ps.setString(3, day);
            ps.setString(4, time);
            ps.setInt(5, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM timetable WHERE Timetable_ID=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
