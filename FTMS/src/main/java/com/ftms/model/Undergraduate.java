package com.ftms.model;

public class Undergraduate {
    private int studentId;
    private String tgNum;
    private String batch;
    private String department;
    private String nic;
    private String status;
    private int year;
    private int semester;
    private String fname;
    private String lname;
    private String email;
    private String phone;

    public Undergraduate(int studentId, String tgNum, String batch, String department, String nic, String status, int year, int semester, String fname, String lname, String email, String phone) {
        this.studentId = studentId;
        this.tgNum = tgNum;
        this.batch = batch;
        this.department = department;
        this.nic = nic;
        this.status = status;
        this.year = year;
        this.semester = semester;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.phone = phone;
    }

    public int getStudentId() { return studentId; }
    public String getTgNum() { return tgNum; }
    public String getBatch() { return batch; }
    public String getDepartment() { return department; }
    public String getNic() { return nic; }
    public String getStatus() { return status; }
    public int getYear() { return year; }
    public int getSemester() { return semester; }
    public String getFname() { return fname; }
    public String getLname() { return lname; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}