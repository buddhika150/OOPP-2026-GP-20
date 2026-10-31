package com.ftms.view.student;

import com.ftms.util.SessionManager;
import com.ftms.view.Login;
import javax.swing.*;
import java.awt.*;

public class SidebarPanel extends JPanel {
    public SidebarPanel(CardLayout cardLayout, JPanel mainContentPanel, JFrame parentFrame, ProfilePanel profilePanel, Runnable refreshDataCallback) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(20, 35, 60));
        setPreferredSize(new Dimension(220, 700));

        JLabel lblTitle = new JLabel("  FTMS");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(25, 10, 5, 10));
        add(lblTitle);

        JLabel lblSubtitle = new JLabel("  FACULTY MANAGEMENT");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 10));
        lblSubtitle.setForeground(new Color(170, 185, 205));
        lblSubtitle.setBorder(BorderFactory.createEmptyBorder(0, 12, 25, 10));
        add(lblSubtitle);

        JButton btnDashboard = createSidebarButton("Dashboard");
        JButton btnProfile = createSidebarButton("My Profile");
        JButton btnCourses = createSidebarButton("My Courses");
        JButton btnAttendance = createSidebarButton("My Attendance");
        JButton btnEligibility = createSidebarButton("My Eligibility");
        JButton btnGrades = createSidebarButton("My Grades & GPA");
        JButton btnLogout = createSidebarButton("logout");

        btnDashboard.addActionListener(e -> cardLayout.show(mainContentPanel, "Dashboard"));
        btnProfile.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "Profile");
            profilePanel.updateProfileData();
        });
        btnCourses.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "Courses");
            refreshDataCallback.run();
        });
        btnAttendance.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "Attendance");
            refreshDataCallback.run();
        });
        btnEligibility.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "Eligibility");
            refreshDataCallback.run();
        });
        btnGrades.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "Grades");
            refreshDataCallback.run();
        });
        btnLogout.addActionListener(e -> {
            SessionManager.getInstance().logout();
            parentFrame.dispose();
            new Login().setVisible(true);
        });

        add(btnDashboard);
        add(btnProfile);
        add(btnCourses);
        add(btnAttendance);
        add(btnEligibility);
        add(btnGrades);
        add(Box.createVerticalGlue());
        add(btnLogout);
        add(Box.createRigidArea(new Dimension(0, 20)));
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 42));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBackground(new Color(30, 50, 85));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        return button;
    }
}