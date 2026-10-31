
package com.ftms.view.lecturer;

import com.ftms.database.DatabaseConnection;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import java.awt.*;
import java.net.URL;


public class MyCourses extends JFrame{

    JPanel main_panel = new JPanel(new BorderLayout(10,10));

    private int lectureId;

    MyCourses(int lecturerId){

        this.lectureId = lecturerId;

        windwo();
        Heading();
        createTabel();
        CreaterSidebar();

        setVisible(true);

    }



    private void windwo(){


        setTitle("My courses");
        setSize(800 ,500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10 , 10));
        getContentPane().setBackground(new Color(245, 249, 252));

        this.add(main_panel,BorderLayout.CENTER);

    }

    private void Heading(){

        JPanel heading_panel = new JPanel(new BorderLayout());
        Dimension heading_size = new Dimension(500,200);
        heading_panel.setPreferredSize(heading_size);
        heading_panel.setBackground(Color.WHITE);



        JLabel heading_label = new JLabel("My course");
        heading_label.setFont(new Font("arial",Font.BOLD,50));
        heading_panel.add(heading_label,BorderLayout.WEST);
        main_panel.add(heading_panel, BorderLayout.NORTH);


        URL my_course_imge_path = getClass().getResource("/images/mycourse_image.png");
        ImageIcon heading_image = new ImageIcon(my_course_imge_path);
        JLabel image_label = new JLabel(heading_image);
        heading_panel.add(image_label,BorderLayout.EAST);


    }

    private void createTabel(){


        String[] colums = {
                "Course Code",
                "Course Name",
                "Semester",
                "Acadamic Year"
        };

        DefaultTableModel database_data_gate_model = new DefaultTableModel(colums,0);

        JTable course_tabel = new JTable(database_data_gate_model);

        course_tabel.setRowHeight(30);  //one raw hight
        JScrollPane can_scroll = new JScrollPane(course_tabel);

        main_panel.add(can_scroll,BorderLayout.CENTER);

        // colums desing
        course_tabel.getTableHeader().setFont(new Font("arial",Font.BOLD,16));
        course_tabel.getTableHeader().setBackground(new Color(220,235,250));
        course_tabel.getTableHeader().setPreferredSize(new Dimension(100,45));

        //rows deding

        course_tabel.setFont(new Font("arial", Font.PLAIN, 15));

        loadCourses(database_data_gate_model);

    }

    private void loadCourses(DefaultTableModel  model) {

        String sql = "SELECT c.Course_code, c.Course_name, c.Semester, c.Academic_year " +
                "FROM course c " +
                "INNER JOIN lecturer_course lc ON c.Course_code = lc.Course_code " +
                "WHERE lc.Lecturer_ID = ?";

        try {

            Connection con = DatabaseConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, lectureId);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                String coad = rs.getString("Course_code");

                String name = rs.getString("Course_name");

                int semester = rs.getInt("Semester");

                int year =rs.getInt("Academic_year");

                model.addRow(new Object[]{coad,name,semester,year});


            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }

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
