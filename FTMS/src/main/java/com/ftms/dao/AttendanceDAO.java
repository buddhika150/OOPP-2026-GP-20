
package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import com.ftms.model.Attendance;
import com.ftms.model.AttendanceRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    // Add attendance with duplicate prevention
    public boolean addAttendance(Attendance a) {

        String checkSql =
                "SELECT COUNT(*) FROM attendance " +
                "WHERE Student_ID = ? AND Course_code = ? " +
                "AND Session_date = ? AND Type = ?";

        String insertSql =
                "INSERT INTO attendance " +
                "(Student_ID, Course_code, Session_date, Type, Is_Present) " +
                "VALUES (?, ?, ?, ?, ?)";

        if (a.getSessionDate() == null) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setInt(1, a.getStudentId());
                check.setString(2, a.getCourseCode());
                check.setDate(3, Date.valueOf(a.getSessionDate()));
                check.setString(4, a.getType());

                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Attendance already recorded.");
                        return false;
                    }
                }
            }

            try (PreparedStatement ps =
                         conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, a.getStudentId());
                ps.setString(2, a.getCourseCode());
                ps.setDate(3, Date.valueOf(a.getSessionDate()));
                ps.setString(4, a.getType());
                ps.setInt(5, a.isPresent() ? 1 : 0);

                if (ps.executeUpdate() > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            a.setAttendanceId(keys.getInt(1));
                        }
                    }
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get attendance by ID
    public Attendance getAttendanceById(int attendanceId) {
        String sql = "SELECT * FROM attendance WHERE Attendance_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, attendanceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all attendance records
    public List<Attendance> getAllAttendance() {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance ORDER BY Attendance_ID";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get attendance by student ID
    public List<Attendance> getAttendanceByStudentId(int studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE Student_ID = ? ORDER BY Session_date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get attendance by course
    public List<Attendance> getAttendanceByCourseCode(String courseCode) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE Course_code = ? ORDER BY Student_ID, Session_date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Update attendance
    public boolean updateAttendance(Attendance a) {
        String sql = "UPDATE attendance SET Student_ID = ?, Course_code = ?, " +
                "Session_date = ?, Type = ?, Is_Present = ? WHERE Attendance_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, a.getStudentId());
            ps.setString(2, a.getCourseCode());
            ps.setDate(3, a.getSessionDate() != null
                    ? Date.valueOf(a.getSessionDate()) : null);
            ps.setString(4, a.getType());
            ps.setInt(5, a.isPresent() ? 1 : 0);
            ps.setInt(6, a.getAttendanceId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete attendance
    public boolean deleteAttendance(int attendanceId) {
        String sql = "DELETE FROM attendance WHERE Attendance_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, attendanceId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Calculate attendance percentage
    public double calculateAttendancePercentage(int studentId, String courseCode) {
        String sql =
                "SELECT COUNT(*) AS total, COALESCE(SUM(Is_Present), 0) AS attended " +
                "FROM attendance WHERE Student_ID = ? AND Course_code = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, courseCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt("total") > 0) {
                    return Attendance.calculateAttendancePercentage(
                            rs.getInt("attended"),
                            rs.getInt("total")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Student dashboard attendance summary
    public List<AttendanceRecord> getAttendanceRecordsForStudent(int studentId) {

        List<AttendanceRecord> list = new ArrayList<>();

        String query =
                "SELECT c.Course_code, c.Course_name, " +
                "COUNT(a.Attendance_ID) AS Total_Sessions, " +
                "COALESCE(SUM(a.Is_Present), 0) AS Attended_Sessions, " +
                "COALESCE(ROUND(100.0 * SUM(a.Is_Present) / " +
                "NULLIF(COUNT(a.Attendance_ID), 0), 2), 0) AS Attendance_Percentage " +
                "FROM (SELECT DISTINCT Student_ID, Course_code FROM Enrollment) e " +
                "JOIN Course c ON e.Course_code = c.Course_code " +
                "LEFT JOIN Attendance a ON e.Student_ID = a.Student_ID " +
                "AND e.Course_code = a.Course_code " +
                "WHERE e.Student_ID = ? " +
                "GROUP BY c.Course_code, c.Course_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AttendanceRecord(
                            rs.getString("Course_code"),
                            rs.getString("Course_name"),
                            rs.getInt("Total_Sessions"),
                            rs.getInt("Attended_Sessions"),
                            rs.getDouble("Attendance_Percentage")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Convert database row to Attendance object
    private Attendance mapRow(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("Session_date");

        return new Attendance(
                rs.getInt("Attendance_ID"),
                rs.getInt("Student_ID"),
                rs.getString("Course_code"),
                sqlDate != null ? sqlDate.toLocalDate() : null,
                rs.getString("Type"),
                rs.getInt("Is_Present") == 1
        );
    }
}
