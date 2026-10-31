package com.ftms.view.lecturer;

import com.ftms.database.DatabaseConnection;
import com.ftms.model.Add_course_material;
import com.ftms.model.Lecturer;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

import javax.swing.*;



public class LecturerAttendanceSummary extends JFrame {

    JPanel main_panel = new JPanel(new BorderLayout(10, 10));

    JPanel top_panel = new JPanel(new BorderLayout(10, 10));

    JComboBox<String> course_box;
    DefaultTableModel model;

    private int lectureId;


    public LecturerAttendanceSummary(int lecturerId) {

        this.lectureId = lecturerId;

        setupwindow();
        create_heading();
        CreaterSidebar();
        // create_upload_button();
        create_courese_select();
        create_tabel();


        this.add(main_panel, BorderLayout.CENTER);
        main_panel.add(top_panel, BorderLayout.NORTH);

        load_course(lecturerId);
        setVisible(true);


    }

    private void setupwindow() {

        setTitle("LecturerAttendanceSummary");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 249, 252));


    }


    private void create_heading() {

        JPanel heading_panel = new JPanel(new BorderLayout());
        Dimension heading_size = new Dimension(500, 200);
        heading_panel.setPreferredSize(heading_size);
        heading_panel.setBackground(Color.WHITE);


        JLabel heading_label = new JLabel("Attendance Summary");
        heading_label.setFont(new Font("arial", Font.BOLD, 50));
        heading_panel.add(heading_label, BorderLayout.WEST);
        top_panel.add(heading_panel, BorderLayout.NORTH);


        URL course_material_imge_path = getClass().getResource("/images/attendance_icon.png");
        ImageIcon heading_image = new ImageIcon(course_material_imge_path);
        JLabel image_label = new JLabel(heading_image);
        heading_panel.add(image_label, BorderLayout.EAST);


    }


    private void create_courese_select() {


        JPanel courese_panel = new JPanel((new FlowLayout(FlowLayout.LEFT, 40, 40)));
        courese_panel.setPreferredSize(new Dimension(0, 110));
        courese_panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Select Course:");
        label.setFont(new Font("Arial", Font.BOLD, 18));

        course_box = new JComboBox<>();
        course_box.setPreferredSize(new Dimension(300, 40));
        course_box.setBackground(Color.WHITE);
        course_box.setForeground(new Color(35, 45, 60));
        course_box.setBorder(BorderFactory.createLineBorder(new Color(210, 220, 230), 1));

        courese_panel.add(label);
        courese_panel.add(course_box);

        course_box.addActionListener(e -> {

            String course_code = (String) course_box.getSelectedItem();

            if (course_code != null) {

                loadAttendance(course_code);

            }

        });
        courese_panel.add(course_box);
        top_panel.add(courese_panel, BorderLayout.SOUTH);


    }


    private void load_course(int lecrureId) {

        try {

            Connection con = DatabaseConnection.getConnection();
            String sql = "SELECT Course_code FROM lecturer_course WHERE Lecturer_ID = ?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, lecrureId);

            ResultSet rs = pst.executeQuery();

            course_box.removeAllItems();

            while (rs.next()) {
                course_box.addItem(rs.getString("course_code"));
            }

            rs.close();
            pst.close();
            con.close();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this, ex.getMessage());

        }
    }


    private void create_tabel() {

        String[] colums = {
                "Student ID",
                "Total Classes",
                "Present",
                "Absent",
                "Percentage"
        };

        model = new DefaultTableModel(colums, 0);

        JTable tabel = new JTable(model);

        tabel.setRowHeight(35);
        tabel.setFont(new Font("Arial", Font.BOLD, 15));

        tabel.getTableHeader().setFont(new Font("arial", Font.BOLD, 15));

        JScrollPane can_scroll_tabel = new JScrollPane(tabel);

        main_panel.add(can_scroll_tabel, BorderLayout.CENTER);


    }

    private void loadAttendance(String course_code) {

        System.out.println("Selected Course: " + course_code);

        model.setRowCount(0);

        String sql = "SELECT e.Student_ID, " +
                "COUNT(a.Attendance_ID) AS Total, " +
                "COALESCE(SUM(CASE WHEN a.Is_Present = 1 THEN 1 ELSE 0 END), 0) AS Present " +
                "FROM enrollment e " +
                "LEFT JOIN attendance a ON e.Student_ID = a.Student_ID " +
                "AND e.Course_code = a.Course_code " +
                "WHERE e.Course_code = ? " +
                "GROUP BY e.Student_ID";

        try {

            Connection con = DatabaseConnection.getConnection();


            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, course_code);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                int student_id = rs.getInt("Student_ID");
                int total = rs.getInt("Total");
                int present = rs.getInt("Present");

                int absent = total - present;

                double percentage = 0;

                if (total > 0) {
                    percentage = (present * 100.0) / total;
                }

                model.addRow(new Object[]{
                        student_id,
                        total,
                        present,
                        absent,
                        String.format("%.2f%%", percentage)
                });
            }

            rs.close();
            pst.close();
            con.close();


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //sidebar

    /// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void CreaterSidebar() {

        JPanel sidebar = new JPanel();
        Color sidebar_color = new Color(16, 45, 79);

        sidebar.setBackground(sidebar_color);

        this.add(sidebar, BorderLayout.WEST);


        // FTMS text

        JLabel sidebar_headr_text = new JLabel("FTMS");
        sidebar_headr_text.setForeground(Color.WHITE);
        sidebar_headr_text.setFont(new Font("Arial", Font.BOLD, 40));
        sidebar.add(sidebar_headr_text);


        //FACULTY OF TECHNOLOGY
        //MANAGEMENT SYSTEM   text

        JLabel sidebar_dear_text_2 = new JLabel("FACULTY OF TECHNOLOGY");
        sidebar_dear_text_2.setForeground(Color.WHITE);
        sidebar_dear_text_2.setFont(new Font("Arial", Font.BOLD, 15));
        sidebar.add(sidebar_dear_text_2);


        JLabel sidebar_dear_text_3 = new JLabel("MANAGEMENT SYSTEM");
        sidebar_dear_text_3.setForeground(Color.WHITE);
        sidebar_dear_text_3.setFont(new Font("Arial", Font.BOLD, 15));
        sidebar.add(sidebar_dear_text_3);


        //side bar button common variable

        Dimension sidebar_size = new Dimension(250, 500);
        sidebar.setPreferredSize(sidebar_size);


        Color sidebar_button_color = new Color(0, 143, 156);
        Dimension sidebar_button_size = new Dimension(215, 40);

        //Dashbord
        JButton dashbord_button = new JButton("Dashbord");
        dashbord_button.setFont(new Font("Arial", Font.BOLD, 15));

        sidebar.add(dashbord_button);

        dashbord_button.setBackground(sidebar_button_color);
        dashbord_button.setForeground(Color.WHITE);

        dashbord_button.setPreferredSize(sidebar_button_size);

        dashbord_button.addActionListener(e -> {
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