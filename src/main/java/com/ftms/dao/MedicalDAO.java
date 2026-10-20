package com.ftms.dao;

import com.ftms.model.Medical;
import com.ftms.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing Medical certificate records in the database.
 * Encapsulates all SQL operations for the Medical table in db.sql:
 * Medical(Medical_ID, Student_ID, Attendance_ID, Submission_date, Start_date, End_date, Reason, Approval_status, Status).
 *
 * Demonstrates:
 * - DAO Pattern & Encapsulation: Database operations isolated cleanly from UI and domain models.
 * - Resource Management: Try-with-resources ensuring PreparedStatement, ResultSet, and Connection are closed.
 * - Nullable Foreign Key Handling: Correctly handles optional Attendance_ID via Types.INTEGER when null.
 * - Defensive Parameterization: All SQL queries parameterized via PreparedStatement to prevent injection.
 * - Workflow Validation: Validates state transitions to APPROVED or REJECTED per database ENUM constraints.
 */
public class MedicalDAO {

    private Connection connection;

    /**
     * Default constructor. Uses DatabaseConnection.getConnection() for each operation.
     */
    public MedicalDAO() {
    }

    /**
     * Constructor allowing connection injection for external transaction control or testing.
     *
     * @param connection active JDBC Connection
     */
    public MedicalDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Obtains a database connection from the injected connection or the centralized DatabaseConnection utility.
     */
    protected Connection getConnection() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            return this.connection;
        }
        return DatabaseConnection.getConnection();
    }

    // =========================================================================
    // INSERT OPERATIONS
    // =========================================================================

    /**
     * Inserts a new Medical record into the database.
     * Properly handles optional Attendance_ID (foreign key to Attendance).
     *
     * @param medical the Medical model to persist
     * @return true if record was inserted successfully
     * @throws SQLException if a database error occurs or constraints are violated
     */
    public boolean insertMedical(Medical medical) throws SQLException {
        if (medical == null) {
            throw new IllegalArgumentException("Medical record cannot be null.");
        }

        String sql = "INSERT INTO Medical (Student_ID, Attendance_ID, Submission_date, Start_date, End_date, Reason, Approval_status, Status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, medical.getStudentId());

            // Handle optional Attendance_ID (nullable foreign key)
            if (medical.getAttendanceId() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, medical.getAttendanceId());
            }

            ps.setDate(3, medical.getSubmissionDateAsSqlDate());
            ps.setDate(4, medical.getStartDateAsSqlDate());
            ps.setDate(5, medical.getEndDateAsSqlDate());
            ps.setString(6, medical.getReason());
            ps.setString(7, medical.getApprovalStatus());
            ps.setString(8, medical.getStatus());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        medical.setMedicalId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // =========================================================================
    // RETRIEVAL OPERATIONS
    // =========================================================================

    /**
     * Retrieves a Medical record by its primary key Medical_ID.
     *
     * @param medicalId primary key ID
     * @return Medical instance if found, or null
     * @throws SQLException if database error occurs
     */
    public Medical getMedicalById(int medicalId) throws SQLException {
        String sql = "SELECT Medical_ID, Student_ID, Attendance_ID, Submission_date, Start_date, End_date, Reason, Approval_status, Status " +
                     "FROM Medical WHERE Medical_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, medicalId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedical(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all medical records ordered chronologically by submission date.
     */
    public List<Medical> getAllMedicals() throws SQLException {
        String sql = "SELECT Medical_ID, Student_ID, Attendance_ID, Submission_date, Start_date, End_date, Reason, Approval_status, Status " +
                     "FROM Medical ORDER BY Submission_date DESC, Medical_ID DESC";
        List<Medical> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToMedical(rs));
            }
        }
        return list;
    }

    /**
     * Retrieves all medical records submitted by a specific student.
     *
     * @param studentId foreign key Student_ID
     */
    public List<Medical> getMedicalsByStudent(int studentId) throws SQLException {
        String sql = "SELECT Medical_ID, Student_ID, Attendance_ID, Submission_date, Start_date, End_date, Reason, Approval_status, Status " +
                     "FROM Medical WHERE Student_ID = ? ORDER BY Submission_date DESC";
        List<Medical> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMedical(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves medical records linked to a specific Attendance session.
     *
     * @param attendanceId foreign key Attendance_ID
     */
    public List<Medical> getMedicalsByAttendanceId(int attendanceId) throws SQLException {
        String sql = "SELECT Medical_ID, Student_ID, Attendance_ID, Submission_date, Start_date, End_date, Reason, Approval_status, Status " +
                     "FROM Medical WHERE Attendance_ID = ? ORDER BY Submission_date DESC";
        List<Medical> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attendanceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMedical(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves medical records that overlap with a specific date range.
     */
    public List<Medical> getMedicalsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and End date cannot be null.");
        }
        String sql = "SELECT Medical_ID, Student_ID, Attendance_ID, Submission_date, Start_date, End_date, Reason, Approval_status, Status " +
                     "FROM Medical WHERE Start_date <= ? AND End_date >= ? ORDER BY Start_date ASC";
        List<Medical> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(endDate));
            ps.setDate(2, Date.valueOf(startDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMedical(rs));
                }
            }
        }
        return list;
    }

    // =========================================================================
    // RETRIEVAL BY APPROVAL STATUS (PENDING, APPROVED, REJECTED)
    // =========================================================================

    /**
     * Retrieves all pending medical submissions awaiting review.
     */
    public List<Medical> getPendingMedicals() throws SQLException {
        return getMedicalsByApprovalStatus(Medical.STATUS_PENDING);
    }

    /**
     * Retrieves all approved medical certificates.
     */
    public List<Medical> getApprovedMedicals() throws SQLException {
        return getMedicalsByApprovalStatus(Medical.STATUS_APPROVED);
    }

    /**
     * Retrieves all rejected medical submissions.
     */
    public List<Medical> getRejectedMedicals() throws SQLException {
        return getMedicalsByApprovalStatus(Medical.STATUS_REJECTED);
    }

    /**
     * Retrieves medical records matching an explicit approval status ENUM value
     * ('PENDING', 'APPROVED', 'REJECTED').
     *
     * @param approvalStatus status filter
     */
    public List<Medical> getMedicalsByApprovalStatus(String approvalStatus) throws SQLException {
        if (approvalStatus == null) {
            throw new IllegalArgumentException("Approval status cannot be null.");
        }
        String upperStatus = approvalStatus.trim().toUpperCase();
        if (!upperStatus.equals(Medical.STATUS_PENDING) &&
            !upperStatus.equals(Medical.STATUS_APPROVED) &&
            !upperStatus.equals(Medical.STATUS_REJECTED)) {
            throw new IllegalArgumentException("Invalid approval status: " + approvalStatus +
                    ". Expected: PENDING, APPROVED, or REJECTED.");
        }

        String sql = "SELECT Medical_ID, Student_ID, Attendance_ID, Submission_date, Start_date, End_date, Reason, Approval_status, Status " +
                     "FROM Medical WHERE Approval_status = ? ORDER BY Submission_date ASC, Medical_ID ASC";
        List<Medical> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, upperStatus);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMedical(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves medical records for a specific student filtered by approval status.
     */
    public List<Medical> getMedicalsByStudentAndStatus(int studentId, String approvalStatus) throws SQLException {
        if (approvalStatus == null) {
            throw new IllegalArgumentException("Approval status cannot be null.");
        }
        String upperStatus = approvalStatus.trim().toUpperCase();

        String sql = "SELECT Medical_ID, Student_ID, Attendance_ID, Submission_date, Start_date, End_date, Reason, Approval_status, Status " +
                     "FROM Medical WHERE Student_ID = ? AND Approval_status = ? ORDER BY Submission_date DESC";
        List<Medical> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, upperStatus);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMedical(rs));
                }
            }
        }
        return list;
    }

    // =========================================================================
    // STATUS UPDATE OPERATIONS (APPROVED / REJECTED)
    // =========================================================================

    /**
     * Updates the approval status of a Medical record to APPROVED or REJECTED.
     *
     * @param medicalId      primary key Medical_ID
     * @param approvalStatus new status: 'APPROVED' or 'REJECTED'
     * @return true if updated successfully
     * @throws SQLException if a database error occurs
     */
    public boolean updateApprovalStatus(int medicalId, String approvalStatus) throws SQLException {
        if (approvalStatus == null) {
            throw new IllegalArgumentException("Approval status cannot be null.");
        }
        String upperStatus = approvalStatus.trim().toUpperCase();
        if (!upperStatus.equals(Medical.STATUS_APPROVED) && !upperStatus.equals(Medical.STATUS_REJECTED)) {
            throw new IllegalArgumentException("Invalid status update: '" + approvalStatus +
                    "'. Allowed values: APPROVED, REJECTED.");
        }

        String sql = "UPDATE Medical SET Approval_status = ?, Status = ? WHERE Medical_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, upperStatus);
            ps.setString(2, "PROCESSED_" + upperStatus);
            ps.setInt(3, medicalId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Convenience method to approve a pending medical submission.
     *
     * @param medicalId primary key Medical_ID
     * @return true if updated
     * @throws SQLException on database error
     */
    public boolean approveMedical(int medicalId) throws SQLException {
        return updateApprovalStatus(medicalId, Medical.STATUS_APPROVED);
    }

    /**
     * Convenience method to reject a medical submission.
     *
     * @param medicalId primary key Medical_ID
     * @return true if updated
     * @throws SQLException on database error
     */
    public boolean rejectMedical(int medicalId) throws SQLException {
        return updateApprovalStatus(medicalId, Medical.STATUS_REJECTED);
    }

    // =========================================================================
    // DELETE OPERATION
    // =========================================================================

    /**
     * Deletes a Medical record by primary key.
     */
    public boolean deleteMedical(int medicalId) throws SQLException {
        String sql = "DELETE FROM Medical WHERE Medical_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, medicalId);
            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // HELPER MAPPING METHOD
    // =========================================================================

    private Medical mapResultSetToMedical(ResultSet rs) throws SQLException {
        int medicalId = rs.getInt("Medical_ID");
        int studentId = rs.getInt("Student_ID");

        // Nullable foreign key: Attendance_ID
        int rawAttendanceId = rs.getInt("Attendance_ID");
        Integer attendanceId = rs.wasNull() ? null : rawAttendanceId;

        Date subDate = rs.getDate("Submission_date");
        Date startDate = rs.getDate("Start_date");
        Date endDate = rs.getDate("End_date");
        String reason = rs.getString("Reason");
        String approvalStatus = rs.getString("Approval_status");
        String status = rs.getString("Status");

        LocalDate submissionLocalDate = (subDate != null) ? subDate.toLocalDate() : null;
        LocalDate startLocalDate = (startDate != null) ? startDate.toLocalDate() : null;
        LocalDate endLocalDate = (endDate != null) ? endDate.toLocalDate() : null;

        return new Medical(medicalId, studentId, attendanceId, submissionLocalDate,
                startLocalDate, endLocalDate, reason, approvalStatus, status);
    }
}
