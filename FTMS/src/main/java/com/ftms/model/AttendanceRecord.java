package com.ftms.model;

public class AttendanceRecord {
    private String courseCode;
    private String courseName;
    private int totalSessions;
    private int attendedSessions;
    private double percentage;

    public AttendanceRecord(String courseCode, String courseName, int totalSessions, int attendedSessions, double percentage) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.totalSessions = totalSessions;
        this.attendedSessions = attendedSessions;
        this.percentage = percentage;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public int getAttendedSessions() {
        return attendedSessions;
    }

    public double getPercentage() {
        return percentage;
    }
}