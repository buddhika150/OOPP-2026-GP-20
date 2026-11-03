package com.ftms.model;

public class Timetable {

    private int timetableId;
    private String courseCode;
    private String department;
    private String day;
    private String timeSlot;

    public Timetable(int timetableId, String courseCode,
                     String department, String day, String timeSlot) {
        this.timetableId = timetableId;
        this.courseCode = courseCode;
        this.department = department;
        this.day = day;
        this.timeSlot = timeSlot;
    }

    public int getTimetableId() {
        return timetableId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getDepartment() {
        return department;
    }

    public String getDay() {
        return day;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }
}