
package com.ftms.view.student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Locale;

public class EligibilityPanel extends JPanel {

    private final DefaultTableModel eligibilityTableModel;
    private final JPanel cardsGridContainer;

    private static final double CA_MIN = 16.0;
    private static final double CA_MAX = 40.0;
    private static final double ATTENDANCE_MIN = 80.0;

    public EligibilityPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        JLabel lblTitle = new JLabel("Final Exam Eligibility Status");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        add(lblTitle, BorderLayout.NORTH);

        cardsGridContainer = new JPanel(new GridLayout(0, 2, 15, 15));
        cardsGridContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cardsGridContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // Keep the original column order for StudentDashboard compatibility.
        String[] columns = {
                "Course Code",
                "Course Name",
                "CA Marks (Out of 40)",
                "Theory Attendance",
                "Practical Attendance",
                "Requirement",
                "CA Status"
        };

        eligibilityTableModel = new DefaultTableModel(columns, 0);
        eligibilityTableModel.addTableModelListener(e -> updateCardsFromModel());
    }

    private void updateCardsFromModel() {
        cardsGridContainer.removeAll();

        for (int i = 0; i < eligibilityTableModel.getRowCount(); i++) {
            String code = value(i, 0);
            String name = value(i, 1);
            String marks = value(i, 2);
            String theory = value(i, 3);
            String practical = value(i, 4);

            cardsGridContainer.add(
                    createEligibilityCard(code, name, marks, theory, practical)
            );
        }

        cardsGridContainer.revalidate();
        cardsGridContainer.repaint();
    }

    private String value(int row, int column) {
        Object obj = eligibilityTableModel.getValueAt(row, column);
        return obj == null ? "" : obj.toString().trim();
    }

    private Double number(String raw) {
        if (raw == null) {
            return null;
        }

        String s = raw.trim().replace("%", "").replace(",", "");

        if (s.isEmpty()
                || s.equalsIgnoreCase("null")
                || s.equals("--")
                || s.equalsIgnoreCase("N/A")
                || s.equalsIgnoreCase("pending")) {
            return null;
        }

        try {
            double n = Double.parseDouble(s);
            return Double.isFinite(n) ? n : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String finalExamStatus(Double ca, Double theory, Double practical) {
        if ((ca != null && ca < CA_MIN)
                || (theory != null && theory < ATTENDANCE_MIN)
                || (practical != null && practical < ATTENDANCE_MIN)) {
            return "NOT ELIGIBLE";
        }

        if (ca == null || theory == null || practical == null) {
            return "PENDING";
        }

        return "ELIGIBLE";
    }

    private JLabel styledLabel(String text, int size, boolean bold, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(
                "Segoe UI",
                bold ? Font.BOLD : Font.PLAIN,
                size
        ));
        label.setForeground(color);
        return label;
    }

    private JLabel statusBadge(String status) {
        JLabel badge = styledLabel(
                status,
                11,
                true,
                "ELIGIBLE".equals(status)
                        ? new Color(22, 101, 52)
                        : "PENDING".equals(status)
                        ? new Color(146, 64, 14)
                        : new Color(153, 27, 27)
        );

        Color background = "ELIGIBLE".equals(status)
                ? new Color(220, 252, 231)
                : "PENDING".equals(status)
                ? new Color(254, 249, 195)
                : new Color(254, 226, 226);

        badge.setOpaque(true);
        badge.setBackground(background);
        badge.setBorder(
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        );

        return badge;
    }

    private JPanel attendanceRow(String title, String attendance) {
        JPanel row = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 2)
        );

        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(styledLabel(
                title + ":",
                12,
                true,
                new Color(30, 41, 59)
        ));

        row.add(styledLabel(
                attendance.isEmpty() ? "--" : attendance,
                12,
                false,
                new Color(100, 116, 139)
        ));

        return row;
    }

    private JPanel createEligibilityCard(
            String code,
            String name,
            String marks,
            String theory,
            String practical
    ) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(226, 232, 240),
                        1,
                        true
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        Double ca = number(marks);
        Double theoryPct = number(theory);
        Double practicalPct = number(practical);

        String overall = finalExamStatus(
                ca,
                theoryPct,
                practicalPct
        );

        // Course header
        JPanel topHeader = new JPanel(new BorderLayout(8, 0));
        topHeader.setOpaque(false);

        JLabel lblCourse = styledLabel(
                code + " - " + name,
                14,
                true,
                new Color(15, 23, 42)
        );

        topHeader.add(lblCourse, BorderLayout.CENTER);

        // Center content
        JPanel centerPanel = new JPanel(
                new GridLayout(1, 2, 10, 10)
        );

        centerPanel.setOpaque(false);
        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 0, 10, 0)
        );

        String markText = ca == null
                ? "--"
                : String.format(Locale.US, "%.2f", ca);

        // CA Marks label and large mark value
        JPanel marksPanel = new JPanel();
        marksPanel.setLayout(
                new BoxLayout(marksPanel, BoxLayout.Y_AXIS)
        );
        marksPanel.setOpaque(false);

        JLabel lblCaTitle = styledLabel(
                "CA Marks",
                14,
                true,
                new Color(100, 116, 139)
        );

        JLabel lblMarks = styledLabel(
                markText,
                32,
                true,
                new Color(15, 23, 42)
        );

        marksPanel.add(Box.createVerticalGlue());
        marksPanel.add(lblCaTitle);
        marksPanel.add(Box.createVerticalStrut(8));
        marksPanel.add(lblMarks);
        marksPanel.add(Box.createVerticalGlue());

        centerPanel.add(marksPanel);

        // CA details and attendance
        JPanel details = new JPanel();
        details.setLayout(
                new BoxLayout(details, BoxLayout.Y_AXIS)
        );
        details.setOpaque(false);

        details.add(Box.createVerticalGlue());

        details.add(styledLabel(
                "Obtained: " + markText + " / 40.0",
                12,
                false,
                new Color(100, 116, 139)
        ));

        details.add(styledLabel(
                ca == null
                        ? "(--%)"
                        : String.format(
                        Locale.US,
                        "(%.2f%%)",
                        ca / CA_MAX * 100
                ),
                12,
                false,
                new Color(100, 116, 139)
        ));

        details.add(Box.createVerticalStrut(8));

        details.add(attendanceRow(
                "Theory Attendance",
                theory
        ));

        details.add(Box.createVerticalStrut(5));

        details.add(attendanceRow(
                "Practical Attendance",
                practical
        ));

        details.add(Box.createVerticalGlue());

        centerPanel.add(details);

        // Bottom requirement and final eligibility
        JPanel bottom = new JPanel(
                new BorderLayout(0, 9)
        );
        bottom.setOpaque(false);

        JLabel lblReq = styledLabel(
                "Requirement: CA >= 16/40; Theory >= 80%; Practical >= 80%",
                12,
                false,
                new Color(100, 116, 139)
        );

        JPanel result = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 3)
        );
        result.setOpaque(false);

        result.add(styledLabel(
                "FINAL EXAM ELIGIBILITY:",
                14,
                true,
                new Color(15, 23, 42)
        ));

        result.add(statusBadge(overall));

        bottom.add(lblReq, BorderLayout.NORTH);
        bottom.add(result, BorderLayout.SOUTH);

        card.add(topHeader, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    public DefaultTableModel getEligibilityTableModel() {
        return eligibilityTableModel;
    }
}
