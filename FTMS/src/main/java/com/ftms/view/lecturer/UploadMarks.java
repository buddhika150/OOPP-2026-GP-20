


package com.ftms.view.lecturer;

import com.ftms.database.DatabaseConnection;
import com.ftms.view.LoginFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.sql.*;

public class UploadMarks extends JFrame {

    private int lecturerId;

    private JComboBox<String> course_box;
    private JTable marks_table;
    private DefaultTableModel model;
    private JPanel center_panel;

    public UploadMarks(int lecturerId) {

        this.lecturerId = lecturerId;

        setupWindow();
        CreaterSidebar();
        createHeading();
        createCourseSelect();
        createTable();
        createSaveButton();
        loadCourses();

        setVisible(true);
    }

    private void setupWindow() {

        setTitle("Upload Marks");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        getContentPane().setBackground(Color.WHITE);
    }

    private void createHeading() {

        JLabel heading = new JLabel("Upload Student Marks");

        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20)
        );

        add(heading, BorderLayout.NORTH);
    }

    private void createCourseSelect() {

        JPanel course_panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        course_panel.setBackground(Color.WHITE);

        JLabel course_label = new JLabel("Select Course:");
        course_label.setFont(new Font("Arial", Font.BOLD, 17));

        course_box = new JComboBox<>();
        course_box.setPreferredSize(new Dimension(250, 35));

        course_panel.add(course_label);
        course_panel.add(course_box);

        center_panel = new JPanel(new BorderLayout(10, 10));
        center_panel.setBackground(Color.WHITE);

        center_panel.add(course_panel, BorderLayout.NORTH);
        add(center_panel, BorderLayout.CENTER);

        course_box.addActionListener(e -> {

            String courseCode = (String) course_box.getSelectedItem();


                loadStudents(courseCode);

        });
    }

    private void createTable() {

        String[] columns = {
                "Student ID",
                "CA Marks /40",
                "Final Exam /60"
        };

        model = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {    //select can edit colum
                return column == 1 || column == 2;
            }
        };

        marks_table = new JTable(model);

        marks_table.setRowHeight(35);
        marks_table.setFont(new Font("Arial", Font.PLAIN, 15));

        marks_table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        center_panel.add(new JScrollPane(marks_table), BorderLayout.CENTER
        );
    }

    private void createSaveButton() {

        JButton save_button = new JButton("Save Marks");

        save_button.setFont(new Font("Arial", Font.BOLD, 18));
        save_button.setBackground(new Color(0, 143, 156));
        save_button.setForeground(Color.WHITE);

        JPanel bottom_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        bottom_panel.setBackground(Color.WHITE);

        bottom_panel.add(save_button);

        add(bottom_panel, BorderLayout.SOUTH);

        save_button.addActionListener(e -> saveMarks());
    }

    private void loadCourses() {

        String sql =
                "SELECT DISTINCT Course_code " + "FROM lecturer_course " + "WHERE Lecturer_ID = ? " + "ORDER BY Course_code";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, lecturerId);

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                    course_box.addItem(rs.getString("Course_code"));
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void loadStudents(String courseCode) {

        model.setRowCount(0);

        String sql =
                "SELECT e.Student_ID, m.CA_marks, m.Final_exam_marks " +
                        "FROM enrollment e " +
                        "LEFT JOIN marks m " +
                        "ON e.Student_ID = m.Student_ID " +
                        "AND e.Course_code = m.Course_code " +
                        "WHERE e.Course_code = ? " +
                        "AND EXISTS (" +
                        "SELECT 1 FROM lecturer_course lc " +
                        "WHERE lc.Course_code = e.Course_code " +
                        "AND lc.Lecturer_ID = ?) " +
                        "ORDER BY e.Student_ID";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, courseCode);
            pst.setInt(2, lecturerId);

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {

                    Object ca = rs.getObject("CA_marks");
                    Object exam = rs.getObject("Final_exam_marks");

                    model.addRow(new Object[]{
                            rs.getInt("Student_ID"),
                            ca == null ? "" : ca,
                            exam == null ? "" : exam
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private String calculateGrade(double total) {

        if (total >= 85) return "A+";
        else if (total >= 70) return "A";
        else if (total >= 65) return "A-";
        else if (total >= 60) return "B+";
        else if (total >= 55) return "B";
        else if (total >= 50) return "B-";
        else if (total >= 45) return "C+";
        else if (total >= 40) return "C";
        else if (total >= 35) return "C-";
        else if (total >= 30) return "D+";
        else if (total >= 25) return "D";
        else return "E";
    }

    private void saveMarks() {

        String courseCode = (String) course_box.getSelectedItem();



        String sql =
                "INSERT INTO marks " +
                        "(Student_ID, Course_code, CA_marks, Final_exam_marks, Total_marks, Grade) " +
                        "VALUES (?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "CA_marks = VALUES(CA_marks), " +
                        "Final_exam_marks = VALUES(Final_exam_marks), " +
                        "Total_marks = VALUES(Total_marks), " +
                        "Grade = VALUES(Grade)";


        String checkSql =
                "SELECT 1 FROM lecturer_course " +
                        "WHERE Lecturer_ID = ? AND Course_code = ?";

        try (Connection con = DatabaseConnection.getConnection()) {

            try (PreparedStatement check = con.prepareStatement(checkSql)) {

                check.setInt(1, lecturerId);
                check.setString(2, courseCode);

                try (ResultSet rs = check.executeQuery()) {

                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(
                                this, "Course is not assigned to this lecturer"
                        );
                        return;
                    }
                }
            }

            double[][] values = new double[model.getRowCount()][2];

            for (int i = 0; i < model.getRowCount(); i++) {

                String caText = String.valueOf(
                        model.getValueAt(i, 1)
                ).trim();

                String finalText = String.valueOf(
                        model.getValueAt(i, 2)
                ).trim();

                if (caText.isEmpty() || finalText.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Enter both marks for Student " +
                                    model.getValueAt(i, 0)
                    );
                    return;
                }

                double ca;
                double finalMark;

                try {
                    ca = Double.parseDouble(caText);
                    finalMark = Double.parseDouble(finalText);

                } catch (NumberFormatException ex) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Enter valid numbers for Student " +
                                    model.getValueAt(i, 0)
                    );
                    return;
                }

                if (!Double.isFinite(ca) ||
                        !Double.isFinite(finalMark) ||
                        ca < 0 || ca > 40 ||
                        finalMark < 0 || finalMark > 60) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Invalid marks for Student " +
                                    model.getValueAt(i, 0)
                    );
                    return;
                }

                values[i][0] = ca;
                values[i][1] = finalMark;
            }

            con.setAutoCommit(false);

            try (PreparedStatement pst = con.prepareStatement(sql)) {

                for (int i = 0; i < model.getRowCount(); i++) {

                    int studentId = Integer.parseInt(
                            model.getValueAt(i, 0).toString()
                    );

                    double ca = values[i][0];
                    double exam = values[i][1];

                    double total = ca + exam;
                    String grade = calculateGrade(total);

                    pst.setInt(1, studentId);
                    pst.setString(2, courseCode);
                    pst.setDouble(3, ca);
                    pst.setDouble(4, exam);
                    pst.setDouble(5, total);
                    pst.setString(6, grade);

                    pst.addBatch();
                }

                pst.executeBatch();
                con.commit();

                JOptionPane.showMessageDialog(
                        this, "Marks Saved Successfully!"
                );

                loadStudents(courseCode);

            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
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
