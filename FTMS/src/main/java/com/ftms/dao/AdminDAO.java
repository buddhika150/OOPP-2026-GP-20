package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    // Count all users
    public int getTotalUsers() {

        String sql = "SELECT COUNT(*) FROM user";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Count all courses
    public int getTotalCourses() {

        String sql = "SELECT COUNT(*) FROM course";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    public int getTotalNotices() throws SQLException {
        return getCount("SELECT COUNT(*) FROM notice");
    }

    public int getTotalTimetables() throws SQLException {
        return getCount("SELECT COUNT(*) FROM timetable");
    }
    private int getCount(String sql) throws SQLException {

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }
    }
}
