package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import com.ftms.model.Medical;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    /**
     * Resolves the student's absence date.
     * If linked to an Attendance record, checks attendance session date.
     * Otherwise defaults to medical start date.
     */
    public LocalDate getAbsenceDate(Medical m) {
        if (m == null) {
            return null;
        }
        if (m.getAttendanceId() != null && m.getAttendanceId() > 0) {
            String sql = "SELECT Session_date FROM attendance WHERE Attendance_ID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, m.getAttendanceId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Date d = rs.getDate("Session_date");
                        if (d != null) {
                            return d.toLocalDate();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return m.getStartDate();
    }

    /**
     * Strictly verifies whether the medical submission is within 14 days of the absence date.
     */
    public boolean isWithin14Days(Medical m) {
        if (m == null) {
            return false;
        }
        LocalDate absentDate = getAbsenceDate(m);
        LocalDate subDate = m.getSubmissionDate() != null ? m.getSubmissionDate() : LocalDate.now();
        return Medical.isWithin14Days(absentDate, subDate);
    }

    // 1. Insert new medical record (AUTO_INCREMENT) including medicalimg with 14-day rule enforced
    public boolean addMedical(Medical m) {
        LocalDate absentDate = getAbsenceDate(m);
        LocalDate subDate = m.getSubmissionDate() != null ? m.getSubmissionDate() : LocalDate.now();

        // Enforce 14-day business rule at the DAO layer
        Medical.validateSubmissionDeadline(absentDate, subDate);

        String sql = "INSERT INTO medical (Student_ID, Attendance_ID, Submission_date, Start_date, End_date, Reason, Approval_status, Status, medicalimg) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, m.getStudentId());
            ps.setObject(2, m.getAttendanceId(), Types.INTEGER);
            ps.setDate(3, subDate != null ? Date.valueOf(subDate) : null);
            ps.setDate(4, m.getStartDate() != null ? Date.valueOf(m.getStartDate()) : null);
            ps.setDate(5, m.getEndDate() != null ? Date.valueOf(m.getEndDate()) : null);
            ps.setString(6, m.getReason());
            ps.setString(7, m.getApprovalStatus());
            ps.setString(8, m.getStatus());
            ps.setString(9, m.getMedicalImg());

            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        m.setMedicalId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Get medical record by ID
    public Medical getMedicalById(int medicalId) {
        String sql = "SELECT * FROM medical WHERE Medical_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, medicalId);
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

    // 3. Get all medical records
    public List<Medical> getAllMedicals() {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT * FROM medical ORDER BY Medical_ID";

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

    // 4. Get medical records by Student_ID
    public List<Medical> getMedicalsByStudentId(int studentId) {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT * FROM medical WHERE Student_ID = ? ORDER BY Medical_ID";

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

    // 5. Get medical records by Approval_status (e.g. PENDING, APPROVED, REJECTED)
    public List<Medical> getMedicalsByApprovalStatus(String approvalStatus) {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT * FROM medical WHERE Approval_status = ? ORDER BY Medical_ID";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, approvalStatus);
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

    // 6. Update full medical record including medicalimg with 14-day rule enforced
    public boolean updateMedical(Medical m) {
        LocalDate absentDate = getAbsenceDate(m);
        LocalDate subDate = m.getSubmissionDate() != null ? m.getSubmissionDate() : LocalDate.now();

        // Enforce 14-day business rule at the DAO layer
        Medical.validateSubmissionDeadline(absentDate, subDate);

        String sql = "UPDATE medical SET Student_ID = ?, Attendance_ID = ?, Submission_date = ?, " +
                "Start_date = ?, End_date = ?, Reason = ?, Approval_status = ?, Status = ?, medicalimg = ? " +
                "WHERE Medical_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, m.getStudentId());
            ps.setObject(2, m.getAttendanceId(), Types.INTEGER);
            ps.setDate(3, subDate != null ? Date.valueOf(subDate) : null);
            ps.setDate(4, m.getStartDate() != null ? Date.valueOf(m.getStartDate()) : null);
            ps.setDate(5, m.getEndDate() != null ? Date.valueOf(m.getEndDate()) : null);
            ps.setString(6, m.getReason());
            ps.setString(7, m.getApprovalStatus());
            ps.setString(8, m.getStatus());
            ps.setString(9, m.getMedicalImg());
            ps.setInt(10, m.getMedicalId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 7. Update only Approval_status (PENDING, APPROVED, REJECTED)
    public boolean updateApprovalStatus(int medicalId, String approvalStatus) {
        String sql = "UPDATE medical SET Approval_status = ? WHERE Medical_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, approvalStatus.trim().toUpperCase());
            ps.setInt(2, medicalId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean approveMedical(int medicalId) {
        return updateApprovalStatus(medicalId, Medical.APPROVAL_APPROVED);
    }

    public boolean rejectMedical(int medicalId) {
        return updateApprovalStatus(medicalId, Medical.APPROVAL_REJECTED);
    }

    // 8. Delete medical record by ID
    public boolean deleteMedical(int medicalId) {
        String sql = "DELETE FROM medical WHERE Medical_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, medicalId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Student deletion: ownership and pending approval are enforced in SQL.
    // Staff's existing deleteMedical(int) remains unchanged.
    public boolean deletePendingMedicalForStudent(int medicalId, int studentId) {
        String sql = "DELETE FROM medical WHERE Medical_ID = ? AND Student_ID = ? " +
                "AND UPPER(Approval_status) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, medicalId);
            ps.setInt(2, studentId);
            ps.setString(3, Medical.APPROVAL_PENDING);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to convert ResultSet row to Medical object
    private Medical mapRow(ResultSet rs) throws SQLException {
        int rawAttId = rs.getInt("Attendance_ID");
        Integer attendanceId = rs.wasNull() ? null : rawAttId;

        Date subDate = rs.getDate("Submission_date");
        Date startDate = rs.getDate("Start_date");
        Date endDate = rs.getDate("End_date");

        return new Medical(
                rs.getInt("Medical_ID"),
                rs.getInt("Student_ID"),
                attendanceId,
                subDate != null ? subDate.toLocalDate() : null,
                startDate != null ? startDate.toLocalDate() : null,
                endDate != null ? endDate.toLocalDate() : null,
                rs.getString("Reason"),
                rs.getString("Approval_status"),
                rs.getString("Status"),
                rs.getString("medicalimg")
        );
    }
}
