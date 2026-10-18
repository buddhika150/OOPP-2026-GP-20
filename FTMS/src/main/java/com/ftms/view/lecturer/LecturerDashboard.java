package com.ftms.view.lecturer;
import javax.swing.*;
import java.awt.*;


public class LecturerDashboard extends JFrame{

       JPanel main_panel = new JPanel(new BorderLayout());    //slide bar and dear balence main panel


    public LecturerDashboard(){




        setupWindow();
        CreateHeading();
        CreateButton();
        CreaterSidebar();

        this.add(main_panel, BorderLayout.CENTER);  //slide bar and hear balence main panel;







        setVisible(true);

}

//window
private void setupWindow(){

        setTitle("Lecturer Dashboard");
        setSize(800 , 500);
        setLocationRelativeTo(null);            //open windo dispay center
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color bgcolor = new Color(242, 246, 250);
        this.getContentPane().setBackground(bgcolor);

    this.setLayout(new BorderLayout());


}

    private void CreateHeading(){

        JPanel heading_panel = new JPanel();
        heading_panel.setPreferredSize(new Dimension(500,200));


        JLabel Heading =  new JLabel();
        Heading.setText("Welcome Lecturer");
        Heading.setHorizontalAlignment(JLabel.CENTER);  // Left -> center
        Heading.setVerticalAlignment(JLabel.TOP);

        Color h_color = new Color(16,45,79);
        Heading.setForeground(h_color);
        Heading.setFont(new Font("Arial",Font.BOLD,50));

        heading_panel.add(Heading);


        main_panel.add(heading_panel,BorderLayout.NORTH);

    }

    private void CreateButton(){


        JPanel button_panel = new JPanel();
        Color button_panel_color = new Color(242,246,250);
        button_panel.setBackground(button_panel_color);


        Color buttoncolor = new Color(0,143,156);
        Dimension button_size = new Dimension(300,200);

        //Manage botton


        JButton Mange_Button  = new JButton();
        Mange_Button.setText("ManageCourses");
        Mange_Button.setFont(new Font("Arial",Font.BOLD,30));

        Mange_Button.setBackground(buttoncolor);
        Mange_Button.setForeground(Color.WHITE);
        Mange_Button.setPreferredSize(button_size);

        button_panel.add(Mange_Button);

        //Action

        Mange_Button.addActionListener(e -> {

            ManageCourses M_Window = new  ManageCourses();
        });


        //UPload button

        JButton upload_Button = new JButton();
        upload_Button.setText("Upload Materials");
        upload_Button.setFont(new Font("Arial",Font.BOLD,30));

        upload_Button.setBackground(buttoncolor);
        upload_Button.setForeground(Color.WHITE);
        upload_Button.setPreferredSize(button_size);


        button_panel.add(upload_Button);

        //Upload marks

        JButton upload_marks = new JButton("Upload Marks");
        upload_marks.setFont(new Font("Arial",Font.BOLD,30));


        button_panel.add(upload_marks);
        upload_marks.setBackground(buttoncolor);
        upload_marks.setForeground(Color.WHITE);
        upload_marks.setPreferredSize(button_size);




        //Attendance Summary

        JButton attendance_summary = new JButton("Attendance Summary");
        attendance_summary.setFont(new Font("Arial",Font.BOLD,30));


        button_panel.add(attendance_summary);
        attendance_summary.setBackground(buttoncolor);
        attendance_summary.setForeground(Color.WHITE);
        attendance_summary.setPreferredSize(button_size);




        //Student Eligibility

        JButton student_eligibility = new JButton("Student Eligibility");
        student_eligibility.setFont(new Font("Arial",Font.BOLD,30));



        button_panel.add(student_eligibility);
        student_eligibility.setBackground(buttoncolor);
        student_eligibility.setForeground(Color.WHITE);
        student_eligibility.setPreferredSize(button_size);




        //button_panel.setLayout(new GridLayout(3,2 ,15,15));
        button_panel.setLayout(new FlowLayout());
        button_panel.setPreferredSize(new Dimension(550, 360));




        //main panel

        JPanel center_panel = new JPanel();
        center_panel.setLayout(new GridLayout(1,1,0,0));

        center_panel.setBorder(BorderFactory.createEmptyBorder(40,50,40,50));

        center_panel.add(button_panel);
        main_panel.add(center_panel, BorderLayout.CENTER);



    }



    private  void CreaterSidebar(){

        JPanel sidebar = new JPanel();
        Color sidebar_color = new Color(16,45,79);

        sidebar.setBackground(sidebar_color);

        this.add(sidebar , BorderLayout.WEST);

        //side bar button common variable

        Dimension sidebar_size = new Dimension(250,500);
        sidebar.setPreferredSize(sidebar_size);


        Color sidebar_button_color = new Color(0,143,156);
        Dimension sidebar_button_size = new Dimension(215,40);

        //Dashbord
        JButton dashbord_button = new JButton("Dashbord");
        dashbord_button.setFont(new Font("Arial",Font.BOLD,15));

        sidebar.add(dashbord_button);

        dashbord_button.setBackground(sidebar_button_color);
        dashbord_button.setForeground(Color.WHITE);

        dashbord_button.setPreferredSize(sidebar_button_size);


        //Mange course

        JButton manage_course_button = new JButton("Manage Course");
        sidebar.add(manage_course_button);

        manage_course_button.setPreferredSize(sidebar_button_size);
        manage_course_button.setBackground(sidebar_button_color);
        manage_course_button.setForeground(Color.WHITE);


        //Upload Marks

        JButton course_material_button = new JButton("Course Materials");
        sidebar.add(course_material_button);

        course_material_button.setPreferredSize(sidebar_button_size);
        course_material_button.setBackground(sidebar_button_color);
        course_material_button.setForeground(Color.WHITE);


        //log out

        JButton logout_button = new JButton("Logout");
        sidebar.add(logout_button);

        logout_button.setPreferredSize(sidebar_button_size);
        logout_button.setBackground(sidebar_button_color);
        logout_button.setForeground(Color.WHITE);







    }



}
