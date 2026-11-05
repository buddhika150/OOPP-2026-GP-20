package com.ftms.model;

import java.time.LocalDate;

public class Medical {

    public static final String APPROVAL_PENDING = "PENDING";
    public static final String APPROVAL_APPROVED = "APPROVED";
    public static final String APPROVAL_REJECTED = "REJECTED";

    public static final String STATUS_RECORDED = "RECORDED";
    public static final int SUBMISSION_DEADLINE_DAYS = 14;

    private int medicalId, studentId;
    private Integer attendanceId;
    private LocalDate submissionDate, startDate, endDate;
    private String reason, approvalStatus, status, medicalImg;

    /**
     * Validates whether a medical submission is within the 14-day deadline from the absent date.
     * Deadline rule: absence date + 14 days.
     */
    public static boolean isWithin14Days(LocalDate absentDate, LocalDate submissionDate) {
        if (absentDate == null || submissionDate == null) {
            return false;
        }
        LocalDate deadline = absentDate.plusDays(SUBMISSION_DEADLINE_DAYS);
        return !submissionDate.isBefore(absentDate) && !submissionDate.isAfter(deadline);
    }

    /**
     * Enforces the 14-day rule. Throws IllegalArgumentException if expired.
     */
    public static void validateSubmissionDeadline(LocalDate absentDate, LocalDate submissionDate) {
        if (absentDate == null) {
            throw new IllegalArgumentException("Absent date is required to validate medical submission.");
        }
        if (submissionDate == null) {
            submissionDate = LocalDate.now();
        }
        if (submissionDate.isBefore(absentDate)) {
            throw new IllegalArgumentException("Submission date cannot be before the absent date.");
        }
        LocalDate deadline = absentDate.plusDays(SUBMISSION_DEADLINE_DAYS);
        if (submissionDate.isAfter(deadline)) {
            throw new IllegalArgumentException("Medical submission deadline has expired. You can only submit a medical certificate within 14 days from the absent date.");
        }
    }

    // Constructor for existing medical records (10 arguments)
    public Medical(int medicalId, int studentId,
                   Integer attendanceId,
                   LocalDate submissionDate,
                   LocalDate startDate,
                   LocalDate endDate,
                   String reason,
                   String approvalStatus,
                   String status,
                   String medicalImg) {

        this.medicalId = medicalId;
        this.studentId = studentId;
        this.attendanceId = attendanceId;
        this.submissionDate = submissionDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.approvalStatus = approvalStatus;
        this.status = status;
        this.medicalImg = medicalImg;
    }

    // Constructor for new medical records (9 arguments)
    public Medical(int studentId,
                   Integer attendanceId,
                   LocalDate submissionDate,
                   LocalDate startDate,
                   LocalDate endDate,
                   String reason,
                   String approvalStatus,
                   String status,
                   String medicalImg) {

        this(0, studentId, attendanceId,
                submissionDate, startDate, endDate,
                reason, approvalStatus, status, medicalImg);
    }

    public int getMedicalId() {
        return medicalId;
    }

    public void setMedicalId(int medicalId) {
        this.medicalId = medicalId;
    }

    public int getStudentId() {
        return studentId;
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public String getStatus() {
        return status;
    }

    public String getMedicalImg() {
        return medicalImg;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public void approveMedical() {
        this.approvalStatus = APPROVAL_APPROVED;
    }

    public void rejectMedical() {
        this.approvalStatus = APPROVAL_REJECTED;
    }
}
