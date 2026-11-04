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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panel for adding, updating, deleting, and viewing student attendance records.
 */
public class AddAttendancePanel extends JPanel {

    // Theme colours matching the FTMS screenshot
    private static final Color BG_MAIN = new Color(245, 248, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(224, 232, 240);
    private static final Color NAVY_DARK = new Color(11, 46, 74);
    private static final Color TEAL_ACCENT = new Color(0, 131, 155);
    private static final Color TEXT_MUTED = new Color(110, 125, 140);
    private static final Color BTN_DANGER = new Color(205, 58, 58);
    private static final Color BTN_NEUTRAL = new Color(108, 122, 137);

    private final AttendanceDAO attendanceDAO;

    private JTextField txtAttendanceId;
    private JTextField txtStudentId;
    private JComboBox<String> cmbCourseCode;
    private JTextField txtSessionDate;
    private JComboBox<String> cmbSessionType;
    private JComboBox<String> cmbStatus;

    private JTextField txtSearchStudentId;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JLabel lblRecordCount;

    public AddAttendancePanel() {
        this.attendanceDAO = new AttendanceDAO();
        initComponents();
        loadAttendanceRecords();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(4, 0, 0, 0));

        // 1. Form Card (Top)
        JPanel formCard = createCardPanel();
        formCard.setLayout(new BorderLayout(0, 14));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(18, 22, 18, 22)
        ));

        JLabel lblFormTitle = new JLabel("Record / Manage Student Attendance");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(NAVY_DARK);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtAttendanceId = createTextField();
        txtAttendanceId.setEditable(false);
        txtAttendanceId.setBackground(new Color(240, 244, 248));
        txtAttendanceId.setText("Auto");

        txtStudentId = createTextField();
        txtStudentId.setToolTipText("Enter Student ID (e.g., 11 - 30)");

        cmbCourseCode = new JComboBox<>(new String[]{"ICT2132", "ICT2142", "ICT2152", "ICT2162"});
        cmbCourseCode.setEditable(true);
        styleComboBox(cmbCourseCode);

        txtSessionDate = createTextField();
        txtSessionDate.setText(LocalDate.now().toString());
        txtSessionDate.setToolTipText("Format: YYYY-MM-DD");

        cmbSessionType = new JComboBox<>(new String[]{Attendance.TYPE_THEORY, Attendance.TYPE_PRACTICAL});
        styleComboBox(cmbSessionType);

        cmbStatus = new JComboBox<>(new String[]{"PRESENT", "ABSENT"});
        styleComboBox(cmbStatus);

        // Row 1: Attendance ID, Student ID, Course Code
        addFormField(fieldsPanel, gbc, 0, 0, "Attendance ID", txtAttendanceId);
        addFormField(fieldsPanel, gbc, 1, 0, "Student ID *", txtStudentId);
        addFormField(fieldsPanel, gbc, 2, 0, "Course Code *", cmbCourseCode);

        // Row 2: Session Date, Session Type, Attendance Status
        addFormField(fieldsPanel, gbc, 0, 1, "Session Date (YYYY-MM-DD) *", txtSessionDate);
        addFormField(fieldsPanel, gbc, 1, 1, "Session Type *", cmbSessionType);
        addFormField(fieldsPanel, gbc, 2, 1, "Attendance Status *", cmbStatus);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons Bar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton btnSave = createButton("Save", TEAL_ACCENT);
        JButton btnUpdate = createButton("Update", NAVY_DARK);
        JButton btnDelete = createButton("Delete", BTN_DANGER);
        JButton btnClear = createButton("Clear", BTN_NEUTRAL);
        JButton btnRefresh = createButton("Refresh", new Color(36, 113, 163));

        btnSave.addActionListener(e -> saveAttendance());
        btnUpdate.addActionListener(e -> updateAttendance());
        btnDelete.addActionListener(e -> deleteAttendance());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> {
            txtSearchStudentId.setText("");
            loadAttendanceRecords();
        });

        buttonPanel.add(btnSave);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);

        formCard.add(buttonPanel, BorderLayout.SOUTH);
        add(formCard, BorderLayout.NORTH);

        // 2. Table Card (Center)
        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 12));
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(16, 22, 18, 22)
        ));

        JPanel tableTopBar = new JPanel(new BorderLayout());
        tableTopBar.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Attendance Records");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTableTitle.setForeground(NAVY_DARK);
        tableTopBar.add(lblTableTitle, BorderLayout.WEST);

        JPanel quickFilterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        quickFilterPanel.setOpaque(false);

        JLabel lblFilterStudent = new JLabel("Quick Filter by Student ID:");
        lblFilterStudent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFilterStudent.setForeground(TEXT_MUTED);

        txtSearchStudentId = createTextField();
        txtSearchStudentId.setPreferredSize(new Dimension(100, 30));

        JButton btnFilter = createButton("Filter", TEAL_ACCENT);
        btnFilter.setPreferredSize(new Dimension(80, 30));
        btnFilter.addActionListener(e -> filterByStudent());

        lblRecordCount = new JLabel("0 records");
        lblRecordCount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRecordCount.setForeground(TEAL_ACCENT);

        quickFilterPanel.add(lblFilterStudent);
        quickFilterPanel.add(txtSearchStudentId);
        quickFilterPanel.add(btnFilter);
        quickFilterPanel.add(Box.createHorizontalStrut(8));
        quickFilterPanel.add(lblRecordCount);

        tableTopBar.add(quickFilterPanel, BorderLayout.EAST);
        tableCard.add(tableTopBar, BorderLayout.NORTH);

        String[] columns = {"Attendance ID", "Student ID", "Course Code", "Session Date", "Session Type", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        attendanceTable = new JTable(tableModel);
        styleTable(attendanceTable);

        attendanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFormFromSelectedRow();
            }
        });

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(new LineBorder(CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int col, int row, String labelText, JComponent comp) {
        JPanel fieldWrapper = new JPanel(new BorderLayout(0, 5));
        fieldWrapper.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(NAVY_DARK);

        comp.setPreferredSize(new Dimension(180, 34));
        fieldWrapper.add(label, BorderLayout.NORTH);
        fieldWrapper.add(comp, BorderLayout.CENTER);

        gbc.gridx = col;
        gbc.gridy = row;
        panel.add(fieldWrapper, gbc);
    }

    public void loadAttendanceRecords() {
        List<Attendance> records = attendanceDAO.getAllAttendance();
        populateTable(records);
    }

    private void filterByStudent() {
        String input = txtSearchStudentId.getText().trim();
        if (input.isEmpty()) {
            loadAttendanceRecords();
            return;
        }
        try {
            int studentId = Integer.parseInt(input);
            List<Attendance> records = attendanceDAO.getAttendanceByStudentId(studentId);
            populateTable(records);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void populateTable(List<Attendance> records) {
        tableModel.setRowCount(0);
        for (Attendance a : records) {
            tableModel.addRow(new Object[]{
                    a.getAttendanceId(),
                    a.getStudentId(),
                    a.getCourseCode(),
                    a.getSessionDate() != null ? a.getSessionDate().toString() : "",
                    a.getType(),
                    a.isPresent() ? "PRESENT" : "ABSENT"
            });
        }
        lblRecordCount.setText(records.size() + " records");
    }

    private void populateFormFromSelectedRow() {
        int row = attendanceTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        txtAttendanceId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtStudentId.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        cmbCourseCode.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 2)));
        txtSessionDate.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        cmbSessionType.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 4)));
        cmbStatus.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void saveAttendance() {
        try {
            Attendance attendance = buildAttendanceFromForm(false);
            if (attendanceDAO.addAttendance(attendance)) {
                JOptionPane.showMessageDialog(this,
                        "Attendance saved successfully! (ID: " + attendance.getAttendanceId() + ")",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAttendanceRecords();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to save attendance. Please verify Student ID and Course Code exist.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateAttendance() {
        String idText = txtAttendanceId.getText().trim();
        if (idText.isEmpty() || "Auto".equalsIgnoreCase(idText)) {
            JOptionPane.showMessageDialog(this, "Please select an attendance record from the table to update.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Attendance attendance = buildAttendanceFromForm(true);
            if (attendanceDAO.updateAttendance(attendance)) {
                JOptionPane.showMessageDialog(this, "Attendance record updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAttendanceRecords();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update attendance record.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteAttendance() {
        String idText = txtAttendanceId.getText().trim();
        if (idText.isEmpty() || "Auto".equalsIgnoreCase(idText)) {
            JOptionPane.showMessageDialog(this, "Please select an attendance record from the table to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Attendance ID " + idText + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int attendanceId = Integer.parseInt(idText);
            if (attendanceDAO.deleteAttendance(attendanceId)) {
                JOptionPane.showMessageDialog(this, "Attendance record deleted successfully!",
                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAttendanceRecords();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not delete record. It may be referenced by a medical record.",
                        "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Attendance ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Attendance buildAttendanceFromForm(boolean includeId) {
        String studentStr = txtStudentId.getText().trim();
        if (studentStr.isEmpty()) {
            throw new IllegalArgumentException("Student ID is required.");
        }
        int studentId;
        try {
            studentId = Integer.parseInt(studentStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Student ID must be a valid integer (e.g., 11).");
        }

        Object courseObj = cmbCourseCode.getSelectedItem();
        String courseCode = courseObj != null ? courseObj.toString().trim() : "";
        if (courseCode.isEmpty()) {
            throw new IllegalArgumentException("Course Code is required.");
        }

        String dateStr = txtSessionDate.getText().trim();
        if (dateStr.isEmpty()) {
            throw new IllegalArgumentException("Session Date is required (YYYY-MM-DD).");
        }
        LocalDate sessionDate;
        try {
            sessionDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Session Date must be in YYYY-MM-DD format (e.g., 2026-06-01).");
        }

        String type = String.valueOf(cmbSessionType.getSelectedItem());
        boolean isPresent = "PRESENT".equalsIgnoreCase(String.valueOf(cmbStatus.getSelectedItem()));

        if (includeId) {
            int attendanceId = Integer.parseInt(txtAttendanceId.getText().trim());
            return new Attendance(attendanceId, studentId, courseCode, sessionDate, type, isPresent);
        }
        return new Attendance(studentId, courseCode, sessionDate, type, isPresent);
    }

    private void clearForm() {
        txtAttendanceId.setText("Auto");
        txtStudentId.setText("");
        cmbCourseCode.setSelectedIndex(0);
        txtSessionDate.setText(LocalDate.now().toString());
        cmbSessionType.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        attendanceTable.clearSelection();
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        return panel;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(NAVY_DARK);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(206, 216, 226), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        return tf;
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        box.setBackground(Color.WHITE);
        box.setForeground(NAVY_DARK);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(105, 34));
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
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(NAVY_DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 5) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Status column badge colour renderer
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String status = value != null ? value.toString() : "";
                if ("PRESENT".equalsIgnoreCase(status)) {
                    lbl.setForeground(new Color(22, 135, 75));
                } else {
                    lbl.setForeground(BTN_DANGER);
                }
                return lbl;
            }
        });
    }
}
