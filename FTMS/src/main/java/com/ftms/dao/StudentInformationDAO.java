package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class StudentInformationDAO {
    public List<String[]> getTimetableByDepartment(String department) throws SQLException {
        List<String[]> rows = new ArrayList<>();
        if (department == null || department.trim().isEmpty()) return rows;
        String sql = "SELECT Course_code, Day, Time_slot FROM timetable " +
                "WHERE TRIM(Department) = ? ORDER BY FIELD(Day, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'), Time_slot, Course_code";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, department.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) rows.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3)});
            }
        }
        return rows;
    }

    public List<String[]> getNotices() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT Title, Content, Published_date FROM notice ORDER BY Published_date DESC, Notice_ID DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rows.add(new String[]{rs.getString(1), rs.getString(2),
                    rs.getDate(3) == null ? "" : rs.getDate(3).toString()});
        }
        return rows;
    }
}
