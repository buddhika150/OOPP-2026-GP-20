package com.ftms.model;

import java.util.*;
import java.util.Date;

public class CourseMaterial {


    private  int materialId;
    private String courseCode;
    private String title;
    private String materialType;
    private String filePath;
    private Date uploadedDate;                  // in put date so variable type is dater

    public CourseMaterial(int materialId , String courseCode ,  String title,String materialType,
                          String filePath,Date uploadedDate){

        this.materialId = materialId;
        this.courseCode = courseCode;
        this.title = title;
        this.materialType = materialType;
        this.filePath = filePath;
        this.uploadedDate = uploadedDate;


    }

    //Getter

    public int getMaterialId(){
        return materialId;
    }

    public String getCourseCode(){
        return courseCode;
    }

    public String getTitle(){
        return title;
    }

    public String getMaterialType(){
        return materialType;
    }

    public String getFilePath(){
        return filePath;
    }

    public Date getUploadedDate(){
        return uploadedDate;
    }


    //Setter

    public void setMaterialId(int materialId){
        this.materialId = materialId;
    }

    public void setCourseCode(String courseCode){
        this.courseCode = courseCode;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public void setMaterialType(String materialType){
        this.materialType=materialType;
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public void setUploadedDate(Date uploadedDate){
        this.uploadedDate = uploadedDate;
    }


    //couse details rerurn;

    public String getMaterialDetails(){

        return "material Id :" + materialId +
               "\ncourseCode :" + courseCode +
                "\ntitle :" + title +
                "\nmaterialType :" + materialType +
                "\nfilePath :" + filePath +
                "\nuploadedDate :" + uploadedDate;

    }

    // methode call ditals viwe

    public void viewCourseMaterial(){
        System.out.println(getMaterialDetails());
    }

}
