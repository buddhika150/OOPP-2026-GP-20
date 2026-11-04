
package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import com.ftms.model.Attendance;
import com.ftms.model.TechnicalOfficer;
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


    // 9. Load Technical Officer full profile from user + technical_officer tables
    public TechnicalOfficer getTechnicalOfficerProfile(int userId, String username) {
        String sql = "SELECT u.User_ID, u.Username, u.Password, u.Email, u.Role, u.Fname, u.Lname, u.Phone, u.Profile_pic, t.Department " +
                "FROM user u LEFT JOIN technical_officer t ON u.User_ID = t.TO_ID " +
                "WHERE (u.User_ID = ? OR u.Username = ?) AND u.Role = 'TECHNICAL_OFFICER' LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, username != null ? username : "");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TechnicalOfficer(
                            rs.getInt("User_ID"),
                            rs.getString("Username"),
                            rs.getString("Password"),
                            rs.getString("Email"),
                            rs.getString("Role"),
                            rs.getString("Fname"),
                            rs.getString("Lname"),
                            rs.getString("Phone"),
                            rs.getString("Profile_pic"),
                            rs.getString("Department")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 10. Update Technical Officer profile details in user + technical_officer tables
    public boolean updateTechnicalOfficerProfile(TechnicalOfficer to) {
        String updateUserSql = "UPDATE user SET Email = ?, Fname = ?, Lname = ?, Phone = ?, Profile_pic = ? WHERE User_ID = ?";
        String updateToSql = "UPDATE technical_officer SET Department = ? WHERE TO_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement psUser = conn.prepareStatement(updateUserSql)) {
                psUser.setString(1, to.getEmail());
                psUser.setString(2, to.getFname());
                psUser.setString(3, to.getLname());
                psUser.setString(4, to.getPhone());
                psUser.setString(5, to.getProfilePic());
                psUser.setInt(6, to.getToId());
                psUser.executeUpdate();
            }

            if (to.getDepartment() != null) {
                try (PreparedStatement psTo = conn.prepareStatement(updateToSql)) {
                    psTo.setString(1, to.getDepartment());
                    psTo.setInt(2, to.getToId());
                    psTo.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to convert ResultSet row to Attendance object

    // Student dashboard attendance summary
    public List<AttendanceRecord> getAttendanceRecordsForStudent(int studentId) {

        List<AttendanceRecord> list = new ArrayList<>();

        String query =
                "SELECT c.Course_code, c.Course_name, " +
                        "COUNT(a.Attendance_ID) AS Total_Sessions, " +
                        "SUM(CASE WHEN UPPER(a.Type) = 'THEORY' THEN 1 ELSE 0 END) AS Theory_Total, " +
                        "SUM(CASE WHEN UPPER(a.Type) = 'THEORY' AND a.Is_Present = 1 THEN 1 ELSE 0 END) AS Theory_Attended, " +
                        "SUM(CASE WHEN UPPER(a.Type) = 'PRACTICAL' THEN 1 ELSE 0 END) AS Practical_Total, " +
                        "SUM(CASE WHEN UPPER(a.Type) = 'PRACTICAL' AND a.Is_Present = 1 THEN 1 ELSE 0 END) AS Practical_Attended, " +
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
                            rs.getDouble("Attendance_Percentage"),
                            rs.getInt("Theory_Total"),
                            rs.getInt("Theory_Attended"),
                            rs.getInt("Practical_Total"),
                            rs.getInt("Practical_Attended")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Attendance adjusted with approved medical credits for eligibility only.
    // Actual attendance methods remain unchanged.
    public List<AttendanceRecord> getMedicalAdjustedAttendanceForStudent(int studentId) {
        List<AttendanceRecord> list = new ArrayList<>();
        String sql =
                "SELECT c.Course_code, c.Course_name, " +
                        "COUNT(a.Attendance_ID) AS Total_Sessions, " +
                        "COALESCE(SUM(CASE WHEN a.Is_Present = 1 THEN 1 ELSE 0 END), 0) AS Attended_Sessions, " +
                        "COALESCE(SUM(CASE WHEN UPPER(TRIM(a.Type)) = 'THEORY' THEN 1 ELSE 0 END), 0) AS Theory_Total, " +
                        "COALESCE(SUM(CASE WHEN UPPER(TRIM(a.Type)) = 'PRACTICAL' THEN 1 ELSE 0 END), 0) AS Practical_Total, " +
                        "COALESCE(SUM(CASE WHEN UPPER(TRIM(a.Type)) = 'THEORY' AND " +
                        "(a.Is_Present = 1 OR EXISTS (SELECT 1 FROM medical m " +
                        "WHERE m.Student_ID = a.Student_ID AND a.Session_date BETWEEN m.Start_date AND m.End_date " +
                        "AND UPPER(TRIM(m.Approval_status)) = 'APPROVED')) THEN 1 ELSE 0 END), 0) AS Theory_Effective, " +
                        "COALESCE(SUM(CASE WHEN UPPER(TRIM(a.Type)) = 'PRACTICAL' AND " +
                        "(a.Is_Present = 1 OR EXISTS (SELECT 1 FROM medical m " +
                        "WHERE m.Student_ID = a.Student_ID AND a.Session_date BETWEEN m.Start_date AND m.End_date " +
                        "AND UPPER(TRIM(m.Approval_status)) = 'APPROVED')) THEN 1 ELSE 0 END), 0) AS Practical_Effective " +
                        "FROM (SELECT DISTINCT Student_ID, Course_code FROM Enrollment) e " +
                        "JOIN Course c ON e.Course_code = c.Course_code " +
                        "LEFT JOIN attendance a ON a.Student_ID = e.Student_ID AND a.Course_code = e.Course_code " +
                        "WHERE e.Student_ID = ? GROUP BY c.Course_code, c.Course_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int total = rs.getInt("Total_Sessions");
                    int attended = rs.getInt("Attended_Sessions");
                    list.add(new AttendanceRecord(
                            rs.getString("Course_code"), rs.getString("Course_name"),
                            total, attended, total == 0 ? 0.0 : 100.0 * attended / total,
                            rs.getInt("Theory_Total"), rs.getInt("Theory_Effective"),
                            rs.getInt("Practical_Total"), rs.getInt("Practical_Effective")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load medical-adjusted attendance", e);
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
