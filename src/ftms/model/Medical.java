package ftms.model;

import ftms.db.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Medical class representing student medical certificates and approvals.
 * Assigned to Member 3.
 *
 * Implements:
 * - addMedical()
 * - updateMedical()
 * - deleteMedical()
 * - approveMedical()
 * - viewMedicalDetails()
 */
public class Medical {
    private int medicalId;
    private int studentId;
    private Integer attendanceId; // nullable foreign key
    private LocalDate submissionDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String approvalStatus; // 'PENDING', 'APPROVED', 'REJECTED'
    private String status;         // optional descriptive status

    // Joined display fields
    private String tgNum;
    private String studentName;
    private String courseCode;
    private String sessionType;
    private LocalDate sessionDate;

    public Medical() {
        this.approvalStatus = "PENDING";
    }

    public Medical(int medicalId, int studentId, Integer attendanceId, LocalDate submissionDate,
                   LocalDate startDate, LocalDate endDate, String reason, String approvalStatus, String status) {
        this.medicalId = medicalId;
        this.studentId = studentId;
        this.attendanceId = attendanceId;
        this.submissionDate = submissionDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        setApprovalStatus(approvalStatus);
        this.status = status;
    }

    public Medical(int studentId, Integer attendanceId, LocalDate submissionDate,
                   LocalDate startDate, LocalDate endDate, String reason) {
        this(0, studentId, attendanceId, submissionDate, startDate, endDate, reason, "PENDING", "SUBMITTED");
    }

    // Encapsulation: Getters and Setters
    public int getMedicalId() {
        return medicalId;
    }

    public void setMedicalId(int medicalId) {
        this.medicalId = medicalId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        if (approvalStatus != null) {
            String upper = approvalStatus.toUpperCase().trim();
            if (upper.equals("PENDING") || upper.equals("APPROVED") || upper.equals("REJECTED")) {
                this.approvalStatus = upper;
            } else {
                throw new IllegalArgumentException("Approval status must be PENDING, APPROVED, or REJECTED");
            }
        } else {
            this.approvalStatus = "PENDING";
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTgNum() {
        return tgNum;
    }

    public void setTgNum(String tgNum) {
        this.tgNum = tgNum;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    // ==========================================
    // Assigned Methods for Member 3
    // ==========================================

    /**
     * Adds a medical record to the database.
     */
    public boolean addMedical() throws SQLException {
        if (studentId <= 0 || startDate == null || endDate == null) {
            throw new IllegalArgumentException("Student ID, Start Date, and End Date are mandatory.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be prior to start date.");
        }

        String sql = "INSERT INTO Medical (Student_ID, Attendance_ID, Submission_date, Start_date, End_date, Reason, Approval_status, Status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, this.studentId);
            if (this.attendanceId != null && this.attendanceId > 0) {
                stmt.setInt(2, this.attendanceId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setDate(3, this.submissionDate != null ? Date.valueOf(this.submissionDate) : Date.valueOf(LocalDate.now()));
            stmt.setDate(4, Date.valueOf(this.startDate));
            stmt.setDate(5, Date.valueOf(this.endDate));
            stmt.setString(6, this.reason);
            stmt.setString(7, this.approvalStatus != null ? this.approvalStatus : "PENDING");
            stmt.setString(8, this.status != null ? this.status : "ACTIVE");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.medicalId = rs.getInt(1);
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Updates an existing medical record.
     */
    public boolean updateMedical() throws SQLException {
        if (this.medicalId <= 0) {
            throw new IllegalArgumentException("Medical ID is required for update.");
        }
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be prior to start date.");
        }

        String sql = "UPDATE Medical SET Student_ID = ?, Attendance_ID = ?, Submission_date = ?, Start_date = ?, " +
                     "End_date = ?, Reason = ?, Approval_status = ?, Status = ? WHERE Medical_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.studentId);
            if (this.attendanceId != null && this.attendanceId > 0) {
                stmt.setInt(2, this.attendanceId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setDate(3, this.submissionDate != null ? Date.valueOf(this.submissionDate) : null);
            stmt.setDate(4, this.startDate != null ? Date.valueOf(this.startDate) : null);
            stmt.setDate(5, this.endDate != null ? Date.valueOf(this.endDate) : null);
            stmt.setString(6, this.reason);
            stmt.setString(7, this.approvalStatus);
            stmt.setString(8, this.status);
            stmt.setInt(9, this.medicalId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a medical record.
     */
    public boolean deleteMedical() throws SQLException {
        if (this.medicalId <= 0) {
            throw new IllegalArgumentException("Medical ID is required for deletion.");
        }

        String sql = "DELETE FROM Medical WHERE Medical_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.medicalId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Approves, rejects, or updates the approval status of a medical record.
     *
     * @param newStatus 'APPROVED', 'REJECTED', or 'PENDING'
     */
    public boolean approveMedical(String newStatus) throws SQLException {
        setApprovalStatus(newStatus);
        String sql = "UPDATE Medical SET Approval_status = ? WHERE Medical_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.approvalStatus);
            stmt.setInt(2, this.medicalId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Loads full medical details from database into this object.
     */
    public boolean viewMedicalDetails() throws SQLException {
        if (this.medicalId <= 0) {
            throw new IllegalArgumentException("Medical ID is required.");
        }

        String sql = "SELECT m.Medical_ID, m.Student_ID, m.Attendance_ID, m.Submission_date, m.Start_date, m.End_date, " +
                     "m.Reason, m.Approval_status, m.Status, s.TG_num, u.Fname, u.Lname, a.Course_code, a.Type, a.Session_date " +
                     "FROM Medical m " +
                     "JOIN Student s ON m.Student_ID = s.Student_ID " +
                     "JOIN User u ON s.Student_ID = u.User_ID " +
                     "LEFT JOIN Attendance a ON m.Attendance_ID = a.Attendance_ID " +
                     "WHERE m.Medical_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.medicalId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    this.studentId = rs.getInt("Student_ID");
                    int attId = rs.getInt("Attendance_ID");
                    this.attendanceId = rs.wasNull() ? null : attId;
                    Date sub = rs.getDate("Submission_date");
                    this.submissionDate = sub != null ? sub.toLocalDate() : null;
                    Date start = rs.getDate("Start_date");
                    this.startDate = start != null ? start.toLocalDate() : null;
                    Date end = rs.getDate("End_date");
                    this.endDate = end != null ? end.toLocalDate() : null;
                    this.reason = rs.getString("Reason");
                    this.approvalStatus = rs.getString("Approval_status");
                    this.status = rs.getString("Status");
                    this.tgNum = rs.getString("TG_num");
                    String fn = rs.getString("Fname");
                    String ln = rs.getString("Lname");
                    this.studentName = (fn != null ? fn : "") + " " + (ln != null ? ln : "").trim();
                    this.courseCode = rs.getString("Course_code");
                    this.sessionType = rs.getString("Type");
                    Date sessD = rs.getDate("Session_date");
                    this.sessionDate = sessD != null ? sessD.toLocalDate() : null;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Static helper to list all medical records, optionally filtered by studentId and approval status.
     */
    public static List<Medical> getAllMedicals(Integer studentId, String statusFilter) throws SQLException {
        List<Medical> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT m.Medical_ID, m.Student_ID, m.Attendance_ID, m.Submission_date, m.Start_date, m.End_date, ")
           .append("m.Reason, m.Approval_status, m.Status, s.TG_num, u.Fname, u.Lname, a.Course_code, a.Type, a.Session_date ")
           .append("FROM Medical m ")
           .append("JOIN Student s ON m.Student_ID = s.Student_ID ")
           .append("JOIN User u ON s.Student_ID = u.User_ID ")
           .append("LEFT JOIN Attendance a ON m.Attendance_ID = a.Attendance_ID ")
           .append("WHERE 1=1 ");

        if (studentId != null && studentId > 0) {
            sql.append("AND m.Student_ID = ? ");
        }
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !statusFilter.equalsIgnoreCase("ALL")) {
            sql.append("AND m.Approval_status = ? ");
        }
        sql.append("ORDER BY m.Submission_date DESC, m.Medical_ID DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (studentId != null && studentId > 0) {
                stmt.setInt(idx++, studentId);
            }
            if (statusFilter != null && !statusFilter.trim().isEmpty() && !statusFilter.equalsIgnoreCase("ALL")) {
                stmt.setString(idx++, statusFilter.toUpperCase().trim());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Medical m = new Medical();
                    m.setMedicalId(rs.getInt("Medical_ID"));
                    m.setStudentId(rs.getInt("Student_ID"));
                    int attId = rs.getInt("Attendance_ID");
                    m.setAttendanceId(rs.wasNull() ? null : attId);
                    Date sub = rs.getDate("Submission_date");
                    m.setSubmissionDate(sub != null ? sub.toLocalDate() : null);
                    Date start = rs.getDate("Start_date");
                    m.setStartDate(start != null ? start.toLocalDate() : null);
                    Date end = rs.getDate("End_date");
                    m.setEndDate(end != null ? end.toLocalDate() : null);
                    m.setReason(rs.getString("Reason"));
                    m.setApprovalStatus(rs.getString("Approval_status"));
                    m.setStatus(rs.getString("Status"));
                    m.setTgNum(rs.getString("TG_num"));
                    String fn = rs.getString("Fname");
                    String ln = rs.getString("Lname");
                    m.setStudentName((fn != null ? fn : "") + " " + (ln != null ? ln : "").trim());
                    m.setCourseCode(rs.getString("Course_code"));
                    m.setSessionType(rs.getString("Type"));
                    Date sessD = rs.getDate("Session_date");
                    m.setSessionDate(sessD != null ? sessD.toLocalDate() : null);

                    list.add(m);
                }
            }
        }
        return list;
    }
}
