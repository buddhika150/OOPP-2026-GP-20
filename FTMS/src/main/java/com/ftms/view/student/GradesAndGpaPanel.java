package com.ftms.view.student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GradesAndGpaPanel extends JPanel {
    private DefaultTableModel marksTableModel;
    private JLabel lblStudentSummary, lblSgpa, lblCgpa, lblCaStatus;
    private JLabel lblTotalCourses, lblTotalMarks, lblPercentage;

    public GradesAndGpaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        JPanel topContainer = new JPanel(new BorderLayout(5, 10));
        topContainer.setBackground(getBackground());

        JPanel titleRolePanel = new JPanel(new BorderLayout());
        titleRolePanel.setBackground(getBackground());
        JLabel lblTitle = new JLabel("My Grades & GPA");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        titleRolePanel.add(lblTitle, BorderLayout.WEST);

        JLabel lblRole = new JLabel("STUDENT", SwingConstants.RIGHT);
        lblRole.setFont(new Font("Arial", Font.BOLD, 14));
        lblRole.setForeground(new Color(0, 130, 130));
        titleRolePanel.add(lblRole, BorderLayout.EAST);
        topContainer.add(titleRolePanel, BorderLayout.NORTH);

        lblStudentSummary = new JLabel("Student ID: Loading... | Program: Loading... | Batch: Loading... | Semester: Loading... | Academic Year: Loading...");
        lblStudentSummary.setFont(new Font("Arial", Font.PLAIN, 12));
        lblStudentSummary.setOpaque(true);
        lblStudentSummary.setBackground(Color.WHITE);
        lblStudentSummary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        topContainer.add(lblStudentSummary, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(getBackground());

        JPanel gpaCardsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        gpaCardsPanel.setBackground(getBackground());

        lblSgpa = new JLabel("0.00", SwingConstants.CENTER);
        lblSgpa.setFont(new Font("Arial", Font.BOLD, 26));
        lblSgpa.setForeground(new Color(0, 130, 130));

        lblCgpa = new JLabel("0.00", SwingConstants.CENTER);
        lblCgpa.setFont(new Font("Arial", Font.BOLD, 26));
        lblCgpa.setForeground(new Color(0, 130, 130));

        lblCaStatus = new JLabel("Loading...", SwingConstants.CENTER);
        lblCaStatus.setFont(new Font("Arial", Font.BOLD, 20));
        lblCaStatus.setForeground(new Color(0, 130, 130));

        gpaCardsPanel.add(wrapInCard(lblSgpa, "SGPA (Semester)", "Out of 4.00"));
        gpaCardsPanel.add(wrapInCard(lblCgpa, "CGPA (Cumulative)", "Out of 4.00"));
        gpaCardsPanel.add(wrapInCard(lblCaStatus, "CA Eligibility", "CA >= 40%"));

        centerPanel.add(gpaCardsPanel, BorderLayout.NORTH);

        String[] columns = {"Course Code", "Course Name", "CA Marks (100)", "Final Marks (60)", "Total (100)", "Grade"};
        marksTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(marksTableModel);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));

        lblTotalCourses = new JLabel("Total Courses: 0");
        lblTotalMarks = new JLabel("Total Marks: 0/0");
        lblPercentage = new JLabel("Percentage: 0.00%");

        lblTotalCourses.setFont(new Font("Arial", Font.BOLD, 13));
        lblTotalMarks.setFont(new Font("Arial", Font.BOLD, 13));
        lblPercentage.setFont(new Font("Arial", Font.BOLD, 13));

        bottomBar.add(lblTotalCourses);
        bottomBar.add(lblTotalMarks);
        bottomBar.add(lblPercentage);

        add(bottomBar, BorderLayout.SOUTH);
    }

    private JPanel wrapInCard(JLabel label, String title, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 13));
        lblTitle.setForeground(new Color(80, 90, 105));
        card.add(lblTitle, BorderLayout.NORTH);

        card.add(label, BorderLayout.CENTER);

        JLabel lblSub = new JLabel(subtitle, SwingConstants.CENTER);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 11));
        lblSub.setForeground(new Color(130, 140, 155));
        card.add(lblSub, BorderLayout.SOUTH);

        return card;
    }

    public DefaultTableModel getMarksTableModel() { return marksTableModel; }
    public JLabel getLblStudentSummary() { return lblStudentSummary; }
    public JLabel getLblSgpa() { return lblSgpa; }
    public JLabel getLblCgpa() { return lblCgpa; }
    public JLabel getLblCaStatus() { return lblCaStatus; }
    public JLabel getLblTotalCourses() { return lblTotalCourses; }
    public JLabel getLblTotalMarks() { return lblTotalMarks; }
    public JLabel getLblPercentage() { return lblPercentage; }
}