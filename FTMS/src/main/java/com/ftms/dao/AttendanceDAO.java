package com.ftms.dao;

import com.ftms.model.AttendanceRecord;
import com.ftms.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public List<AttendanceRecord> getAttendanceRecordsForStudent(int studentId) {
        List<AttendanceRecord> list = new ArrayList<>();
        String query = "SELECT c.Course_code, c.Course_name, " +
                "COUNT(a.Attendance_ID) AS Total_Sessions, " +
                "COALESCE(SUM(a.Is_Present), 0) AS Attended_Sessions, " +
                "COALESCE(ROUND(100.0 * SUM(a.Is_Present) / NULLIF(COUNT(a.Attendance_ID), 0), 2), 0) AS Attendance_Percentage " +
                "FROM (SELECT DISTINCT Student_ID, Course_code FROM Enrollment) e " +
                "JOIN Course c ON e.Course_code = c.Course_code " +
                "LEFT JOIN Attendance a ON e.Student_ID = a.Student_ID AND e.Course_code = a.Course_code " +
                "WHERE e.Student_ID = ? " +
                "GROUP BY c.Course_code, c.Course_name";

        // Note: DBConnection class එක ඔබගේ ප්‍රොජෙක්ට් එකේ ඇති ආකාරයට වෙනස් කරගන්න
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String code = rs.getString("Course_code");
                String name = rs.getString("Course_name");
                int total = rs.getInt("Total_Sessions");
                int attended = rs.getInt("Attended_Sessions");
                double percentage = rs.getDouble("Attendance_Percentage");

                list.add(new AttendanceRecord(code, name, total, attended, percentage));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}