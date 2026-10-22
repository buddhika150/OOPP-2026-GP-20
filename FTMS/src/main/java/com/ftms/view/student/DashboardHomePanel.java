package com.ftms.view.student;

import com.ftms.model.Undergraduate;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class DashboardHomePanel extends JPanel {
    private JProgressBar attendanceProgressBar;
    private JLabel lblAttendanceText;
    private JLabel lblWelcome;

    public DashboardHomePanel(Undergraduate currentStudent) {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        JPanel topHeaderPanel = new JPanel(new BorderLayout());
        topHeaderPanel.setBackground(getBackground());

        JPanel titleAndRolePanel = new JPanel(new BorderLayout());
        titleAndRolePanel.setBackground(getBackground());

        JLabel lblTitle = new JLabel("Student Dashboard");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        titleAndRolePanel.add(lblTitle, BorderLayout.WEST);

        JLabel lblRoleBadge = new JLabel("STUDENT", SwingConstants.RIGHT);
        lblRoleBadge.setFont(new Font("Arial", Font.BOLD, 14));
        lblRoleBadge.setForeground(new Color(0, 130, 130));
        titleAndRolePanel.add(lblRoleBadge, BorderLayout.EAST);

        topHeaderPanel.add(titleAndRolePanel, BorderLayout.NORTH);

        JPanel bannerCard = new JPanel(new BorderLayout());
        bannerCard.setBackground(Color.WHITE);
        bannerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JPanel bannerTextPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        bannerTextPanel.setBackground(Color.WHITE);

        String studentName = (currentStudent != null) ? currentStudent.getFname() + " " + currentStudent.getLname() : "Student";
        lblWelcome = new JLabel("Welcome Back, " + studentName + "!");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 22));
        lblWelcome.setForeground(new Color(30, 40, 60));

        JLabel lblSubtitleDesc = new JLabel("Here's an overview of your academic activities and progress.");
        lblSubtitleDesc.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitleDesc.setForeground(new Color(110, 120, 135));

        bannerTextPanel.add(lblWelcome);
        bannerTextPanel.add(lblSubtitleDesc);
        bannerCard.add(bannerTextPanel, BorderLayout.WEST);

        JLabel lblBannerImage = new JLabel();
        try {
            URL imgUrl = getClass().getResource("/images/dashboardimage.png");
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image scaledImg = icon.getImage().getScaledInstance(220, 90, Image.SCALE_SMOOTH);
                lblBannerImage.setIcon(new ImageIcon(scaledImg));
            } else {
                lblBannerImage.setText("[Image Not Found]");
            }
        } catch (Exception e) {
            lblBannerImage.setText("[Image Error]");
        }
        bannerCard.add(lblBannerImage, BorderLayout.EAST);

        JPanel bannerWrapper = new JPanel(new BorderLayout());
        bannerWrapper.setBackground(getBackground());
        bannerWrapper.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        bannerWrapper.add(bannerCard, BorderLayout.CENTER);

        topHeaderPanel.add(bannerWrapper, BorderLayout.CENTER);
        add(topHeaderPanel, BorderLayout.NORTH);

        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        cardsGrid.setBackground(getBackground());

        cardsGrid.add(createDashboardCard("My Courses", "• Software Engineering (SE101)\n• Database Systems (DB201)\n• Data Structures (CS204)\n• Computer Networks (CN301)"));

        JPanel attCard = new JPanel(new BorderLayout(10, 10));
        attCard.setBackground(Color.WHITE);
        attCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel attTitle = new JLabel("Attendance");
        attTitle.setFont(new Font("Arial", Font.BOLD, 15));
        attCard.add(attTitle, BorderLayout.NORTH);

        JPanel attContent = new JPanel(new GridLayout(2, 1, 5, 5));
        attContent.setBackground(Color.WHITE);
        attendanceProgressBar = new JProgressBar(0, 100);
        attendanceProgressBar.setValue(92);
        attendanceProgressBar.setStringPainted(true);
        attendanceProgressBar.setForeground(new Color(0, 150, 150));

        lblAttendanceText = new JLabel("Present: 92%   |   Absent: 8% (Overall)");
        lblAttendanceText.setFont(new Font("Arial", Font.PLAIN, 12));

        attContent.add(lblAttendanceText);
        attContent.add(attendanceProgressBar);
        attCard.add(attContent, BorderLayout.CENTER);
        cardsGrid.add(attCard);

        cardsGrid.add(createDashboardCard("CA Eligibility", "Continuous Assessment Requirement: 40%\n\nStatus: ELIGIBLE\nYour CA Average: Evaluated from Database"));
        cardsGrid.add(createDashboardCard("Grades & GPA", "Current GPA: 3.45\n\nTotal Credits Earned: 72\nAcademic Standing: Good Standing"));

        add(cardsGrid, BorderLayout.CENTER);
    }

    private JPanel createDashboardCard(String title, String content) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblCardTitle = new JLabel(title);
        lblCardTitle.setFont(new Font("Arial", Font.BOLD, 15));
        card.add(lblCardTitle, BorderLayout.NORTH);

        JTextArea area = new JTextArea(content);
        area.setEditable(false);
        area.setBackground(Color.WHITE);
        area.setFont(new Font("Arial", Font.PLAIN, 13));
        area.setForeground(new Color(60, 70, 85));
        card.add(area, BorderLayout.CENTER);

        return card;
    }
}