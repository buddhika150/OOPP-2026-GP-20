package ftms.gui;

import ftms.db.DataHelper;
import ftms.model.Attendance;
import ftms.model.TechnicalOfficer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Java Swing GUI Panel for Attendance Summary complying with SRS Section 4.
 * Assigned to Member 3.
 */
public class AttendanceSummaryPanel extends JPanel {
    private final TechnicalOfficer technicalOfficer;

    private JComboBox<String> cmbCourse;
    private JComboBox<DataHelper.StudentItem> cmbStudent;
    private JCheckBox chkWholeBatch;
    private JComboBox<String> cmbViewMode; // "Combined (30 Sessions)", "Theory Only (15 Sessions)", "Practical Only (15 Sessions)"
    private JComboBox<String> cmbScenarioFilter;

    // Metrics labels
    private JLabel lblTotalRecords;
    private JLabel lblEligibleCount;
    private JLabel lblIneligibleCount;
    private JLabel lblMedicalsCount;

    // Table
    private JTable summaryTable;
    private DefaultTableModel tableModel;

    private List<Attendance.AttendanceSummaryDTO> currentSummaries = new ArrayList<>();

    public AttendanceSummaryPanel(TechnicalOfficer technicalOfficer) {
        this.technicalOfficer = technicalOfficer;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        loadFilters();
        loadSummary();
    }

    private void initComponents() {
        // TOP: Header & Metrics Cards
        JPanel pnlNorth = new JPanel(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Attendance Summary & 80% Eligibility Analysis");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlNorth.add(lblTitle, BorderLayout.NORTH);

        // Metrics Summary Cards
        JPanel pnlMetrics = new JPanel(new GridLayout(1, 4, 12, 10));
        lblTotalRecords = createMetricCard(pnlMetrics, "Total Evaluated", "0", new Color(52, 58, 64));
        lblEligibleCount = createMetricCard(pnlMetrics, "Eligible (>= 80%)", "0", new Color(40, 167, 69));
        lblIneligibleCount = createMetricCard(pnlMetrics, "Ineligible (< 80%)", "0", new Color(220, 53, 69));
        lblMedicalsCount = createMetricCard(pnlMetrics, "Assisted by Medicals", "0", new Color(23, 162, 184));

        pnlNorth.add(pnlMetrics, BorderLayout.CENTER);

        // Filter Bar
        JPanel pnlFilters = new JPanel(new GridBagLayout());
        pnlFilters.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Filter Options (Individual & Whole-Batch)",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Course & Student Selection
        gbc.gridx = 0; gbc.gridy = 0;
        pnlFilters.add(new JLabel("Course:"), gbc);
        cmbCourse = new JComboBox<>();
        cmbCourse.addItem("ALL COURSES");
        gbc.gridx = 1; gbc.gridy = 0;
        pnlFilters.add(cmbCourse, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        pnlFilters.add(new JLabel("Student:"), gbc);
        cmbStudent = new JComboBox<>();
        gbc.gridx = 3; gbc.gridy = 0;
        pnlFilters.add(cmbStudent, gbc);

        chkWholeBatch = new JCheckBox("Whole Batch", true);
        gbc.gridx = 4; gbc.gridy = 0;
        pnlFilters.add(chkWholeBatch, gbc);

        // Row 2: View Mode & Scenario Filter
        gbc.gridx = 0; gbc.gridy = 1;
        pnlFilters.add(new JLabel("Session Mode:"), gbc);
        cmbViewMode = new JComboBox<>(new String[]{
                "Combined (Theory + Practical)",
                "Theory Only (15 Sessions)",
                "Practical Only (15 Sessions)"
        });
        gbc.gridx = 1; gbc.gridy = 1;
        pnlFilters.add(cmbViewMode, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        pnlFilters.add(new JLabel("Scenario Filter:"), gbc);
        cmbScenarioFilter = new JComboBox<>(new String[]{
                "All Scenarios",
                "Above 80%",
                "Exactly 80%",
                "Below 80% without Medicals",
                "Above 80% with Medicals",
                "Below 80% with Medicals"
        });
        gbc.gridx = 3; gbc.gridy = 1;
        pnlFilters.add(cmbScenarioFilter, gbc);

        JPanel pnlFilterActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton btnGenerate = new JButton("Generate Summary");
        btnGenerate.setBackground(new Color(0, 123, 255));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFocusPainted(false);

        JButton btnReset = new JButton("Reset");
        pnlFilterActions.add(btnGenerate);
        pnlFilterActions.add(btnReset);

        gbc.gridx = 4; gbc.gridy = 1;
        pnlFilters.add(pnlFilterActions, gbc);

        pnlNorth.add(pnlFilters, BorderLayout.SOUTH);
        add(pnlNorth, BorderLayout.NORTH);

        // CENTER: Summary Table
        String[] columns = {
                "Student TG", "Student Name", "Course",
                "Theory Att. (/15)", "Theory %",
                "Pract. Att. (/15)", "Pract. %",
                "Combined (/30)", "Raw %",
                "Approved Meds", "Effective %", "Scenario / Eligibility"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        summaryTable = new JTable(tableModel);
        summaryTable.setRowHeight(25);
        summaryTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        summaryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Custom Cell Renderer for Eligibility Status
        summaryTable.getColumnModel().getColumn(11).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String str = value.toString();
                    if (str.contains("Eligible") && !str.contains("Not Eligible")) {
                        c.setForeground(new Color(34, 139, 34)); // Forest green
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (str.contains("Not Eligible")) {
                        c.setForeground(new Color(178, 34, 34)); // Firebrick red
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(summaryTable);
        add(scrollPane, BorderLayout.CENTER);

        // Listeners
        chkWholeBatch.addActionListener(e -> cmbStudent.setEnabled(!chkWholeBatch.isSelected()));
        btnGenerate.addActionListener(e -> loadSummary());
        btnReset.addActionListener(e -> {
            cmbCourse.setSelectedIndex(0);
            chkWholeBatch.setSelected(true);
            cmbStudent.setEnabled(false);
            cmbViewMode.setSelectedIndex(0);
            cmbScenarioFilter.setSelectedIndex(0);
            loadSummary();
        });

        cmbScenarioFilter.addActionListener(e -> renderFilteredSummaries());
    }

    private JLabel createMetricCard(JPanel parent, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2, true),
                new EmptyBorder(8, 12, 8, 12)));
        card.setBackground(Color.WHITE);

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblT.setForeground(Color.DARK_GRAY);

        JLabel lblV = new JLabel(value);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblV.setForeground(color);

        card.add(lblT, BorderLayout.NORTH);
        card.add(lblV, BorderLayout.CENTER);
        parent.add(card);

        return lblV;
    }

    private void loadFilters() {
        cmbCourse.removeAllItems();
        cmbCourse.addItem("ALL COURSES");
        for (DataHelper.CourseItem c : DataHelper.getAllCourses()) {
            cmbCourse.addItem(c.getCourseCode());
        }

        cmbStudent.removeAllItems();
        for (DataHelper.StudentItem s : DataHelper.getAllStudents()) {
            cmbStudent.addItem(s);
        }
        cmbStudent.setEnabled(!chkWholeBatch.isSelected());
    }

    private void loadSummary() {
        String courseCode = (String) cmbCourse.getSelectedItem();
        if ("ALL COURSES".equals(courseCode)) {
            courseCode = null;
        }

        int studentId = 0;
        if (!chkWholeBatch.isSelected()) {
            DataHelper.StudentItem selStudent = (DataHelper.StudentItem) cmbStudent.getSelectedItem();
            if (selStudent != null) {
                studentId = selStudent.getStudentId();
            }
        }

        try {
            currentSummaries = technicalOfficer.viewAttendanceSummary(courseCode, studentId);
            renderFilteredSummaries();
        } catch (SQLException ex) {
            UiUtils.showError(this, "Error generating attendance summary: " + ex.getMessage(), "Database Error");
        }
    }

    private void renderFilteredSummaries() {
        tableModel.setRowCount(0);

        String scenarioFilter = (String) cmbScenarioFilter.getSelectedItem();
        int total = 0;
        int eligible = 0;
        int ineligible = 0;
        int assistedByMedicals = 0;

        for (Attendance.AttendanceSummaryDTO dto : currentSummaries) {
            String status = dto.getEligibilityStatus();

            // Check scenario filter
            if (scenarioFilter != null && !scenarioFilter.equals("All Scenarios")) {
                if (!status.toLowerCase().contains(scenarioFilter.toLowerCase())) {
                    continue;
                }
            }

            total++;
            if (status.contains("Eligible") && !status.contains("Not Eligible")) {
                eligible++;
            } else {
                ineligible++;
            }

            if (status.contains("with Medicals")) {
                assistedByMedicals++;
            }

            tableModel.addRow(new Object[]{
                    dto.getTgNum(),
                    dto.getStudentName(),
                    dto.getCourseCode(),
                    dto.getTheoryAttended(),
                    String.format("%.1f%%", dto.getTheoryPercentage()),
                    dto.getPracticalAttended(),
                    String.format("%.1f%%", dto.getPracticalPercentage()),
                    dto.getTotalAttended(),
                    String.format("%.1f%%", dto.getCombinedPercentage()),
                    dto.getTotalApprovedMedicals(),
                    String.format("%.1f%%", dto.getEffectiveCombinedPercentage()),
                    dto.getEligibilityStatus()
            });
        }

        lblTotalRecords.setText(String.valueOf(total));
        lblEligibleCount.setText(String.valueOf(eligible));
        lblIneligibleCount.setText(String.valueOf(ineligible));
        lblMedicalsCount.setText(String.valueOf(assistedByMedicals));
    }
}
