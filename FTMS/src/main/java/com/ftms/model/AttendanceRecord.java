package com.ftms.model;

public class AttendanceRecord {
    private String courseCode;
    private String courseName;
    private int totalSessions;
    private int attendedSessions;
    private double percentage;
    private int theoryTotal;
    private int theoryAttended;
    private int practicalTotal;
    private int practicalAttended;

    public AttendanceRecord(String courseCode, String courseName, int totalSessions, int attendedSessions, double percentage) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.totalSessions = totalSessions;
        this.attendedSessions = attendedSessions;
        this.percentage = percentage;
    }

    public AttendanceRecord(String courseCode, String courseName, int totalSessions,
                            int attendedSessions, double percentage,
                            int theoryTotal, int theoryAttended,
                            int practicalTotal, int practicalAttended) {
        this(courseCode, courseName, totalSessions, attendedSessions, percentage);
        this.theoryTotal = theoryTotal;
        this.theoryAttended = theoryAttended;
        this.practicalTotal = practicalTotal;
        this.practicalAttended = practicalAttended;
    }

    public int getTheoryTotal() { return theoryTotal; }
    public int getTheoryAttended() { return theoryAttended; }
    public int getPracticalTotal() { return practicalTotal; }
    public int getPracticalAttended() { return practicalAttended; }
    public double getTheoryPercentage() {
        return theoryTotal == 0 ? 0.0 : 100.0 * theoryAttended / theoryTotal;
    }
    public double getPracticalPercentage() {
        return practicalTotal == 0 ? 0.0 : 100.0 * practicalAttended / practicalTotal;
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