


package com.ftms.view.lecturer;

import com.ftms.database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UploadMarks extends JFrame {

    private int lecturerId;

    private JComboBox<String> course_box;
    private JTable marks_table;
    private DefaultTableModel model;

    public UploadMarks(int lecturerId) {

        this.lecturerId = lecturerId;

        setupWindow();
        createHeading();
        createCourseSelect();
        createTable();
        createSaveButton();
        loadCourses();

        setVisible(true);
    }

    private void setupWindow() {

        setTitle("Upload Marks");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        JPanel center_panel = new JPanel(new BorderLayout(10, 10));
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

        JPanel center_panel = (JPanel) getContentPane().getComponent(1);

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

    private void saveMarks() {

        String courseCode = (String) course_box.getSelectedItem();



        String sql =
                "INSERT INTO marks " +
                        "(Student_ID, Course_code, CA_marks, Final_exam_marks) " +
                        "VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "CA_marks = VALUES(CA_marks), " +
                        "Final_exam_marks = VALUES(Final_exam_marks), " +
                        "Total_marks = NULL, Grade = NULL";

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

                    pst.setInt(1, studentId);
                    pst.setString(2, courseCode);
                    pst.setDouble(3, values[i][0]);
                    pst.setDouble(4, values[i][1]);

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
}
