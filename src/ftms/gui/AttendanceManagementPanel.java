package ftms.gui;

import ftms.db.DataHelper;
import ftms.model.Attendance;
import ftms.model.TechnicalOfficer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Java Swing GUI Panel for Attendance Entry and Maintenance.
 * Assigned to Member 3.
 */
public class AttendanceManagementPanel extends JPanel {
    private final TechnicalOfficer technicalOfficer;

    // Form inputs
    private JTextField txtAttendanceId;
    private JComboBox<DataHelper.StudentItem> cmbStudent;
    private JComboBox<DataHelper.CourseItem> cmbCourse;
    private JTextField txtSessionDate;
    private JComboBox<String> cmbType;

    // Filter inputs
    private JComboBox<String> cmbFilterCourse;
    private JComboBox<String> cmbFilterType;

    // Table
    private JTable attendanceTable;
    private DefaultTableModel tableModel;

    public AttendanceManagementPanel(TechnicalOfficer technicalOfficer) {
        this.technicalOfficer = technicalOfficer;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        loadDropdownData();
        loadAttendanceData();
    }

    private void initComponents() {
        // TOP: Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Undergraduate Attendance Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        add(pnlHeader, BorderLayout.NORTH);

        // CENTER: Split into Form on Left, Table on Right
        JPanel pnlCenter = new JPanel(new BorderLayout(15, 15));

        // Form Panel
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Attendance Details", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));
        pnlForm.setPreferredSize(new Dimension(360, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Attendance ID (Read-only)
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        pnlForm.add(new JLabel("Attendance ID:"), gbc);
        txtAttendanceId = new JTextField();
        txtAttendanceId.setEditable(false);
        txtAttendanceId.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(txtAttendanceId, gbc);

        // Student
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(new JLabel("Student: *"), gbc);
        cmbStudent = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(cmbStudent, gbc);

        // Course
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(new JLabel("Course: *"), gbc);
        cmbCourse = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(cmbCourse, gbc);

        // Session Date
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(new JLabel("Session Date (YYYY-MM-DD): *"), gbc);
        txtSessionDate = new JTextField(LocalDate.now().toString());
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(txtSessionDate, gbc);

        // Session Type
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(new JLabel("Session Type: *"), gbc);
        cmbType = new JComboBox<>(new String[]{"THEORY", "PRACTICAL"});
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(cmbType, gbc);

        // Buttons
        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 8, 8));
        JButton btnAdd = new JButton("Add Attendance");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);

        JButton btnUpdate = new JButton("Update Attendance");
        btnUpdate.setBackground(new Color(0, 123, 255));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);

        JButton btnDelete = new JButton("Delete Attendance");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);

        JButton btnClear = new JButton("Clear Form");
        btnClear.setFocusPainted(false);

        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnClear);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        pnlForm.add(pnlButtons, gbc);

        pnlCenter.add(pnlForm, BorderLayout.WEST);

        // Table & Filter Panel
        JPanel pnlTableContainer = new JPanel(new BorderLayout(8, 8));

        // Filters Toolbar
        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlFilters.setBorder(BorderFactory.createTitledBorder("Filters"));

        pnlFilters.add(new JLabel("Course:"));
        cmbFilterCourse = new JComboBox<>();
        cmbFilterCourse.addItem("ALL COURSES");
        pnlFilters.add(cmbFilterCourse);

        pnlFilters.add(new JLabel("Session Type:"));
        cmbFilterType = new JComboBox<>(new String[]{"ALL", "THEORY", "PRACTICAL"});
        pnlFilters.add(cmbFilterType);

        JButton btnApplyFilter = new JButton("Filter");
        JButton btnRefresh = new JButton("Refresh");
        pnlFilters.add(btnApplyFilter);
        pnlFilters.add(btnRefresh);

        pnlTableContainer.add(pnlFilters, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Student TG", "Student Name", "Course Code", "Course Name", "Session Date", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only view in table
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(24);
        attendanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        pnlTableContainer.add(scrollPane, BorderLayout.CENTER);

        pnlCenter.add(pnlTableContainer, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // Action Listeners
        btnAdd.addActionListener(e -> onAddAttendance());
        btnUpdate.addActionListener(e -> onUpdateAttendance());
        btnDelete.addActionListener(e -> onDeleteAttendance());
        btnClear.addActionListener(e -> clearForm());
        btnApplyFilter.addActionListener(e -> loadAttendanceData());
        btnRefresh.addActionListener(e -> {
            loadDropdownData();
            loadAttendanceData();
        });

        attendanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && attendanceTable.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });
    }

    private void loadDropdownData() {
        cmbStudent.removeAllItems();
        List<DataHelper.StudentItem> students = DataHelper.getAllStudents();
        for (DataHelper.StudentItem s : students) {
            cmbStudent.addItem(s);
        }

        cmbCourse.removeAllItems();
        cmbFilterCourse.removeAllItems();
        cmbFilterCourse.addItem("ALL COURSES");

        List<DataHelper.CourseItem> courses = DataHelper.getAllCourses();
        for (DataHelper.CourseItem c : courses) {
            cmbCourse.addItem(c);
            cmbFilterCourse.addItem(c.getCourseCode());
        }
    }

    private void loadAttendanceData() {
        tableModel.setRowCount(0);

        String selectedCourse = (String) cmbFilterCourse.getSelectedItem();
        if ("ALL COURSES".equals(selectedCourse)) {
            selectedCourse = null;
        }

        String selectedType = (String) cmbFilterType.getSelectedItem();
        if ("ALL".equalsIgnoreCase(selectedType)) {
            selectedType = null;
        }

        try {
            List<Attendance> records = Attendance.getAttendanceRecords(selectedCourse, null, selectedType);
            for (Attendance a : records) {
                tableModel.addRow(new Object[]{
                        a.getAttendanceId(),
                        a.getTgNum(),
                        a.getStudentName(),
                        a.getCourseCode(),
                        a.getCourseName(),
                        a.getSessionDate(),
                        a.getType()
                });
            }
        } catch (SQLException ex) {
            UiUtils.showError(this, "Error loading attendance records: " + ex.getMessage(), "Database Error");
        }
    }

    private void populateFormFromSelectedRow() {
        int row = attendanceTable.getSelectedRow();
        if (row >= 0) {
            txtAttendanceId.setText(tableModel.getValueAt(row, 0).toString());
            String tg = (String) tableModel.getValueAt(row, 1);
            String courseCode = (String) tableModel.getValueAt(row, 3);
            Object dateObj = tableModel.getValueAt(row, 5);
            String type = (String) tableModel.getValueAt(row, 6);

            // Match student combo
            for (int i = 0; i < cmbStudent.getItemCount(); i++) {
                DataHelper.StudentItem item = cmbStudent.getItemAt(i);
                if (item.getTgNum().equals(tg)) {
                    cmbStudent.setSelectedIndex(i);
                    break;
                }
            }

            // Match course combo
            for (int i = 0; i < cmbCourse.getItemCount(); i++) {
                DataHelper.CourseItem item = cmbCourse.getItemAt(i);
                if (item.getCourseCode().equals(courseCode)) {
                    cmbCourse.setSelectedIndex(i);
                    break;
                }
            }

            txtSessionDate.setText(dateObj != null ? dateObj.toString() : "");
            cmbType.setSelectedItem(type);
        }
    }

    private void onAddAttendance() {
        DataHelper.StudentItem student = (DataHelper.StudentItem) cmbStudent.getSelectedItem();
        DataHelper.CourseItem course = (DataHelper.CourseItem) cmbCourse.getSelectedItem();
        String dateStr = txtSessionDate.getText().trim();
        String type = (String) cmbType.getSelectedItem();

        if (student == null || course == null || dateStr.isEmpty() || type == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate sessionDate;
        try {
            sessionDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid Date format. Use YYYY-MM-DD.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = technicalOfficer.addAttendance(student.getStudentId(), course.getCourseCode(), sessionDate, type);
            if (success) {
                JOptionPane.showMessageDialog(this, "Attendance recorded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAttendanceData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to record attendance.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdateAttendance() {
        String idStr = txtAttendanceId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an attendance record from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int attendanceId = Integer.parseInt(idStr);
        DataHelper.StudentItem student = (DataHelper.StudentItem) cmbStudent.getSelectedItem();
        DataHelper.CourseItem course = (DataHelper.CourseItem) cmbCourse.getSelectedItem();
        String dateStr = txtSessionDate.getText().trim();
        String type = (String) cmbType.getSelectedItem();

        if (student == null || course == null || dateStr.isEmpty() || type == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate sessionDate;
        try {
            sessionDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid Date format. Use YYYY-MM-DD.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = technicalOfficer.maintainAttendance(attendanceId, student.getStudentId(), course.getCourseCode(), sessionDate, type);
            if (success) {
                JOptionPane.showMessageDialog(this, "Attendance updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAttendanceData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update attendance record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeleteAttendance() {
        String idStr = txtAttendanceId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an attendance record from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int attendanceId = Integer.parseInt(idStr);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete attendance record #" + attendanceId + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = technicalOfficer.deleteAttendance(attendanceId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Attendance record deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadAttendanceData();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete attendance record.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtAttendanceId.setText("");
        txtSessionDate.setText(LocalDate.now().toString());
        if (cmbStudent.getItemCount() > 0) cmbStudent.setSelectedIndex(0);
        if (cmbCourse.getItemCount() > 0) cmbCourse.setSelectedIndex(0);
        cmbType.setSelectedIndex(0);
        attendanceTable.clearSelection();
    }
}
