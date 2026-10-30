package com.ftms.view.technicalofficer;

import com.ftms.dao.AttendanceDAO;
import com.ftms.model.Attendance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel for viewing individual and batch attendance summaries and percentages
 * across THEORY, PRACTICAL, and COMBINED sessions.
 */
public class AttendanceSummaryPanel extends JPanel {

    private static final Color BG_MAIN = new Color(245, 248, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(224, 232, 240);
    private static final Color NAVY_DARK = new Color(11, 46, 74);
    private static final Color TEAL_ACCENT = new Color(0, 131, 155);
    private static final Color TEXT_MUTED = new Color(110, 125, 140);
    private static final Color GREEN_OK = new Color(22, 135, 75);
    private static final Color RED_WARN = new Color(205, 58, 58);

    private final AttendanceDAO attendanceDAO;

    private JTextField txtStudentId;
    private JComboBox<String> cmbCourseCode;
    private JComboBox<String> cmbTypeFilter;

    private JLabel lblTotalGroups;
    private JLabel lblTotalSessions;
    private JLabel lblAttendedSessions;
    private JLabel lblOverallPercentage;

    private JTable summaryTable;
    private DefaultTableModel tableModel;

    public AttendanceSummaryPanel() {
        this.attendanceDAO = new AttendanceDAO();
        initComponents();
        loadSummaryData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(4, 0, 0, 0));

        // Top container: Filter Card + KPI Cards
        JPanel topContainer = new JPanel(new BorderLayout(0, 14));
        topContainer.setOpaque(false);

        // 1. Filter Card
        JPanel filterCard = new JPanel(new BorderLayout(0, 12));
        filterCard.setBackground(CARD_BG);
        filterCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(16, 22, 16, 22)
        ));

        JLabel lblFilterTitle = new JLabel("Search & Filter Attendance Summary (Individual / Batch)");
        lblFilterTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFilterTitle.setForeground(NAVY_DARK);
        filterCard.add(lblFilterTitle, BorderLayout.NORTH);

        JPanel controlsRow = new JPanel(new GridBagLayout());
        controlsRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtStudentId = new JTextField();
        txtStudentId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtStudentId.setForeground(NAVY_DARK);
        txtStudentId.setToolTipText("Leave empty for Batch Summary, or enter Student ID (e.g., 11)");
        txtStudentId.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(206, 216, 226), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        cmbCourseCode = new JComboBox<>(new String[]{"ALL COURSES", "ICT2132", "ICT2142", "ICT2152", "ICT2162"});
        cmbCourseCode.setEditable(true);
        cmbCourseCode.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbCourseCode.setBackground(Color.WHITE);

        cmbTypeFilter = new JComboBox<>(new String[]{"COMBINED (ALL)", Attendance.TYPE_THEORY, Attendance.TYPE_PRACTICAL});
        cmbTypeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTypeFilter.setBackground(Color.WHITE);

        addFilterField(controlsRow, gbc, 0, "Student ID (Optional - Blank for Batch)", txtStudentId);
        addFilterField(controlsRow, gbc, 1, "Course Code", cmbCourseCode);
        addFilterField(controlsRow, gbc, 2, "Session Type Filter", cmbTypeFilter);

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnWrap.setOpaque(false);

        JButton btnSearch = createButton("Apply Filter", TEAL_ACCENT);
        JButton btnReset = createButton("Reset", new Color(108, 122, 137));
        JButton btnRefresh = createButton("Refresh", NAVY_DARK);

        btnSearch.addActionListener(e -> loadSummaryData());
        btnReset.addActionListener(e -> {
            txtStudentId.setText("");
            cmbCourseCode.setSelectedIndex(0);
            cmbTypeFilter.setSelectedIndex(0);
            loadSummaryData();
        });
        btnRefresh.addActionListener(e -> loadSummaryData());

        btnWrap.add(btnSearch);
        btnWrap.add(btnReset);
        btnWrap.add(btnRefresh);

        filterCard.add(controlsRow, BorderLayout.CENTER);
        filterCard.add(btnWrap, BorderLayout.SOUTH);
        topContainer.add(filterCard, BorderLayout.NORTH);

        // 2. KPI Summary Cards Row
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiGrid.setOpaque(false);

        lblTotalGroups = new JLabel("0");
        lblTotalSessions = new JLabel("0");
        lblAttendedSessions = new JLabel("0");
        lblOverallPercentage = new JLabel("0.0%");

        kpiGrid.add(createKpiCard("Student-Course Entries", lblTotalGroups, NAVY_DARK));
        kpiGrid.add(createKpiCard("Total Filtered Sessions", lblTotalSessions, NAVY_DARK));
        kpiGrid.add(createKpiCard("Sessions Attended", lblAttendedSessions, TEAL_ACCENT));
        kpiGrid.add(createKpiCard("Overall Attendance %", lblOverallPercentage, GREEN_OK));

        topContainer.add(kpiGrid, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // 3. Table Card
        JPanel tableCard = new JPanel(new BorderLayout(0, 10));
        tableCard.setBackground(CARD_BG);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(16, 22, 18, 22)
        ));

        JLabel lblTableHeading = new JLabel("Student & Batch Course Attendance Breakdown (80% Eligibility Threshold)");
        lblTableHeading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTableHeading.setForeground(NAVY_DARK);
        tableCard.add(lblTableHeading, BorderLayout.NORTH);

        String[] columns = {
                "Student ID",
                "Course Code",
                "Theory (Att/Tot)",
                "Theory %",
                "Practical (Att/Tot)",
                "Practical %",
                "Filtered (Att/Tot)",
                "Filtered %",
                "Eligibility (>= 80%)"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        summaryTable = new JTable(tableModel);
        styleTable(summaryTable);

        JScrollPane scrollPane = new JScrollPane(summaryTable);
        scrollPane.setBorder(new LineBorder(CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private void addFilterField(JPanel panel, GridBagConstraints gbc, int col, String labelText, JComponent comp) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));
        wrapper.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(NAVY_DARK);

        comp.setPreferredSize(new Dimension(170, 34));
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(comp, BorderLayout.CENTER);

        gbc.gridx = col;
        gbc.gridy = 0;
        panel.add(wrapper, gbc);
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(valueColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void loadSummaryData() {
        String studentFilterStr = txtStudentId.getText().trim();
        Integer studentFilter = null;
        if (!studentFilterStr.isEmpty()) {
            try {
                studentFilter = Integer.parseInt(studentFilterStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Student ID must be a valid integer.",
                        "Invalid Filter", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Object courseObj = cmbCourseCode.getSelectedItem();
        String courseFilter = courseObj != null ? courseObj.toString().trim().toUpperCase() : "ALL COURSES";
        String typeFilter = String.valueOf(cmbTypeFilter.getSelectedItem());

        List<Attendance> rawRecords;
        if (studentFilter != null) {
            rawRecords = attendanceDAO.getAttendanceByStudentId(studentFilter);
        } else if (!courseFilter.isEmpty() && !"ALL COURSES".equalsIgnoreCase(courseFilter)) {
            rawRecords = attendanceDAO.getAttendanceByCourseCode(courseFilter);
        } else {
            rawRecords = attendanceDAO.getAllAttendance();
        }

        // Group records by (Student_ID + Course_code)
        Map<String, SummaryStats> statsMap = new LinkedHashMap<>();

        int grandTotalFiltered = 0;
        int grandAttendedFiltered = 0;

        for (Attendance a : rawRecords) {
            if (studentFilter != null && a.getStudentId() != studentFilter) {
                continue;
            }
            if (!courseFilter.isEmpty() && !"ALL COURSES".equalsIgnoreCase(courseFilter)
                    && !courseFilter.equalsIgnoreCase(a.getCourseCode())) {
                continue;
            }

            String key = a.getStudentId() + "_" + a.getCourseCode();
            SummaryStats stats = statsMap.computeIfAbsent(key,
                    k -> new SummaryStats(a.getStudentId(), a.getCourseCode()));

            if (Attendance.TYPE_THEORY.equalsIgnoreCase(a.getType())) {
                stats.theoryTotal++;
                if (a.isPresent()) {
                    stats.theoryPresent++;
                }
            } else if (Attendance.TYPE_PRACTICAL.equalsIgnoreCase(a.getType())) {
                stats.practicalTotal++;
                if (a.isPresent()) {
                    stats.practicalPresent++;
                }
            }
        }

        tableModel.setRowCount(0);

        for (SummaryStats s : statsMap.values()) {
            int filteredPresent;
            int filteredTotal;

            if (Attendance.TYPE_THEORY.equalsIgnoreCase(typeFilter)) {
                filteredPresent = s.theoryPresent;
                filteredTotal = s.theoryTotal;
            } else if (Attendance.TYPE_PRACTICAL.equalsIgnoreCase(typeFilter)) {
                filteredPresent = s.practicalPresent;
                filteredTotal = s.practicalTotal;
            } else {
                filteredPresent = s.theoryPresent + s.practicalPresent;
                filteredTotal = s.theoryTotal + s.practicalTotal;
            }

            if (filteredTotal == 0) {
                continue;
            }

            grandTotalFiltered += filteredTotal;
            grandAttendedFiltered += filteredPresent;

            double theoryPct = s.theoryTotal > 0
                    ? Attendance.calculateAttendancePercentage(s.theoryPresent, s.theoryTotal) : 0.0;
            double practicalPct = s.practicalTotal > 0
                    ? Attendance.calculateAttendancePercentage(s.practicalPresent, s.practicalTotal) : 0.0;
            double filteredPct = Attendance.calculateAttendancePercentage(filteredPresent, filteredTotal);

            String eligibility = filteredPct >= 80.0 ? "ELIGIBLE" : "NOT ELIGIBLE";

            tableModel.addRow(new Object[]{
                    s.studentId,
                    s.courseCode,
                    s.theoryPresent + " / " + s.theoryTotal,
                    String.format("%.1f%%", theoryPct),
                    s.practicalPresent + " / " + s.practicalTotal,
                    String.format("%.1f%%", practicalPct),
                    filteredPresent + " / " + filteredTotal,
                    String.format("%.1f%%", filteredPct),
                    eligibility
            });
        }

        double overallPct = grandTotalFiltered > 0
                ? Attendance.calculateAttendancePercentage(grandAttendedFiltered, grandTotalFiltered) : 0.0;

        lblTotalGroups.setText(String.valueOf(tableModel.getRowCount()));
        lblTotalSessions.setText(String.valueOf(grandTotalFiltered));
        lblAttendedSessions.setText(String.valueOf(grandAttendedFiltered));
        lblOverallPercentage.setText(String.format("%.1f%%", overallPct));
        lblOverallPercentage.setForeground(overallPct >= 80.0 ? GREEN_OK : RED_WARN);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(115, 34));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setSelectionBackground(new Color(216, 238, 245));
        table.setSelectionForeground(NAVY_DARK);
        table.setGridColor(new Color(235, 240, 245));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(NAVY_DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String text = value != null ? value.toString() : "";
                if ("ELIGIBLE".equalsIgnoreCase(text)) {
                    lbl.setForeground(GREEN_OK);
                } else {
                    lbl.setForeground(RED_WARN);
                }
                return lbl;
            }
        });
    }

    private static class SummaryStats {
        final int studentId;
        final String courseCode;
        int theoryPresent;
        int theoryTotal;
        int practicalPresent;
        int practicalTotal;

        SummaryStats(int studentId, String courseCode) {
            this.studentId = studentId;
            this.courseCode = courseCode;
        }
    }
}
