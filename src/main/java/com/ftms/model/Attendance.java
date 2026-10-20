package com.ftms.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Model class representing an Attendance record.
 * Maps directly to the Attendance table in db.sql:
 * Attendance(Attendance_ID, Student_ID, Course_code, Session_date, Type).
 *
 * Demonstrates:
 * - Classes and Objects: Explicit model representing a recorded attendance session.
 * - Encapsulation: Private member attributes with validated getters and setters.
 * - Database Compatibility: Matches exact column names, types, and ENUM values (THEORY, PRACTICAL).
 */
public class Attendance {

    // Database ENUM constants for Attendance Type
    public static final String TYPE_THEORY = "THEORY";
    public static final String TYPE_PRACTICAL = "PRACTICAL";

    public enum AttendanceType {
        THEORY,
        PRACTICAL
    }

    private int attendanceId;
    private int studentId;
    private String courseCode;
    private LocalDate sessionDate;
    private String type;

    /**
     * Default constructor.
     */
    public Attendance() {
    }

    /**
     * Constructor for creating a new attendance record before insertion (without Attendance_ID).
     *
     * @param studentId   foreign key Student_ID
     * @param courseCode  foreign key Course_code
     * @param sessionDate session date (NOT NULL)
     * @param type        session type ENUM('THEORY','PRACTICAL')
     */
    public Attendance(int studentId, String courseCode, LocalDate sessionDate, String type) {
        setStudentId(studentId);
        setCourseCode(courseCode);
        setSessionDate(sessionDate);
        setType(type);
    }

    /**
     * Full constructor for loading an existing attendance record from the database.
     *
     * @param attendanceId primary key Attendance_ID
     * @param studentId    foreign key Student_ID
     * @param courseCode   foreign key Course_code
     * @param sessionDate  session date (NOT NULL)
     * @param type         session type ENUM('THEORY','PRACTICAL')
     */
    public Attendance(int attendanceId, int studentId, String courseCode, LocalDate sessionDate, String type) {
        this.attendanceId = attendanceId;
        setStudentId(studentId);
        setCourseCode(courseCode);
        setSessionDate(sessionDate);
        setType(type);
    }

    // ================= Getters and Setters (Encapsulation) =================

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
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

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be null or empty.");
        }
        this.courseCode = courseCode.trim();
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        if (sessionDate == null) {
            throw new IllegalArgumentException("Session date cannot be null.");
        }
        this.sessionDate = sessionDate;
    }

    /**
     * Convenience setter for converting java.sql.Date from JDBC to LocalDate.
     */
    public void setSessionDate(java.sql.Date sqlDate) {
        if (sqlDate == null) {
            throw new IllegalArgumentException("Session date cannot be null.");
        }
        this.sessionDate = sqlDate.toLocalDate();
    }

    /**
     * Convenience getter for converting LocalDate to java.sql.Date for JDBC.
     */
    public java.sql.Date getSessionDateAsSqlDate() {
        return (this.sessionDate != null) ? java.sql.Date.valueOf(this.sessionDate) : null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Attendance type cannot be null.");
        }
        String upperType = type.trim().toUpperCase();
        if (!upperType.equals(TYPE_THEORY) && !upperType.equals(TYPE_PRACTICAL)) {
            throw new IllegalArgumentException("Invalid attendance type: '" + type + "'. Allowed ENUM values: " + TYPE_THEORY + ", " + TYPE_PRACTICAL);
        }
        this.type = upperType;
    }

    public void setType(AttendanceType attendanceType) {
        if (attendanceType == null) {
            throw new IllegalArgumentException("AttendanceType cannot be null.");
        }
        this.type = attendanceType.name();
    }

    // ================= Business & Utility Methods =================

    public boolean isTheory() {
        return TYPE_THEORY.equalsIgnoreCase(this.type);
    }

    public boolean isPractical() {
        return TYPE_PRACTICAL.equalsIgnoreCase(this.type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendance that = (Attendance) o;
        return attendanceId == that.attendanceId &&
                studentId == that.studentId &&
                Objects.equals(courseCode, that.courseCode) &&
                Objects.equals(sessionDate, that.sessionDate) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attendanceId, studentId, courseCode, sessionDate, type);
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", studentId=" + studentId +
                ", courseCode='" + courseCode + '\'' +
                ", sessionDate=" + sessionDate +
                ", type='" + type + '\'' +
                '}';
    }
}
