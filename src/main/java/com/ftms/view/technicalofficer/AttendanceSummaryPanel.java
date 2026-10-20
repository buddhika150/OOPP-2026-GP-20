package com.ftms.view.technicalofficer;

import com.ftms.dao.AttendanceDAO;
import com.ftms.dao.AttendanceDAO.AttendanceSummary;
import com.ftms.model.Attendance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * AttendanceSummaryPanel - Displays individual and whole-batch attendance summaries.
 * Features Theory vs Practical attendance breakdown, percentage calculation,
 * and 80% exam eligibility evaluation in accordance with SRS requirements.
 */
public class AttendanceSummaryPanel extends JPanel {

    // Theme Palette matching reference screenshot
    private static final Color BG_PAGE = new Color(0xF4, 0xF7, 0xFB);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xE2, 0xE8, 0xF0);
    private static final Color TEXT_PRIMARY = new Color(0x1E, 0x29, 0x3B);
    private static final Color TEXT_MUTED = new Color(0x64, 0x74, 0x8B);
    private static final Color TEAL_PRIMARY = new Color(0x00, 0xA8, 0xA8);
    private static final Color PILL_GREEN_BG = new Color(0xDC, 0xFC, 0xE7);
    private static final Color PILL_GREEN_TEXT = new Color(0x15, 0x80, 0x3D);
    private static final Color PILL_RED_BG = new Color(0xFE, 0xE2, 0xE2);
    private static final Color PILL_RED_TEXT = new Color(0xB9, 0x1C, 0x1C);
    private static final Color PILL_YELLOW_BG = new Color(0xFE, 0xF3, 0xC7);
    private static final Color PILL_YELLOW_TEXT = new Color(0xB4, 0x53, 0x09);

    private final AttendanceDAO attendanceDAO;

    // Individual Tab Components
    private JTextField individualStudentIdField;
    private JComboBox<String> individualCourseCombo;
    private JPanel individualResultCard;
    private JLabel studentNameLabel;
    private JLabel studentTgLabel;
    private JLabel theoryStatLabel;
    private JLabel practicalStatLabel;
    private JLabel combinedStatLabel;
    private JLabel eligibilityStatusLabel;
    private DefaultTableModel sessionHistoryModel;

    // Batch Tab Components
    private JComboBox<String> batchSelectorCombo;
    private JComboBox<String> batchCourseCombo;
    private DefaultTableModel batchTableModel;
    private JLabel batchEmptyStateLabel;
    private JScrollPane batchScrollPane;

    public AttendanceSummaryPanel() {
        this(new AttendanceDAO());
    }

    public AttendanceSummaryPanel(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO != null ? attendanceDAO : new AttendanceDAO();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(BG_PAGE);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Attendance Summary");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("View individual student attendance or generate whole-batch 80% eligibility summaries");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_MUTED);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane for Individual vs Batch
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(TEXT_PRIMARY);

        tabbedPane.addTab("Individual Student Attendance", createIndividualTab());
        tabbedPane.addTab("Whole-Batch Attendance Summary", createBatchTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // =========================================================================
    // TAB 1: INDIVIDUAL STUDENT ATTENDANCE
    // =========================================================================

    private JPanel createIndividualTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BG_PAGE);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Search Controls Card
        JPanel searchCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        searchCard.setBackground(CARD_BG);
        searchCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 16, 6, 16)
        ));

        JLabel stuLabel = new JLabel("Student ID:");
        stuLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        stuLabel.setForeground(TEXT_PRIMARY);
        searchCard.add(stuLabel);

        individualStudentIdField = new JTextField(8);
        individualStudentIdField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        individualStudentIdField.setPreferredSize(new Dimension(100, 34));
        individualStudentIdField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(2, 8, 2, 8)
        ));
        searchCard.add(individualStudentIdField);

        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        courseLabel.setForeground(TEXT_PRIMARY);
        searchCard.add(courseLabel);

        String[] courses = {
                "ICT2131 - Database Management Systems",
                "ICT2132 - Object Oriented Programming",
                "ICT2133 - Data Structures",
                "ICT2134 - Software Engineering",
                "ICT2135 - Web Application Development"
        };
        individualCourseCombo = new JComboBox<>(courses);
        individualCourseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        individualCourseCombo.setPreferredSize(new Dimension(240, 34));
        individualCourseCombo.setBackground(Color.WHITE);
        searchCard.add(individualCourseCombo);

        JButton searchBtn = new JButton("Search Summary");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchBtn.setBackground(TEAL_PRIMARY);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(new EmptyBorder(8, 16, 8, 16));
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> handleIndividualSearch());
        searchCard.add(searchBtn);

        panel.add(searchCard, BorderLayout.NORTH);

        // Result Card Container
        individualResultCard = new JPanel(new BorderLayout(0, 16));
        individualResultCard.setBackground(CARD_BG);
        individualResultCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Result Header (Student Name & Status)
        JPanel resultHeader = new JPanel(new BorderLayout(10, 5));
        resultHeader.setOpaque(false);

        JPanel nameContainer = new JPanel(new GridLayout(2, 1, 2, 2));
        nameContainer.setOpaque(false);

        studentNameLabel = new JLabel("Enter a Student ID and click 'Search Summary'");
        studentNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        studentNameLabel.setForeground(TEXT_PRIMARY);

        studentTgLabel = new JLabel("TG Number: -- | Course: --");
        studentTgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentTgLabel.setForeground(TEXT_MUTED);

        nameContainer.add(studentNameLabel);
        nameContainer.add(studentTgLabel);
        resultHeader.add(nameContainer, BorderLayout.WEST);

        eligibilityStatusLabel = new JLabel(" ");
        eligibilityStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        eligibilityStatusLabel.setOpaque(true);
        eligibilityStatusLabel.setBackground(BG_PAGE);
        eligibilityStatusLabel.setBorder(new EmptyBorder(6, 14, 6, 14));
        resultHeader.add(eligibilityStatusLabel, BorderLayout.EAST);

        individualResultCard.add(resultHeader, BorderLayout.NORTH);

        // Metrics Grid (Theory, Practical, Combined)
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        metricsPanel.setOpaque(false);

        theoryStatLabel = new JLabel("0 / 0 (0.0%)", SwingConstants.CENTER);
        practicalStatLabel = new JLabel("0 / 0 (0.0%)", SwingConstants.CENTER);
        combinedStatLabel = new JLabel("0 / 0 (0.0%)", SwingConstants.CENTER);

        metricsPanel.add(createMetricSubCard("Theory Attendance", theoryStatLabel));
        metricsPanel.add(createMetricSubCard("Practical Attendance", practicalStatLabel));
        metricsPanel.add(createMetricSubCard("Overall Combined", combinedStatLabel));

        // Session History Table
        String[] histCols = {"Attendance ID", "Course Code", "Session Date", "Type"};
        sessionHistoryModel = new DefaultTableModel(histCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable historyTable = new JTable(sessionHistoryModel);
        historyTable.setRowHeight(32);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        historyTable.getTableHeader().setBackground(new Color(0xF8, 0xFA, 0xFC));

        JScrollPane histScroll = new JScrollPane(historyTable);
        histScroll.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        histScroll.getViewport().setBackground(Color.WHITE);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setOpaque(false);
        centerPanel.add(metricsPanel, BorderLayout.NORTH);
        centerPanel.add(histScroll, BorderLayout.CENTER);

        individualResultCard.add(centerPanel, BorderLayout.CENTER);

        panel.add(individualResultCard, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMetricSubCard(String title, JLabel statLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(new Color(0xFB, 0xFD, 0xFE));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(TEXT_MUTED);

        statLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statLabel.setForeground(TEXT_PRIMARY);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(statLabel, BorderLayout.CENTER);

        return card;
    }

    private void handleIndividualSearch() {
        String idText = individualStudentIdField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(idText);
            if (studentId <= 0) {
                JOptionPane.showMessageDialog(this, "Student ID must be a positive integer.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Student ID format.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseSelection = (String) individualCourseCombo.getSelectedItem();
        String courseCode = courseSelection != null ? courseSelection.split(" - ")[0].trim() : "ICT2131";

        try {
            AttendanceSummary summary = attendanceDAO.getStudentAttendanceSummary(studentId, courseCode);
            List<Attendance> history = attendanceDAO.getAttendanceByStudentAndCourse(studentId, courseCode);

            studentNameLabel.setText(summary.getStudentName().trim().isEmpty() ?
                    "Student #" + studentId : summary.getStudentName());
            studentTgLabel.setText("TG Number: " + (summary.getTgNumber().isEmpty() ? "N/A" : summary.getTgNumber()) +
                    "  |  Course: " + courseCode);

            theoryStatLabel.setText(summary.getAttendedTheory() + " / " + summary.getTotalTheory() +
                    " (" + String.format("%.1f", summary.getTheoryPercentage()) + "%)");
            practicalStatLabel.setText(summary.getAttendedPractical() + " / " + summary.getTotalPractical() +
                    " (" + String.format("%.1f", summary.getPracticalPercentage()) + "%)");
            combinedStatLabel.setText(summary.getTotalAttended() + " / " + summary.getTotalSessions() +
                    " (" + String.format("%.1f", summary.getCombinedPercentage()) + "%)");

            // 80% Threshold Status Pill
            if (summary.isEligible()) {
                eligibilityStatusLabel.setText("ELIGIBLE FOR EXAM (>= 80%)");
                eligibilityStatusLabel.setBackground(PILL_GREEN_BG);
                eligibilityStatusLabel.setForeground(PILL_GREEN_TEXT);
            } else {
                eligibilityStatusLabel.setText("INELIGIBLE - BARRED (< 80%)");
                eligibilityStatusLabel.setBackground(PILL_RED_BG);
                eligibilityStatusLabel.setForeground(PILL_RED_TEXT);
            }

            // Populate session history
            sessionHistoryModel.setRowCount(0);
            for (Attendance a : history) {
                sessionHistoryModel.addRow(new Object[]{
                        "#" + a.getAttendanceId(),
                        a.getCourseCode(),
                        a.getSessionDate() != null ? a.getSessionDate().toString() : "N/A",
                        a.getType()
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching summary: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // TAB 2: WHOLE-BATCH ATTENDANCE SUMMARY
    // =========================================================================

    private JPanel createBatchTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BG_PAGE);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Controls Card
        JPanel controlsCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        controlsCard.setBackground(CARD_BG);
        controlsCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 16, 6, 16)
        ));

        JLabel batchLabel = new JLabel("Batch:");
        batchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        batchLabel.setForeground(TEXT_PRIMARY);
        controlsCard.add(batchLabel);

        String[] batches = {"2024", "2023", "2022"};
        batchSelectorCombo = new JComboBox<>(batches);
        batchSelectorCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        batchSelectorCombo.setPreferredSize(new Dimension(90, 34));
        batchSelectorCombo.setBackground(Color.WHITE);
        controlsCard.add(batchSelectorCombo);

        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        courseLabel.setForeground(TEXT_PRIMARY);
        controlsCard.add(courseLabel);

        String[] courses = {
                "ICT2131 - Database Management Systems",
                "ICT2132 - Object Oriented Programming",
                "ICT2133 - Data Structures",
                "ICT2134 - Software Engineering",
                "ICT2135 - Web Application Development"
        };
        batchCourseCombo = new JComboBox<>(courses);
        batchCourseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        batchCourseCombo.setPreferredSize(new Dimension(240, 34));
        batchCourseCombo.setBackground(Color.WHITE);
        controlsCard.add(batchCourseCombo);

        JButton generateBtn = new JButton("Generate Batch Summary");
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        generateBtn.setBackground(TEAL_PRIMARY);
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setFocusPainted(false);
        generateBtn.setBorder(new EmptyBorder(8, 16, 8, 16));
        generateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateBtn.addActionListener(e -> handleBatchSummary());
        controlsCard.add(generateBtn);

        panel.add(controlsCard, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = new JPanel(new BorderLayout(0, 10));
        tableCard.setBackground(CARD_BG);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        String[] cols = {"Student ID", "TG Number", "Student Name", "Theory (Att/Tot, %)",
                "Practical (Att/Tot, %)", "Combined %", "80% Eligibility"};

        batchTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable batchTable = new JTable(batchTableModel);
        batchTable.setRowHeight(34);
        batchTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        batchTable.setGridColor(new Color(0xF1, 0xF5, 0xF9));

        JTableHeader header = batchTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(0xF8, 0xFA, 0xFC));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        // Center renderers and custom status pill renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < cols.length - 1; i++) {
            batchTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        batchTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setOpaque(true);

                String text = value != null ? value.toString() : "";
                if (text.contains("ELIGIBLE") && !text.contains("INELIGIBLE")) {
                    lbl.setBackground(PILL_GREEN_BG);
                    lbl.setForeground(PILL_GREEN_TEXT);
                } else if (text.contains("INELIGIBLE")) {
                    lbl.setBackground(PILL_RED_BG);
                    lbl.setForeground(PILL_RED_TEXT);
                } else {
                    lbl.setBackground(new Color(0xEE, 0xF2, 0xF6));
                    lbl.setForeground(TEXT_MUTED);
                }
                return lbl;
            }
        });

        batchScrollPane = new JScrollPane(batchTable);
        batchScrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        batchScrollPane.getViewport().setBackground(Color.WHITE);

        batchEmptyStateLabel = new JLabel("Click 'Generate Batch Summary' to load student records.", SwingConstants.CENTER);
        batchEmptyStateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        batchEmptyStateLabel.setForeground(TEXT_MUTED);

        tableCard.add(batchScrollPane, BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    private void handleBatchSummary() {
        String batch = (String) batchSelectorCombo.getSelectedItem();
        String courseSelection = (String) batchCourseCombo.getSelectedItem();
        String courseCode = courseSelection != null ? courseSelection.split(" - ")[0].trim() : "ICT2131";

        try {
            List<AttendanceSummary> list = attendanceDAO.getBatchAttendanceSummaries(batch, courseCode);
            batchTableModel.setRowCount(0);

            if (list == null || list.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No students or attendance records found for Batch " + batch + " and Course " + courseCode + ".",
                        "Empty Results", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (AttendanceSummary s : list) {
                String theoryStr = s.getAttendedTheory() + "/" + s.getTotalTheory() +
                        " (" + String.format("%.1f", s.getTheoryPercentage()) + "%)";
                String practicalStr = s.getAttendedPractical() + "/" + s.getTotalPractical() +
                        " (" + String.format("%.1f", s.getPracticalPercentage()) + "%)";
                String combinedStr = String.format("%.1f", s.getCombinedPercentage()) + "%";
                String eligibilityStr = s.isEligible() ? "ELIGIBLE" : "INELIGIBLE";

                batchTableModel.addRow(new Object[]{
                        s.getStudentId(),
                        s.getTgNumber(),
                        s.getStudentName(),
                        theoryStr,
                        practicalStr,
                        combinedStr,
                        eligibilityStr
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
