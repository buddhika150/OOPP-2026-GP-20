
package com.ftms.model;

import java.time.LocalDate;

public class Medical {

    public static final String APPROVAL_PENDING = "PENDING";
    public static final String APPROVAL_APPROVED = "APPROVED";
    public static final String APPROVAL_REJECTED = "REJECTED";

    public static final String STATUS_RECORDED = "RECORDED";

    private int medicalId, studentId;
    private Integer attendanceId;
    private LocalDate submissionDate, startDate, endDate;
    private String reason, approvalStatus, status, medicalImg;

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
}
