package com.ftms.view.lecturer;

import javax.swing.*;
import java.awt.*;

public class ManageCourses extends JFrame{


public ManageCourses() {

    setWindow();
    Create_Fields();

    setVisible(true);

}


private void setWindow(){

    setTitle("Manage Courses");
    setSize(600,400);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    this.setLayout(new GridLayout(9,2,10,10));

}

private void Create_Fields(){

    //course coad
    JLabel course_label = new JLabel("Course Coad:");
    this.add(course_label);


    JTextField coad_field = new JTextField();

    coad_field.setColumns(15);

    this.add(coad_field);

    //course name

    JLabel c_name = new JLabel("Course Name");
    this.add(c_name);

    JTextField c_namefield = new JTextField();
    c_namefield.setColumns(15);

    this.add(c_namefield);

    //Semester

    JLabel  semester_label = new JLabel("Semester:");
    this.add(semester_label);

    JTextField s_field = new JTextField();
    s_field.setColumns(15);

    this.add(s_field);


    //acadamic year

    JLabel acadamic_year = new JLabel("Acadamic Year:");
    this.add(acadamic_year);

    JTextField year_field = new JTextField();
    year_field.setColumns(15);
    this.add(year_field);


    //Theory hourse

    JLabel theory_hourse_label = new JLabel("Theory Hourse:");
    this.add(theory_hourse_label);

    JTextField theory_hourse_field = new JTextField();
    theory_hourse_field.setColumns(15);
    this.add(theory_hourse_field);



    //Theory cradit

    JLabel theory_cradit_label = new JLabel("Theory Cradit:");
    this.add(theory_cradit_label);

    JTextField theory_cradit_field = new JTextField();
    theory_cradit_field.setColumns(15);
    this.add(theory_cradit_field);



    //practical hourse

    JLabel p_h_label = new JLabel("Practical Hourse");
    this.add(p_h_label);


    JTextField p_h_field = new JTextField();
    p_h_field.setColumns(15);
    this.add(p_h_field);

    //Practical Cradit

    JLabel practical_cradit_label = new JLabel("Practical Cradit:");
    this.add(practical_cradit_label);

    JTextField p_c_field = new JTextField();
    p_c_field.setColumns(15);
    this.add(p_c_field);



    JButton savebutton = new JButton("Save course");
    this.add(savebutton);



}




}


