package com.ftms.view.student;

import com.ftms.model.Undergraduate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfilePanel extends JPanel {
    private Undergraduate currentStudent;
    private JLabel lblHeaderName, lblHeaderTg;
    private JLabel lblProfName, lblProfTg, lblProfDept, lblProfBatch, lblProfNic, lblProfEmail, lblProfPhone, lblProfStatus, lblProfYearSem;

    public ProfilePanel(Undergraduate currentStudent) {
        this.currentStudent = currentStudent;
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(25, 30, 25, 30));
        setBackground(new Color(240, 242, 245));

        // Title section
        JLabel lblTitle = new JLabel("My Profile Details");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 60, 75));
        add(lblTitle, BorderLayout.NORTH);

        // Main Card Panel
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(new Color(248, 249, 250));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 235), 1, true),
                new EmptyBorder(25, 30, 25, 30)
        ));

        // Top Header Section (Name and ID)
        JPanel topHeader = new JPanel();
        topHeader.setLayout(new BoxLayout(topHeader, BoxLayout.Y_AXIS));
        topHeader.setOpaque(false);
        topHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblHeaderName = new JLabel("-", SwingConstants.CENTER);
        lblHeaderName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHeaderName.setForeground(new Color(25, 30, 45));
        lblHeaderName.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblHeaderTg = new JLabel("Student ID: -", SwingConstants.CENTER);
        lblHeaderTg.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblHeaderTg.setForeground(new Color(100, 110, 125));
        lblHeaderTg.setAlignmentX(Component.CENTER_ALIGNMENT);

        topHeader.add(lblHeaderName);
        topHeader.add(Box.createRigidArea(new Dimension(0, 5)));
        topHeader.add(lblHeaderTg);

        cardPanel.add(topHeader);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Details Grid Panel
        JPanel detailsGrid = new JPanel(new GridBagLayout());
        detailsGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(8, 10, 8, 10);

        lblProfName = new JLabel("-");
        lblProfTg = new JLabel("-");
        lblProfDept = new JLabel("-");
        lblProfBatch = new JLabel("-");
        lblProfNic = new JLabel("-");
        lblProfEmail = new JLabel("-");
        lblProfPhone = new JLabel("-");
        lblProfStatus = createStatusLabel("-");
        lblProfYearSem = new JLabel("-");

        // Styling value labels
        Font valFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color valColor = new Color(40, 45, 55);

        JLabel[] valLabels = {lblProfName, lblProfTg, lblProfDept, lblProfBatch, lblProfNic, lblProfEmail, lblProfPhone, lblProfYearSem};
        for (JLabel l : valLabels) {
            l.setFont(valFont);
            l.setForeground(valColor);
        }

        // Left Column Items
        addGridRow(detailsGrid, gbc, 0, 0, "Full Name:", lblProfName);
        addGridRow(detailsGrid, gbc, 0, 1, "Department / Program:", lblProfDept);
        addGridRow(detailsGrid, gbc, 0, 2, "Batch:", lblProfBatch);
        addGridRow(detailsGrid, gbc, 0, 3, "NIC:", lblProfNic);

        // Right Column Items
        addGridRow(detailsGrid, gbc, 1, 0, "TG Number / ID:", lblProfTg);
        addGridRow(detailsGrid, gbc, 1, 1, "Email:", lblProfEmail);
        addGridRow(detailsGrid, gbc, 1, 2, "Phone:", lblProfPhone);
        addGridRow(detailsGrid, gbc, 1, 3, "Status:", lblProfStatus);
        addGridRow(detailsGrid, gbc, 1, 4, "Year & Semester:", lblProfYearSem);

        cardPanel.add(detailsGrid);

        // Wrapper Panel for centering layout
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(cardPanel, BorderLayout.NORTH);

        add(centerWrapper, BorderLayout.CENTER);

        updateProfileData();
    }

    private void addGridRow(JPanel panel, GridBagConstraints gbc, int col, int row, String labelText, Component valComp) {
        gbc.gridx = col * 2;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 70, 85));
        panel.add(label, gbc);

        gbc.gridx = col * 2 + 1;
        gbc.weightx = 0.8;
        panel.add(valComp, gbc);
    }

    private JLabel createStatusLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 245, 230)); // light green badge background
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(40, 140, 75)); // dark green text
        label.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        label.setOpaque(false);
        return label;
    }

    public void updateProfileData() {
        if (currentStudent == null) return;

        String fullName = currentStudent.getFname() + " " + currentStudent.getLname();
        String tgNum = currentStudent.getTgNum();

        lblHeaderName.setText(fullName);
        lblHeaderTg.setText("Student ID: " + tgNum);

        lblProfName.setText(fullName);
        lblProfTg.setText(tgNum);
        lblProfDept.setText(currentStudent.getDepartment());
        lblProfBatch.setText(currentStudent.getBatch());
        lblProfNic.setText(currentStudent.getNic());
        lblProfEmail.setText(currentStudent.getEmail());
        lblProfPhone.setText(currentStudent.getPhone());

        String statusText = currentStudent.getStatus() != null ? currentStudent.getStatus().toUpperCase() : "-";
        lblProfStatus.setText("● " + statusText);

        lblProfYearSem.setText("Year " + currentStudent.getYear() + " - Semester " + currentStudent.getSemester());
    }
}