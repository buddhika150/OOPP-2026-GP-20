package com.ftms.view.lecturer;

import com.ftms.database.DatabaseConnection;
import com.ftms.view.LoginFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.sql.*;

public class Lecturer_Student_Marks_GPA extends JFrame {

    private int lecturerId;

    private JComboBox<String> course_box;
    private JTable eligibility_table;
    private DefaultTableModel model;

    JPanel main_panel = new JPanel(new BorderLayout(10, 10));
    JPanel center_panel = new JPanel(new BorderLayout(10, 10));

    public Lecturer_Student_Marks_GPA(int lecturerId) {

        this.lecturerId = lecturerId;

        setupWindow();
        CreateHeading();
        CreaterSidebar();
        createCourseSelect();
        createTable();
        loadCourses();

        this.add(main_panel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void setupWindow() {

        setTitle("Student Marks, Grades & GPA");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        getContentPane().setBackground(new Color(245, 249, 252));
    }

    private void CreateHeading() {

        JPanel heading_panel = new JPanel(new BorderLayout());

        heading_panel.setPreferredSize(new Dimension(500, 150));
        heading_panel.setBackground(Color.WHITE);

        JLabel heading = new JLabel(" Student Marks, Grades & GPA");
        heading.setFont(new Font("Arial", Font.BOLD, 45));
        heading.setForeground(new Color(16, 45, 79));

        heading_panel.add(heading, BorderLayout.WEST);

        main_panel.add(heading_panel, BorderLayout.NORTH);
    }

    private void createCourseSelect() {

        JPanel course_panel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 30, 25)
        );

        course_panel.setBackground(Color.WHITE);

        JLabel course_label = new JLabel("Select Course:");
        course_label.setFont(new Font("Arial", Font.BOLD, 20));

        course_box = new JComboBox<>();
        course_box.setPreferredSize(new Dimension(250, 40));

        course_panel.add(course_label);
        course_panel.add(course_box);

        center_panel.setBackground(Color.WHITE);
        center_panel.add(course_panel, BorderLayout.NORTH);

        main_panel.add(center_panel, BorderLayout.CENTER);

        course_box.addActionListener(e -> {

            String courseCode =
                    (String) course_box.getSelectedItem();

            if (courseCode != null) {
                loadStudents(courseCode);
            }
        });
    }

    private void createTable() {

        String[] columns = {
                "Student ID", "Course Code", "CA /40", "Final /60",
                "Total /100", "Grade", "SGPA", "CGPA"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        eligibility_table = new JTable(model);

        eligibility_table.setRowHeight(40);
        eligibility_table.setFont(
                new Font("Arial", Font.PLAIN, 17)
        );

        eligibility_table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        center_panel.add(
                new JScrollPane(eligibility_table),
                BorderLayout.CENTER
        );
    }

    private void loadCourses() {

        try {

            Connection con = DatabaseConnection.getConnection();

            String sql =
                    "SELECT DISTINCT Course_code FROM lecturer_course " +
                            "WHERE Lecturer_ID = ?";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, lecturerId);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                course_box.addItem(rs.getString("Course_code"));
            }

            rs.close();
            pst.close();
            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void loadStudents(String courseCode) {

        model.setRowCount(0);

        try {

            Connection con = DatabaseConnection.getConnection();

            String sql =
                    "SELECT e.Student_ID, e.Course_code, " +
                            "m.CA_marks, m.Final_exam_marks, m.Total_marks, m.Grade, " +
                            "g.SGPA, g.CGPA " +
                            "FROM enrollment e " +
                            "LEFT JOIN marks m ON e.Student_ID = m.Student_ID " +
                            "AND e.Course_code = m.Course_code " +
                            "LEFT JOIN gpa g ON e.Student_ID = g.Student_ID " +
                            "AND e.Semester = g.Semester " +
                            "WHERE e.Course_code = ? " +
                            "AND EXISTS (SELECT 1 FROM lecturer_course lc " +
                            "WHERE lc.Course_code = e.Course_code " +
                            "AND lc.Lecturer_ID = ?) " +
                            "ORDER BY e.Student_ID";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, courseCode);
            pst.setInt(2, lecturerId);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object ca = rs.getObject("CA_marks");
                Object exam = rs.getObject("Final_exam_marks");
                Object total = rs.getObject("Total_marks");
                String grade = rs.getString("Grade");
                Object sgpa = rs.getObject("SGPA");
                Object cgpa = rs.getObject("CGPA");

                model.addRow(new Object[]{
                        rs.getInt("Student_ID"),
                        rs.getString("Course_code"),
                        ca == null ? "Pending" : ca,
                        exam == null ? "Pending" : exam,
                        total == null ? "Pending" : total,
                        grade == null || grade.trim().isEmpty() ? "Pending" : grade,
                        sgpa == null ? "Pending" : sgpa,
                        cgpa == null ? "Pending" : cgpa
                });
            }

            rs.close();
            pst.close();
            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
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
            new LecturerDashboard(lecturerId);
            this.setVisible(false);
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
            new MyCourses(lecturerId);
            this.setVisible(false);
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
            new CourseMaterials(lecturerId);
            this.setVisible(false);
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
            new UploadMarks(lecturerId);
            this.setVisible(false);


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
            new LecturerAttendanceSummary(lecturerId);
            this.setVisible(false);


        });



        //Student Eligibility

        JButton eligibility_button = new JButton("Student Eligibility");
        sidebar.add(eligibility_button);

        eligibility_button.setPreferredSize(sidebar_button_size);
        eligibility_button.setBackground(sidebar_button_color);
        eligibility_button.setForeground(Color.WHITE);

        URL eligibility_image_path = getClass().getResource("/images/student_eligibility.png");
        ImageIcon eligibility_icon = new ImageIcon(eligibility_image_path);
        eligibility_button.setIcon(eligibility_icon);

        eligibility_button.setHorizontalAlignment(SwingConstants.LEFT);
        eligibility_button.setIconTextGap(15);

        eligibility_button.addActionListener(e -> {

            new LecturerStudentEligibility(lecturerId);
            this.setVisible(false);


        });


        //Profile button

        JButton  profile_button= new JButton("Profile");
        sidebar.add(profile_button);

        profile_button.setPreferredSize(sidebar_button_size);
        profile_button.setBackground(sidebar_button_color);
        profile_button.setForeground(Color.WHITE);

        URL profile_image_path = getClass().getResource("/images/profile.png");
        ImageIcon profile_icon = new ImageIcon(profile_image_path);
        profile_button.setIcon(profile_icon);

        profile_button.setHorizontalAlignment(SwingConstants.LEFT);
        profile_button.setIconTextGap(15);

        profile_button.addActionListener(e -> {

            new LecturerProfile(lecturerId);
            this.setVisible(false);


        });


        //Student gpa button

        JButton  student_gpa_button= new JButton("Student Marks and GPA");
        sidebar.add(student_gpa_button);

        student_gpa_button.setPreferredSize(sidebar_button_size);
        student_gpa_button.setBackground(sidebar_button_color);
        student_gpa_button.setForeground(Color.WHITE);

        URL student_gpa_path = getClass().getResource("/images/gpa.png");
        ImageIcon student_gpa_icon = new ImageIcon(student_gpa_path);
        student_gpa_button.setIcon(student_gpa_icon);

        student_gpa_button.setHorizontalAlignment(SwingConstants.LEFT);
        student_gpa_button.setIconTextGap(15);

        student_gpa_button.addActionListener(e -> {

            new Lecturer_Student_Marks_GPA(lecturerId);
            this.setVisible(false);


        });


        //student medicle button

        JButton  student_medicle_button= new JButton("Student Medicle");
        sidebar.add(student_medicle_button);

        student_medicle_button.setPreferredSize(sidebar_button_size);
        student_medicle_button.setBackground(sidebar_button_color);
        student_medicle_button.setForeground(Color.WHITE);

        URL student_madicle_path = getClass().getResource("/images/medicle.png");
        ImageIcon student_medicle_icon = new ImageIcon(student_madicle_path);
        student_medicle_button.setIcon(student_medicle_icon);

        student_medicle_button.setHorizontalAlignment(SwingConstants.LEFT);
        student_medicle_button.setIconTextGap(15);

        student_medicle_button.addActionListener(e -> {

            new Lecturer_student_medicle(lecturerId);
            this.setVisible(false);


        });


        //student ditais button

        JButton  student_ditails_button= new JButton("Student Details");
        sidebar.add(student_ditails_button);

        student_ditails_button.setPreferredSize(sidebar_button_size);
        student_ditails_button.setBackground(sidebar_button_color);
        student_ditails_button.setForeground(Color.WHITE);

        URL student_ditails_path = getClass().getResource("/images/ditals.png");
        ImageIcon student_ditails_icon = new ImageIcon(student_ditails_path);
        student_ditails_button.setIcon(student_ditails_icon);

        student_ditails_button.setHorizontalAlignment(SwingConstants.LEFT);
        student_ditails_button.setIconTextGap(15);

        student_ditails_button.addActionListener(e -> {

            new Lecturer_Student_Details(lecturerId);
            this.setVisible(false);


        });


        //student notice button

        JButton notice_button= new JButton("Notice");
        sidebar.add(notice_button);

        notice_button.setPreferredSize(sidebar_button_size);
        notice_button.setBackground(sidebar_button_color);
        notice_button.setForeground(Color.WHITE);

        URL notice_path = getClass().getResource("/images/notice.png");
        ImageIcon notice_icon = new ImageIcon(notice_path);
        notice_button.setIcon(notice_icon);

        notice_button.setHorizontalAlignment(SwingConstants.LEFT);
        notice_button.setIconTextGap(15);

        notice_button.addActionListener(e -> {

            new Lecturer_Notice_View(lecturerId);
            this.setVisible(false);


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

        logout_button.addActionListener(e -> {

            // Open Login Frame
            new LoginFrame().setVisible(true);

            // Close Dashboard
            this.dispose();

        });




    }


}
