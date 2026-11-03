package com.ftms.model;

public class GPA {
    private double sgpa;
    private double cgpa;
    private int semester;

    public GPA(double sgpa, double cgpa, int semester) {
        this.sgpa = sgpa;
        this.cgpa = cgpa;
        this.semester = semester;
    }

    public double getSgpa() { return sgpa; }
    public double getCgpa() { return cgpa; }
    public int getSemester() { return semester; }
}