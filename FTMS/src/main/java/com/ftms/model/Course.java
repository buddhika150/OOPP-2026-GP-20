package com.ftms.model;

public class Course {

    private String courseCode;
    private String courseName;
    private int semester;
    private int academicYear;
    private int theoryHours;
    private int theoryCredits;
    private int practicalHours;
    private int practicalCredits;


    public Course(String courseCode , String courseName ,int semester, int academicYear,
                   int theoryHours, int theoryCredits, int practicalHours , int practicalCredits){

        this.courseCode = courseCode;
        this.courseName = courseName;
        this.semester = semester;
        this.academicYear = academicYear;
        this.theoryHours = theoryHours;
        this.theoryCredits = theoryCredits;
        this.practicalHours = practicalHours;
        this.practicalCredits = practicalCredits;

    }


    //getter (one by one viwe details)

    public String getCourseCode (){

        return courseCode;
    }

    public  String getCourseName(){
        return courseName;
    }

    public int getSemester(){
        return semester;
    }

    public int getAcademicYear(){
        return academicYear;
    }

    public int getTheoryHours(){
        return theoryHours;
    }

    public int getTheoryCredits(){
        return theoryCredits;
    }

    public int getPracticalHours(){
        return practicalHours;
    }

    public int getPracticalCredits(){
        return practicalCredits;
    }




    //setter (after course create change vlaues)


    public void setCourseCode(String courseCode){

        this.courseCode = courseCode;

    }

    public void setCourseName(String courseName){

        this.courseName = courseName;

    }

    public void setSemester(int semester){

        this.semester = semester;

    }

    public void setAcademicYear(int academicYear){

        this.academicYear = academicYear;
    }

    public void setTheoryHours(int theoryHours){

        this.theoryHours = theoryHours;

    }

    public void setTheoryCredits(int theoryCredits){

        this.theoryCredits = theoryCredits;

    }

    public void setPracticalHours(int practicalHours){

        this.practicalHours = practicalHours;

    }

    public void setPracticalCredits(int practicalCredits){

        this.practicalCredits = practicalCredits;

    }


    //viwe all details

    public String getCourseDetails() {

        return "Course Code: " + courseCode +
                "\nCourse Name: " + courseName +
                "\nSemester: " + semester +
                "\nAcademic Year: " + academicYear +
                "\nTheory Hours: " + theoryHours +
                "\nTheory Credits: " + theoryCredits +
                "\nPractical Hours: " + practicalHours +
                "\nPractical Credits: " + practicalCredits;
    }



}