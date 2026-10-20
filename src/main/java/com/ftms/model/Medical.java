package com.ftms.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Model class representing a Medical certificate submission record.
 * Maps directly to the Medical table in db.sql:
 * Medical(Medical_ID, Student_ID, Attendance_ID, Submission_date, Start_date,
 * End_date, Reason, Approval_status, Status).
 *
 * Demonstrates:
 * - Classes and Objects: Models medical submission state and lifecycle.
 * - Encapsulation: Private member attributes with validated getters and
 * setters.
 * - Database Compatibility: Matches exact column names, types, and ENUM values
 * (PENDING, APPROVED, REJECTED).
 */
public class Medical {

    // Database ENUM constants for Medical Approval Status
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

    public enum MedicalApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    private int medicalId;
    private int studentId;
    private Integer attendanceId; // Nullable in database: Attendance_ID INT, FOREIGN KEY ... ON DELETE SET NULL
    private LocalDate submissionDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String approvalStatus = STATUS_PENDING;
    private String status = "RECORDED";

    /**
     * Default constructor.
     */
    public Medical() {
    }

    /**
     * Constructor for creating a new medical record submission before insertion.
     *
     * @param studentId      foreign key Student_ID
     * @param attendanceId   foreign key Attendance_ID (nullable)
     * @param submissionDate date of submission
     * @param startDate      start date of medical leave
     * @param endDate        end date of medical leave
     * @param reason         explanation/medical reason
     */
    public Medical(int studentId, Integer attendanceId, LocalDate submissionDate, LocalDate startDate,
            LocalDate endDate, String reason) {
        setStudentId(studentId);
        setAttendanceId(attendanceId);
        setSubmissionDate(submissionDate);
        setDateRange(startDate, endDate);
        setReason(reason);
        this.approvalStatus = STATUS_PENDING;
        this.status = "RECORDED";
    }

    /**
     * Full constructor for loading an existing medical record from the database.
     *
     * @param medicalId      primary key Medical_ID
     * @param studentId      foreign key Student_ID
     * @param attendanceId   foreign key Attendance_ID (nullable)
     * @param submissionDate date of submission
     * @param startDate      start date of leave
     * @param endDate        end date of leave
     * @param reason         medical reason
     * @param approvalStatus approval status ENUM('PENDING','APPROVED','REJECTED')
     * @param status         record status VARCHAR(30)
     */
    public Medical(int medicalId, int studentId, Integer attendanceId, LocalDate submissionDate,
            LocalDate startDate, LocalDate endDate, String reason, String approvalStatus, String status) {
        this.medicalId = medicalId;
        setStudentId(studentId);
        setAttendanceId(attendanceId);
        setSubmissionDate(submissionDate);
        setDateRange(startDate, endDate);
        setReason(reason);
        setApprovalStatus(approvalStatus);
        setStatus(status);
    }

    // ================= Getters and Setters (Encapsulation) =================

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
        if (studentId <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive integer.");
        }
        this.studentId = studentId;
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        if (attendanceId != null && attendanceId <= 0) {
            throw new IllegalArgumentException("Attendance ID must be a positive integer if provided.");
        }
        this.attendanceId = attendanceId;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        if (submissionDate == null) {
            throw new IllegalArgumentException("Submission date cannot be null.");
        }
        this.submissionDate = submissionDate;
    }

    public void setSubmissionDate(java.sql.Date sqlDate) {
        this.submissionDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;
    }

    public java.sql.Date getSubmissionDateAsSqlDate() {
        return (this.submissionDate != null) ? java.sql.Date.valueOf(this.submissionDate) : null;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null.");
        }
        if (this.endDate != null && startDate.isAfter(this.endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        this.startDate = startDate;
    }

    public void setStartDate(java.sql.Date sqlDate) {
        this.startDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;
    }

    public java.sql.Date getStartDateAsSqlDate() {
        return (this.startDate != null) ? java.sql.Date.valueOf(this.startDate) : null;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null.");
        }
        if (this.startDate != null && endDate.isBefore(this.startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
        this.endDate = endDate;
    }

    public void setEndDate(java.sql.Date sqlDate) {
        this.endDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;
    }

    public java.sql.Date getEndDateAsSqlDate() {
        return (this.endDate != null) ? java.sql.Date.valueOf(this.endDate) : null;
    }

    public void setDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and End date cannot be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date: " + startDate + " > " + endDate);
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = (reason != null) ? reason.trim() : null;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        if (approvalStatus == null) {
            throw new IllegalArgumentException("Approval status cannot be null.");
        }
        String upperStatus = approvalStatus.trim().toUpperCase();
        if (!upperStatus.equals(STATUS_PENDING) &&
                !upperStatus.equals(STATUS_APPROVED) &&
                !upperStatus.equals(STATUS_REJECTED)) {
            throw new IllegalArgumentException(
                    "Invalid approval status: '" + approvalStatus + "'. Allowed ENUM values: " +
                            STATUS_PENDING + ", " + STATUS_APPROVED + ", " + STATUS_REJECTED);
        }
        this.approvalStatus = upperStatus;
    }

    public void setApprovalStatus(MedicalApprovalStatus statusEnum) {
        if (statusEnum == null) {
            throw new IllegalArgumentException("MedicalApprovalStatus enum cannot be null.");
        }
        this.approvalStatus = statusEnum.name();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status != null) ? status.trim() : null;
    }

    // ================= Business Helper Methods =================

    public boolean isPending() {
        return STATUS_PENDING.equalsIgnoreCase(this.approvalStatus);
    }

    public boolean isApproved() {
        return STATUS_APPROVED.equalsIgnoreCase(this.approvalStatus);
    }

    public boolean isRejected() {
        return STATUS_REJECTED.equalsIgnoreCase(this.approvalStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Medical medical = (Medical) o;
        return medicalId == medical.medicalId &&
                studentId == medical.studentId &&
                Objects.equals(attendanceId, medical.attendanceId) &&
                Objects.equals(submissionDate, medical.submissionDate) &&
                Objects.equals(startDate, medical.startDate) &&
                Objects.equals(endDate, medical.endDate) &&
                Objects.equals(reason, medical.reason) &&
                Objects.equals(approvalStatus, medical.approvalStatus) &&
                Objects.equals(status, medical.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medicalId, studentId, attendanceId, submissionDate, startDate, endDate, reason,
                approvalStatus, status);
    }

    @Override
    public String toString() {
        return "Medical{" +
                "medicalId=" + medicalId +
                ", studentId=" + studentId +
                ", attendanceId=" + attendanceId +
                ", submissionDate=" + submissionDate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", reason='" + reason + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
