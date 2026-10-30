package com.ftms.view.lecturer;

import javax.swing.*;
import java.awt.*;
import java.net.URL;




public class LecturerDashboard extends JFrame{

       JPanel main_panel = new JPanel(new BorderLayout());    //slide bar and dear balence main panel

        private int lectureId;

    public LecturerDashboard(int lectureId){

        this.lectureId = lectureId;


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

        Color bgcolor = new Color(245, 249, 252);
        this.getContentPane().setBackground(bgcolor);

    this.setLayout(new BorderLayout());


}

    private void CreateHeading(){

        JPanel heading_panel = new JPanel(new BorderLayout());
        heading_panel.setPreferredSize(new Dimension(500,200));
        heading_panel.setBackground(new Color(245, 249, 252));
        heading_panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 30));


        JLabel Heading =  new JLabel();
        Heading.setText("Welcome Lecturer");
        Heading.setVerticalAlignment(JLabel.CENTER);

        Color h_color = new Color(16,45,79);
        Heading.setForeground(h_color);
        Heading.setFont(new Font("Arial",Font.BOLD,50));

        heading_panel.add(Heading);

        URL headin_image_path = getClass().getResource("/images/header_image.png");
        ImageIcon heading_icon = new ImageIcon(headin_image_path);
        JLabel image_label = new JLabel(heading_icon);
        heading_panel.add(image_label, BorderLayout.EAST);


        main_panel.add(heading_panel,BorderLayout.NORTH);

    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                 // Main Button
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void CreateButton(){


        JPanel button_panel = new JPanel();
        Color button_panel_color = new Color(245, 249, 252);
        button_panel.setBackground(button_panel_color);


        Color buttoncolor = new Color(255, 255, 255);
        Color button_text_color = new Color(16, 45, 79);
        Dimension button_size = new Dimension(300,180);

        //utton_panel.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        button_panel.setLayout(new GridLayout(3, 2, 20, 20));

        //my courses botton


        JButton my_course_Button  = new JButton();
        my_course_Button.setText("My Courses");
        my_course_Button.setFont(new Font("Arial",Font.BOLD,30));

        my_course_Button.setBackground(buttoncolor);
        my_course_Button.setForeground(button_text_color);
        my_course_Button.setPreferredSize(button_size);

        URL mange_path = getClass().getResource("/images/manae_icon.png");
        ImageIcon mange_icon = new ImageIcon(mange_path);
        my_course_Button.setIcon(mange_icon);




        button_panel.add(my_course_Button);

        //Action

        my_course_Button.addActionListener(e -> {

            new MyCourses(lectureId);       //lecutureta adal coures eak withrak penna nm meke id ek pss krnn oni
        //    this.setVisible(false);   //no class only hide
        });


        //UPload  Material button

        JButton upload_matatial_Button = new JButton();
        upload_matatial_Button.setText("Upload Materials");
        upload_matatial_Button.setFont(new Font("Arial",Font.BOLD,30));

        upload_matatial_Button.setBackground(buttoncolor);
        upload_matatial_Button.setForeground(button_text_color);
        upload_matatial_Button.setPreferredSize(button_size);


        URL upload_matarial_path = getClass().getResource("/images/upload_metarial_icon.png");
        ImageIcon upload_matarial_icon = new ImageIcon(upload_matarial_path);
        upload_matatial_Button.setIcon(upload_matarial_icon);


        button_panel.add(upload_matatial_Button);

        upload_matatial_Button.addActionListener(e -> {

            new CourseMaterials(lectureId);

        });

        //Upload marks

        JButton upload_marks = new JButton("Upload Marks");
        upload_marks.setFont(new Font("Arial",Font.BOLD,30));


        button_panel.add(upload_marks);
        upload_marks.setBackground(buttoncolor);
        upload_marks.setForeground(button_text_color);
        upload_marks.setPreferredSize(button_size);

        URL upload_marks_path = getClass().getResource("/images/upload_button_icon.png");
        ImageIcon upload_marks_icon = new ImageIcon(upload_marks_path);
        upload_marks.setIcon(upload_marks_icon);

        upload_marks.addActionListener(e -> {
             new UploadMarks(lectureId);
        });



        //Attendance Summary

        JButton attendance_summary = new JButton("Attendance Summary");
        attendance_summary.setFont(new Font("Arial",Font.BOLD,30));


        button_panel.add(attendance_summary);
        attendance_summary.setBackground(buttoncolor);
        attendance_summary.setForeground(button_text_color);
        attendance_summary.setPreferredSize(button_size);

        URL attendance_path = getClass().getResource("/images/atendance_icon.png");
        ImageIcon attandance_icon = new ImageIcon(attendance_path);
        attendance_summary.setIcon(attandance_icon);

        attendance_summary.addActionListener(e -> {

          new LecturerAttendanceSummary(lectureId);

        });




        //Student Eligibility

        JButton student_eligibility = new JButton("Student Eligibility");
        student_eligibility.setFont(new Font("Arial",Font.BOLD,30));



        button_panel.add(student_eligibility);
        student_eligibility.setBackground(buttoncolor);
        student_eligibility.setForeground(button_text_color);
        student_eligibility.setPreferredSize(button_size);

        URL student_path = getClass().getResource("/images/Student_icon.png");
        ImageIcon student_path_icon = new ImageIcon(student_path);
        student_eligibility.setIcon(student_path_icon);





        //main panel

        JPanel center_panel = new JPanel();
        center_panel.setLayout(new GridLayout(1,1,0,0));


        center_panel.setBorder(BorderFactory.createEmptyBorder(40,50,40,50));

        center_panel.add(button_panel);
        main_panel.add(center_panel, BorderLayout.CENTER);



    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                              //sidebar
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


private  void CreaterSidebar(){

        JPanel sidebar = new JPanel();
        Color sidebar_color = new Color(16,45,79);

        sidebar.setBackground(sidebar_color);

        this.add(sidebar , BorderLayout.WEST);


    // FTMS text

    JLabel sidebar_headr_text = new JLabel("FTMS");
    sidebar_headr_text.setForeground(Color.WHITE);
    sidebar_headr_text.setFont(new Font("Arial",Font.BOLD,40));
    sidebar.add(sidebar_headr_text);


    //FACULTY OF TECHNOLOGY
    //MANAGEMENT SYSTEM   text

    JLabel sidebar_dear_text_2 = new JLabel("FACULTY OF TECHNOLOGY");
    sidebar_dear_text_2.setForeground(Color.WHITE);
    sidebar_dear_text_2.setFont(new Font("Arial",Font.BOLD,15));
    sidebar.add(sidebar_dear_text_2);


    JLabel sidebar_dear_text_3 = new JLabel("MANAGEMENT SYSTEM");
    sidebar_dear_text_3.setForeground(Color.WHITE);
    sidebar_dear_text_3.setFont(new Font("Arial",Font.BOLD,15));
    sidebar.add(sidebar_dear_text_3);



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

        dashbord_button.addActionListener( e -> {
            new LecturerDashboard(lectureId);
        });


        URL Dash_path = getClass().getResource("/images/house.png");
        ImageIcon dashbord_icon = new ImageIcon(Dash_path);
        dashbord_button.setIcon(dashbord_icon);


        dashbord_button.setHorizontalAlignment(SwingConstants.LEFT);
        dashbord_button.setIconTextGap(15);





        //my course

        JButton my_course_button = new JButton("My Course");
        sidebar.add(my_course_button);

        my_course_button.setPreferredSize(sidebar_button_size);
        my_course_button.setBackground(sidebar_button_color);
        my_course_button.setForeground(Color.WHITE);

        URL manage_path = getClass().getResource("/images/book.png");
        ImageIcon manage_icon = new ImageIcon(manage_path);
        my_course_button.setIcon(manage_icon);

        my_course_button.setHorizontalAlignment(SwingConstants.LEFT);
        my_course_button.setIconTextGap(15);

        my_course_button.addActionListener(e -> {
            new MyCourses(lectureId);
        });


        //course material

        JButton course_material_button = new JButton("Upload Material");
        sidebar.add(course_material_button);

        course_material_button.setPreferredSize(sidebar_button_size);
        course_material_button.setBackground(sidebar_button_color);
        course_material_button.setForeground(Color.WHITE);

         URL course_path = getClass().getResource("/images/file.png");
         ImageIcon course_icon = new ImageIcon(course_path);
         course_material_button.setIcon(course_icon);

         course_material_button.setHorizontalAlignment(SwingConstants.LEFT);
         course_material_button.setIconTextGap(15);

         course_material_button.addActionListener(e -> {
             new CourseMaterials(lectureId);
         });


    //Upload marks

    JButton upload_marks_button = new JButton("Upload Marks");
    sidebar.add(upload_marks_button);

    upload_marks_button.setPreferredSize(sidebar_button_size);
    upload_marks_button.setBackground(sidebar_button_color);
    upload_marks_button.setForeground(Color.WHITE);

    URL upload_mark_path = getClass().getResource("/images/upload_marks.png");
    ImageIcon uploadmark_icon = new ImageIcon(upload_mark_path);
    upload_marks_button.setIcon(uploadmark_icon);

    upload_marks_button.setHorizontalAlignment(SwingConstants.LEFT);
    upload_marks_button.setIconTextGap(15);

    upload_marks_button.addActionListener(e -> {
        new UploadMarks(lectureId);


    });


    //Attendance  summary

    JButton attendance_button = new JButton("Attendance Summary");
    sidebar.add(attendance_button);

    attendance_button.setPreferredSize(sidebar_button_size);
    attendance_button.setBackground(sidebar_button_color);
    attendance_button.setForeground(Color.WHITE);

    URL attendance_image_path = getClass().getResource("/images/attendance_summary.png");
    ImageIcon attendance_icon = new ImageIcon(attendance_image_path);
    attendance_button.setIcon(attendance_icon);

    attendance_button.setHorizontalAlignment(SwingConstants.LEFT);
    attendance_button.setIconTextGap(15);

    attendance_button.addActionListener(e -> {
        new LecturerAttendanceSummary(lectureId);


    });



    //Student Eligibility

    JButton eligibility_button = new JButton("Attendance Summary");
    sidebar.add(attendance_button);

    eligibility_button.setPreferredSize(sidebar_button_size);
    eligibility_button.setBackground(sidebar_button_color);
    eligibility_button.setForeground(Color.WHITE);

    URL eligibility_image_path = getClass().getResource("/images/student_eligibility.png");
    ImageIcon eligibility_icon = new ImageIcon(eligibility_image_path);
    eligibility_button.setIcon(eligibility_icon);

    eligibility_button.setHorizontalAlignment(SwingConstants.LEFT);
    eligibility_button.setIconTextGap(15);

    eligibility_button.addActionListener(e -> {



    });

        //log out

        JButton logout_button = new JButton("Logout");
        sidebar.add(logout_button);

        logout_button.setPreferredSize(sidebar_button_size);
        logout_button.setBackground(sidebar_button_color);
        logout_button.setForeground(Color.WHITE);


        URL log_path = getClass().getResource("/images/logout.png");
        ImageIcon log_icon = new ImageIcon(log_path);
        logout_button.setIcon(log_icon);


        logout_button.setHorizontalAlignment(SwingConstants.LEFT);
        logout_button.setIconTextGap(15);


    }



}
