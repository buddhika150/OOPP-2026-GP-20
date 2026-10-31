package com.ftms.view.student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EligibilityPanel extends JPanel {
    private DefaultTableModel eligibilityTableModel;
    private JPanel cardsGridContainer;

    public EligibilityPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        // Title
        JLabel lblTitle = new JLabel("Continuous Assessment (CA) Eligibility Status");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        add(lblTitle, BorderLayout.NORTH);

        // Cards Grid Container
        cardsGridContainer = new JPanel(new GridLayout(0, 2, 15, 15));
        cardsGridContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cardsGridContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // Model Setup
        String[] columns = {"Course Code", "Course Name", "CA Marks (Out of 100)", "Requirement", "CA Status"};
        eligibilityTableModel = new DefaultTableModel(columns, 0);

        // Update cards dynamically when table model changes
        eligibilityTableModel.addTableModelListener(e -> updateCardsFromModel());
    }

    private void updateCardsFromModel() {
        cardsGridContainer.removeAll();
        for (int i = 0; i < eligibilityTableModel.getRowCount(); i++) {
            String code = String.valueOf(eligibilityTableModel.getValueAt(i, 0));
            String name = String.valueOf(eligibilityTableModel.getValueAt(i, 1));
            String marks = String.valueOf(eligibilityTableModel.getValueAt(i, 2));
            String req = String.valueOf(eligibilityTableModel.getValueAt(i, 3));
            String status = String.valueOf(eligibilityTableModel.getValueAt(i, 4));

            cardsGridContainer.add(createEligibilityCard(code, name, marks, req, status));
        }
        cardsGridContainer.revalidate();
        cardsGridContainer.repaint();
    }

    private JPanel createEligibilityCard(String code, String name, String marks, String req, String status) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Top Header (Course & Status Badge)
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JLabel lblCourse = new JLabel(code + " - " + name);
        lblCourse.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCourse.setForeground(new Color(15, 23, 42));

        boolean isEligible = status != null && status.toLowerCase().contains("eligible") && !status.toLowerCase().contains("not");
        JLabel badge = new JLabel(status.toUpperCase());
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        if (isEligible) {
            badge.setForeground(new Color(22, 101, 52));
            badge.setBackground(new Color(220, 252, 231));
        } else {
            badge.setForeground(new Color(153, 27, 27));
            badge.setBackground(new Color(254, 226, 226));
        }
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isEligible ? new Color(187, 247, 208) : new Color(254, 202, 202), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(badge);

        topHeader.add(lblCourse, BorderLayout.CENTER);
        topHeader.add(badgeWrapper, BorderLayout.EAST);

        // Center Content (Big Marks + Details)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel lblMarks = new JLabel(marks);
        lblMarks.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblMarks.setForeground(new Color(15, 23, 42));

        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setOpaque(false);

        JLabel lblObtained = new JLabel("Obtained: " + marks + " / 100.0");
        lblObtained.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblObtained.setForeground(new Color(100, 116, 139));

        String percentageText;
        try {
            double caMarks = Double.parseDouble(marks.trim());
            percentageText = String.format("(%.2f%%)", caMarks);
        } catch (NumberFormatException ex) {
            percentageText = "(--%)";
        }
        JLabel lblPct = new JLabel(percentageText);
        lblPct.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPct.setForeground(new Color(100, 116, 139));

        details.add(Box.createVerticalGlue());
        details.add(lblObtained);
        details.add(lblPct);
        details.add(Box.createVerticalGlue());

        centerPanel.add(lblMarks);
        centerPanel.add(details);

        // Bottom Requirement
        JLabel lblReq = new JLabel("Requirement: " + req);
        lblReq.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblReq.setForeground(new Color(100, 116, 139));

        card.add(topHeader, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(lblReq, BorderLayout.SOUTH);

        return card;
    }

    public DefaultTableModel getEligibilityTableModel() {
        return eligibilityTableModel;
    }
}