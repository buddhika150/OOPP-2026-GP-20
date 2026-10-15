package ftms.gui;

import ftms.model.TechnicalOfficer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Java Swing GUI Panel for viewing department timetables.
 * Assigned to Member 3 as per SRS 2.3, 9.4, 13.
 */
public class TimetableViewPanel extends JPanel {
    private final TechnicalOfficer technicalOfficer;

    private JComboBox<String> cmbDepartment;
    private JTable timetableTable;
    private DefaultTableModel tableModel;

    public TimetableViewPanel(TechnicalOfficer technicalOfficer) {
        this.technicalOfficer = technicalOfficer;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        loadTimetable();
    }

    private void initComponents() {
        // TOP Header & Filters
        JPanel pnlNorth = new JPanel(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Department Academic Timetable");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlNorth.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlFilter.setBorder(BorderFactory.createTitledBorder("Department Filter"));

        pnlFilter.add(new JLabel("Department:"));
        cmbDepartment = new JComboBox<>(new String[]{"ICT", "ALL DEPARTMENTS"});
        if (technicalOfficer != null && technicalOfficer.getDepartment() != null) {
            cmbDepartment.setSelectedItem(technicalOfficer.getDepartment());
        }
        pnlFilter.add(cmbDepartment);

        JButton btnFilter = new JButton("View Timetable");
        JButton btnRefresh = new JButton("Refresh");
        pnlFilter.add(btnFilter);
        pnlFilter.add(btnRefresh);

        pnlNorth.add(pnlFilter, BorderLayout.CENTER);
        add(pnlNorth, BorderLayout.NORTH);

        // CENTER: Timetable Table
        String[] columns = {"Timetable ID", "Course Code", "Course Name", "Department", "Day", "Time Slot"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        timetableTable = new JTable(tableModel);
        timetableTable.setRowHeight(26);
        timetableTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timetableTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(timetableTable);
        add(scrollPane, BorderLayout.CENTER);

        // Listeners
        btnFilter.addActionListener(e -> loadTimetable());
        btnRefresh.addActionListener(e -> loadTimetable());
    }

    private void loadTimetable() {
        tableModel.setRowCount(0);
        String dept = (String) cmbDepartment.getSelectedItem();
        if ("ALL DEPARTMENTS".equals(dept)) {
            dept = null;
        }

        try {
            List<TechnicalOfficer.TimetableEntry> entries = TechnicalOfficer.viewTimetable(dept);
            for (TechnicalOfficer.TimetableEntry t : entries) {
                tableModel.addRow(new Object[]{
                        t.getTimetableId(),
                        t.getCourseCode(),
                        t.getCourseName(),
                        t.getDepartment(),
                        t.getDay(),
                        t.getTimeSlot()
                });
            }
        } catch (SQLException ex) {
            UiUtils.showError(this, "Error loading timetable: " + ex.getMessage(), "Database Error");
        }
    }
}
