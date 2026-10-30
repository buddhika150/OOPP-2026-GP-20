package com.ftms.model;

public class Marks {
    private String courseCode;
    private String courseName;
    private double caMarks;
    private double finalExamMarks;
    private double totalMarks;
    private String grade;

    public Marks(String courseCode, String courseName, double caMarks, double finalExamMarks, double totalMarks, String grade) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.caMarks = caMarks;
        this.finalExamMarks = finalExamMarks;
        this.totalMarks = totalMarks;
        this.grade = grade;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public double getCaMarks() { return caMarks; }
    public double getFinalExamMarks() { return finalExamMarks; }
    public double getTotalMarks() { return totalMarks; }
    public String getGrade() { return grade; }

    public boolean viewMyEligibility() {
        return caMarks >= 40.0;
    }
}