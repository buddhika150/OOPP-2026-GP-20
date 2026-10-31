package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import com.ftms.model.Attendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    // 1. Insert new attendance record (AUTO_INCREMENT)

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
            System.out.println("Session date is required.");
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Check duplicate attendance
            try (PreparedStatement check =
                         conn.prepareStatement(checkSql)) {

                check.setInt(1, a.getStudentId());
                check.setString(2, a.getCourseCode());
                check.setDate(3, Date.valueOf(a.getSessionDate()));
                check.setString(4, a.getType());

                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println(
                                "Attendance already recorded for this session."
                        );
                        return false;
                    }
                }
            }

            // Save attendance
            try (PreparedStatement ps =
                         conn.prepareStatement(
                                 insertSql,
                                 Statement.RETURN_GENERATED_KEYS)) {

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

    // 2. Get attendance by ID
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

    // 3. Get all attendance records
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

    // 4. Get attendance by Student_ID
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

    // 5. Get attendance by Course_code
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

    // 6. Update existing attendance record
    public boolean updateAttendance(Attendance a) {
        String sql = "UPDATE attendance SET Student_ID = ?, Course_code = ?, Session_date = ?, Type = ?, Is_Present = ? WHERE Attendance_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, a.getStudentId());
            ps.setString(2, a.getCourseCode());
            ps.setDate(3, a.getSessionDate() != null ? Date.valueOf(a.getSessionDate()) : null);
            ps.setString(4, a.getType());
            ps.setInt(5, a.isPresent() ? 1 : 0);
            ps.setInt(6, a.getAttendanceId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 7. Delete attendance record by ID
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

    // 8. Calculate attendance percentage for a student and course
    public double calculateAttendancePercentage(int studentId, String courseCode) {
        String sql = "SELECT COUNT(*) AS total, COALESCE(SUM(Is_Present), 0) AS attended " +
                "FROM attendance WHERE Student_ID = ? AND Course_code = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt("total") > 0) {
                    return Attendance.calculateAttendancePercentage(rs.getInt("attended"), rs.getInt("total"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Helper method to convert ResultSet row to Attendance object
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
