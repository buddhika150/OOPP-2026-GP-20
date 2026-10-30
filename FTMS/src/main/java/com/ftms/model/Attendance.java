package com.ftms.model;

import java.time.LocalDate;

/**
 * Independent model class representing an attendance record in the FTMS system.
 * Maps to the attendance table:
 * Attendance_ID, Student_ID, Course_code, Session_date, Type, Is_Present.
 */
public class Attendance {

    public static final String TYPE_THEORY = "THEORY";
    public static final String TYPE_PRACTICAL = "PRACTICAL";

    private int attendanceId;
    private int studentId;
    private String courseCode;
    private LocalDate sessionDate;
    private String type;
    private boolean isPresent;

    /**
     * Default constructor.
     * Sets isPresent to true to match the SQL default value (DEFAULT 1).
     */
    public Attendance() {
        this.isPresent = true;
    }

    /**
     * Constructor for creating a new attendance record before Attendance_ID is generated.
     */
    public Attendance(int studentId, String courseCode, LocalDate sessionDate,
                      String type, boolean isPresent) {
        setStudentId(studentId);
        setCourseCode(courseCode);
        setSessionDate(sessionDate);
        setType(type);
        setPresent(isPresent);
    }

    /**
     * Full constructor matching all 6 columns of the attendance table.
     */
    public Attendance(int attendanceId, int studentId, String courseCode,
                      LocalDate sessionDate, String type, boolean isPresent) {
        setAttendanceId(attendanceId);
        setStudentId(studentId);
        setCourseCode(courseCode);
        setSessionDate(sessionDate);
        setType(type);
        setPresent(isPresent);
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        if (attendanceId < 0) {
            throw new IllegalArgumentException("Attendance_ID cannot be negative.");
        }
        this.attendanceId = attendanceId;
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

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course_code cannot be null or empty.");
        }
        this.courseCode = courseCode.trim().toUpperCase();
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null || type.trim().isEmpty()) {
            this.type = null;
            return;
        }
        String normalized = type.trim().toUpperCase();
        if (!TYPE_THEORY.equals(normalized) && !TYPE_PRACTICAL.equals(normalized)) {
            throw new IllegalArgumentException("Type must be THEORY or PRACTICAL.");
        }
        this.type = normalized;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        this.isPresent = present;
    }

    public boolean getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(boolean isPresent) {
        this.isPresent = isPresent;
    }

    /**
     * Helper method to calculate attendance percentage.
     */
    public static double calculateAttendancePercentage(int attendedSessions, int totalSessions) {
        if (totalSessions <= 0) {
            throw new IllegalArgumentException("Total sessions must be greater than zero.");
        }
        if (attendedSessions < 0 || attendedSessions > totalSessions) {
            throw new IllegalArgumentException("Attended sessions must be between 0 and total sessions.");
        }
        return (attendedSessions * 100.0) / totalSessions;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", studentId=" + studentId +
                ", courseCode='" + courseCode + '\'' +
                ", sessionDate=" + sessionDate +
                ", type='" + type + '\'' +
                ", isPresent=" + isPresent +
                '}';
    }
}

