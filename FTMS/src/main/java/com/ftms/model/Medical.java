package com.ftms.model;

import java.time.LocalDate;

/**
 * Independent model class representing a student medical record in the FTMS system.
 * Maps to the medical table:
 * Medical_ID, Student_ID, Attendance_ID, Submission_date, Start_date, End_date,
 * Reason, Approval_status, Status, medicalimg.
 */
public class Medical {

    public static final String APPROVAL_PENDING = "PENDING";
    public static final String APPROVAL_APPROVED = "APPROVED";
    public static final String APPROVAL_REJECTED = "REJECTED";

    public static final String STATUS_RECORDED = "RECORDED";

    private int medicalId;
    private int studentId;
    private Integer attendanceId;
    private LocalDate submissionDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String approvalStatus;
    private String status;
    private String medicalImg;

    /**
     * Default constructor.
     * Initializes approvalStatus to PENDING and status to RECORDED.
     */
    public Medical() {
        this.approvalStatus = APPROVAL_PENDING;
        this.status = STATUS_RECORDED;
    }

    /**
     * Constructor for creating a new medical record before Medical_ID is generated.
     */
    public Medical(int studentId, Integer attendanceId, LocalDate submissionDate,
                   LocalDate startDate, LocalDate endDate, String reason,
                   String approvalStatus, String status) {
        this(studentId, attendanceId, submissionDate, startDate, endDate, reason, approvalStatus, status, null);
    }

    /**
     * Constructor for creating a new medical record including medicalImg before Medical_ID is generated.
     */
    public Medical(int studentId, Integer attendanceId, LocalDate submissionDate,
                   LocalDate startDate, LocalDate endDate, String reason,
                   String approvalStatus, String status, String medicalImg) {
        setStudentId(studentId);
        setAttendanceId(attendanceId);
        setSubmissionDate(submissionDate);
        setStartDate(startDate);
        setEndDate(endDate);
        setReason(reason);
        setApprovalStatus(approvalStatus);
        setStatus(status);
        setMedicalImg(medicalImg);
    }

    /**
     * Constructor matching 9 columns of the medical table (without medicalImg).
     */
    public Medical(int medicalId, int studentId, Integer attendanceId,
                   LocalDate submissionDate, LocalDate startDate, LocalDate endDate,
                   String reason, String approvalStatus, String status) {
        this(medicalId, studentId, attendanceId, submissionDate, startDate, endDate, reason, approvalStatus, status, null);
    }

    /**
     * Full constructor matching all 10 columns of the medical table (including medicalimg).
     */
    public Medical(int medicalId, int studentId, Integer attendanceId,
                   LocalDate submissionDate, LocalDate startDate, LocalDate endDate,
                   String reason, String approvalStatus, String status, String medicalImg) {
        setMedicalId(medicalId);
        setStudentId(studentId);
        setAttendanceId(attendanceId);
        setSubmissionDate(submissionDate);
        setStartDate(startDate);
        setEndDate(endDate);
        setReason(reason);
        setApprovalStatus(approvalStatus);
        setStatus(status);
        setMedicalImg(medicalImg);
    }

    public int getMedicalId() {
        return medicalId;
    }

    public void setMedicalId(int medicalId) {
        if (medicalId < 0) {
            throw new IllegalArgumentException("Medical_ID cannot be negative.");
        }
        this.medicalId = medicalId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("Student_ID must be a positive integer.");
        }
        this.studentId = studentId;
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        if (attendanceId != null && attendanceId <= 0) {
            throw new IllegalArgumentException("Attendance_ID must be positive when provided.");
        }
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
        if (startDate != null && this.endDate != null && this.endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Start_date cannot be after End_date.");
        }
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate != null && this.startDate != null && endDate.isBefore(this.startDate)) {
            throw new IllegalArgumentException("End_date cannot be before Start_date.");
        }
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = (reason != null && !reason.trim().isEmpty()) ? reason.trim() : null;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        if (approvalStatus == null || approvalStatus.trim().isEmpty()) {
            this.approvalStatus = APPROVAL_PENDING;
            return;
        }
        String normalized = approvalStatus.trim().toUpperCase();
        if (!APPROVAL_PENDING.equals(normalized)
                && !APPROVAL_APPROVED.equals(normalized)
                && !APPROVAL_REJECTED.equals(normalized)) {
            throw new IllegalArgumentException("Approval_status must be PENDING, APPROVED, or REJECTED.");
        }
        this.approvalStatus = normalized;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : null;
    }

    public String getMedicalImg() {
        return medicalImg;
    }

    public void setMedicalImg(String medicalImg) {
        this.medicalImg = (medicalImg != null && !medicalImg.trim().isEmpty()) ? medicalImg.trim() : null;
    }

    /**
     * Approves this medical record.
     */
    public void approveMedical() {
        this.approvalStatus = APPROVAL_APPROVED;
    }

    /**
     * Rejects this medical record.
     */
    public void rejectMedical() {
        this.approvalStatus = APPROVAL_REJECTED;
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
                ", medicalImg='" + medicalImg + '\'' +
                '}';
    }
}
