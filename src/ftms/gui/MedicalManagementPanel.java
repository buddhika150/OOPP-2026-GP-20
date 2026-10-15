package ftms.gui;

import ftms.db.DataHelper;
import ftms.model.Medical;
import ftms.model.TechnicalOfficer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Java Swing GUI Panel for Medical Entry, Maintenance, and Approval.
 * Assigned to Member 3.
 */
public class MedicalManagementPanel extends JPanel {
    private final TechnicalOfficer technicalOfficer;

    // Form inputs
    private JTextField txtMedicalId;
    private JComboBox<DataHelper.StudentItem> cmbStudent;
    private JComboBox<Object> cmbAttendance; // Attendance session dropdown
    private JTextField txtSubmissionDate;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTextArea txtReason;
    private JComboBox<String> cmbApprovalStatus;
    private JTextField txtStatus;

    // Filters
    private JComboBox<String> cmbFilterStatus;

    // Table
    private JTable medicalTable;
    private DefaultTableModel tableModel;

    public MedicalManagementPanel(TechnicalOfficer technicalOfficer) {
        this.technicalOfficer = technicalOfficer;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        loadStudents();
        loadMedicalRecords();
    }

    private void initComponents() {
        // TOP: Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Medical Records & Approval Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // CENTER: Form on Left, Table on Right
        JPanel pnlCenter = new JPanel(new BorderLayout(15, 15));

        // Form Panel
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Medical Certificate Details", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));
        pnlForm.setPreferredSize(new Dimension(380, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // Medical ID
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(new JLabel("Medical ID:"), gbc);
        txtMedicalId = new JTextField();
        txtMedicalId.setEditable(false);
        txtMedicalId.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(txtMedicalId, gbc);

        // Student
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(new JLabel("Student: *"), gbc);
        cmbStudent = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(cmbStudent, gbc);

        // Linked Attendance Session
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(new JLabel("Linked Session:"), gbc);
        cmbAttendance = new JComboBox<>();
        cmbAttendance.addItem("(None / Not linked)");
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(cmbAttendance, gbc);

        // Submission Date
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(new JLabel("Submission Date: *"), gbc);
        txtSubmissionDate = new JTextField(LocalDate.now().toString());
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(txtSubmissionDate, gbc);

        // Start Date
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(new JLabel("Start Date: *"), gbc);
        txtStartDate = new JTextField(LocalDate.now().toString());
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(txtStartDate, gbc);

        // End Date
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(new JLabel("End Date: *"), gbc);
        txtEndDate = new JTextField(LocalDate.now().toString());
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(txtEndDate, gbc);

        // Approval Status
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(new JLabel("Approval Status: *"), gbc);
        cmbApprovalStatus = new JComboBox<>(new String[]{"PENDING", "APPROVED", "REJECTED"});
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(cmbApprovalStatus, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(new JLabel("Status Note:"), gbc);
        txtStatus = new JTextField("SUBMITTED");
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(txtStatus, gbc);

        // Reason
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(new JLabel("Medical Reason:"), gbc);
        txtReason = new JTextArea(3, 20);
        txtReason.setLineWrap(true);
        txtReason.setWrapStyleWord(true);
        JScrollPane scrollReason = new JScrollPane(txtReason);
        gbc.gridx = 1; gbc.gridy = row++;
        pnlForm.add(scrollReason, gbc);

        // Action Buttons Grid
        JPanel pnlButtons = new JPanel(new GridLayout(3, 2, 6, 6));

        JButton btnAdd = new JButton("Add Medical");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);

        JButton btnUpdate = new JButton("Update Medical");
        btnUpdate.setBackground(new Color(0, 123, 255));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);

        JButton btnApprove = new JButton("Approve (Status)");
        btnApprove.setBackground(new Color(25, 135, 84));
        btnApprove.setForeground(Color.WHITE);
        btnApprove.setFocusPainted(false);

        JButton btnReject = new JButton("Reject (Status)");
        btnReject.setBackground(new Color(220, 53, 69));
        btnReject.setForeground(Color.WHITE);
        btnReject.setFocusPainted(false);

        JButton btnDelete = new JButton("Delete Medical");
        btnDelete.setBackground(new Color(108, 117, 125));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);

        JButton btnClear = new JButton("Clear Form");
        btnClear.setFocusPainted(false);

        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnApprove);
        pnlButtons.add(btnReject);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnClear);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 8, 8, 8);
        pnlForm.add(pnlButtons, gbc);

        pnlCenter.add(pnlForm, BorderLayout.WEST);

        // Table & Filters
        JPanel pnlTableContainer = new JPanel(new BorderLayout(8, 8));

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlFilters.setBorder(BorderFactory.createTitledBorder("Filter Medicals"));

        pnlFilters.add(new JLabel("Status:"));
        cmbFilterStatus = new JComboBox<>(new String[]{"ALL", "PENDING", "APPROVED", "REJECTED"});
        pnlFilters.add(cmbFilterStatus);

        JButton btnFilter = new JButton("Apply Filter");
        JButton btnViewDetails = new JButton("View Medical Details");
        JButton btnRefresh = new JButton("Refresh");

        pnlFilters.add(btnFilter);
        pnlFilters.add(btnViewDetails);
        pnlFilters.add(btnRefresh);

        pnlTableContainer.add(pnlFilters, BorderLayout.NORTH);

        // Table
        String[] columns = {
                "ID", "Student TG", "Student Name", "Start Date", "End Date",
                "Linked Session", "Approval Status", "Status", "Reason"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        medicalTable = new JTable(tableModel);
        medicalTable.setRowHeight(24);
        medicalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Approval status column highlight
        medicalTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String status = value.toString();
                    if ("APPROVED".equalsIgnoreCase(status)) {
                        c.setForeground(new Color(25, 135, 84));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("REJECTED".equalsIgnoreCase(status)) {
                        c.setForeground(new Color(220, 53, 69));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(255, 140, 0)); // Dark Orange for PENDING
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(medicalTable);
        pnlTableContainer.add(scrollPane, BorderLayout.CENTER);

        pnlCenter.add(pnlTableContainer, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // Listeners
        btnAdd.addActionListener(e -> onAddMedical());
        btnUpdate.addActionListener(e -> onUpdateMedical());
        btnApprove.addActionListener(e -> onQuickChangeStatus("APPROVED"));
        btnReject.addActionListener(e -> onQuickChangeStatus("REJECTED"));
        btnDelete.addActionListener(e -> onDeleteMedical());
        btnClear.addActionListener(e -> clearForm());
        btnFilter.addActionListener(e -> loadMedicalRecords());
        btnRefresh.addActionListener(e -> {
            loadStudents();
            loadMedicalRecords();
        });
        btnViewDetails.addActionListener(e -> onViewMedicalDetails());

        cmbStudent.addActionListener(e -> updateAttendanceDropdown());

        medicalTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && medicalTable.getSelectedRow() != -1) {
                populateFormFromRow();
            }
        });
    }

    private void loadStudents() {
        cmbStudent.removeAllItems();
        for (DataHelper.StudentItem s : DataHelper.getAllStudents()) {
            cmbStudent.addItem(s);
        }
        updateAttendanceDropdown();
    }

    private void updateAttendanceDropdown() {
        cmbAttendance.removeAllItems();
        cmbAttendance.addItem("(None / Not linked)");

        DataHelper.StudentItem s = (DataHelper.StudentItem) cmbStudent.getSelectedItem();
        if (s != null) {
            List<DataHelper.AttendanceItem> attList = DataHelper.getAttendanceForStudent(s.getStudentId());
            for (DataHelper.AttendanceItem a : attList) {
                cmbAttendance.addItem(a);
            }
        }
    }

    private void loadMedicalRecords() {
        tableModel.setRowCount(0);

        String statusFilter = (String) cmbFilterStatus.getSelectedItem();
        if ("ALL".equalsIgnoreCase(statusFilter)) {
            statusFilter = null;
        }

        try {
            List<Medical> list = Medical.getAllMedicals(null, statusFilter);
            for (Medical m : list) {
                String linkedInfo = m.getAttendanceId() != null && m.getAttendanceId() > 0
                        ? "#" + m.getAttendanceId() + " (" + m.getCourseCode() + " " + m.getSessionType() + ")"
                        : "None";

                tableModel.addRow(new Object[]{
                        m.getMedicalId(),
                        m.getTgNum(),
                        m.getStudentName(),
                        m.getStartDate(),
                        m.getEndDate(),
                        linkedInfo,
                        m.getApprovalStatus(),
                        m.getStatus(),
                        m.getReason()
                });
            }
        } catch (SQLException ex) {
            UiUtils.showError(this, "Error loading medical records: " + ex.getMessage(), "Database Error");
        }
    }

    private void populateFormFromRow() {
        int row = medicalTable.getSelectedRow();
        if (row >= 0) {
            txtMedicalId.setText(tableModel.getValueAt(row, 0).toString());
            String tg = (String) tableModel.getValueAt(row, 1);
            Object startObj = tableModel.getValueAt(row, 3);
            Object endObj = tableModel.getValueAt(row, 4);
            String approvalStatus = (String) tableModel.getValueAt(row, 6);
            String status = (String) tableModel.getValueAt(row, 7);
            String reason = (String) tableModel.getValueAt(row, 8);

            // Select student in combo
            for (int i = 0; i < cmbStudent.getItemCount(); i++) {
                DataHelper.StudentItem item = cmbStudent.getItemAt(i);
                if (item.getTgNum().equals(tg)) {
                    cmbStudent.setSelectedIndex(i);
                    break;
                }
            }

            txtStartDate.setText(startObj != null ? startObj.toString() : "");
            txtEndDate.setText(endObj != null ? endObj.toString() : "");
            cmbApprovalStatus.setSelectedItem(approvalStatus);
            txtStatus.setText(status != null ? status : "");
            txtReason.setText(reason != null ? reason : "");
        }
    }

    private void onAddMedical() {
        DataHelper.StudentItem student = (DataHelper.StudentItem) cmbStudent.getSelectedItem();
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Please select a student.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer attId = null;
        Object selAtt = cmbAttendance.getSelectedItem();
        if (selAtt instanceof DataHelper.AttendanceItem) {
            attId = ((DataHelper.AttendanceItem) selAtt).getAttendanceId();
        }

        LocalDate subDate;
        LocalDate startDate;
        LocalDate endDate;

        try {
            subDate = LocalDate.parse(txtSubmissionDate.getText().trim());
            startDate = LocalDate.parse(txtStartDate.getText().trim());
            endDate = LocalDate.parse(txtEndDate.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid Date format. Use YYYY-MM-DD.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reason = txtReason.getText().trim();
        String approvalStatus = (String) cmbApprovalStatus.getSelectedItem();
        String status = txtStatus.getText().trim();

        try {
            boolean success = technicalOfficer.addMedical(
                    student.getStudentId(), attId, subDate, startDate, endDate, reason, approvalStatus, status
            );
            if (success) {
                JOptionPane.showMessageDialog(this, "Medical record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadMedicalRecords();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add medical record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdateMedical() {
        String idStr = txtMedicalId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a medical record from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int medicalId = Integer.parseInt(idStr);
        DataHelper.StudentItem student = (DataHelper.StudentItem) cmbStudent.getSelectedItem();
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Please select a student.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer attId = null;
        Object selAtt = cmbAttendance.getSelectedItem();
        if (selAtt instanceof DataHelper.AttendanceItem) {
            attId = ((DataHelper.AttendanceItem) selAtt).getAttendanceId();
        }

        LocalDate subDate;
        LocalDate startDate;
        LocalDate endDate;

        try {
            subDate = LocalDate.parse(txtSubmissionDate.getText().trim());
            startDate = LocalDate.parse(txtStartDate.getText().trim());
            endDate = LocalDate.parse(txtEndDate.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid Date format. Use YYYY-MM-DD.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reason = txtReason.getText().trim();
        String approvalStatus = (String) cmbApprovalStatus.getSelectedItem();
        String status = txtStatus.getText().trim();

        try {
            boolean success = technicalOfficer.maintainMedical(
                    medicalId, student.getStudentId(), attId, subDate, startDate, endDate, reason, approvalStatus, status
            );
            if (success) {
                JOptionPane.showMessageDialog(this, "Medical record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadMedicalRecords();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update medical record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onQuickChangeStatus(String newStatus) {
        String idStr = txtMedicalId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a medical record from the table to change status.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int medicalId = Integer.parseInt(idStr);
        try {
            boolean success = technicalOfficer.approveMedical(medicalId, newStatus);
            if (success) {
                JOptionPane.showMessageDialog(this, "Medical record status changed to: " + newStatus, "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                loadMedicalRecords();
                cmbApprovalStatus.setSelectedItem(newStatus);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update approval status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeleteMedical() {
        String idStr = txtMedicalId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a medical record to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int medicalId = Integer.parseInt(idStr);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete medical record #" + medicalId + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = technicalOfficer.deleteMedical(medicalId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Medical record deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadMedicalRecords();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete medical record.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onViewMedicalDetails() {
        String idStr = txtMedicalId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a medical record from the table to view full details.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int medicalId = Integer.parseInt(idStr);
        Medical med = new Medical();
        med.setMedicalId(medicalId);
        try {
            if (med.viewMedicalDetails()) {
                String details = String.format(
                        "Medical ID: %d\n" +
                        "Student TG: %s\n" +
                        "Student Name: %s\n" +
                        "Submission Date: %s\n" +
                        "Covered Period: %s to %s\n" +
                        "Approval Status: %s\n" +
                        "Record Status: %s\n" +
                        "Linked Attendance: %s\n" +
                        "Reason: %s",
                        med.getMedicalId(),
                        med.getTgNum(),
                        med.getStudentName(),
                        med.getSubmissionDate(),
                        med.getStartDate(),
                        med.getEndDate(),
                        med.getApprovalStatus(),
                        med.getStatus(),
                        med.getAttendanceId() != null ? ("#" + med.getAttendanceId() + " (" + med.getCourseCode() + " " + med.getSessionType() + " on " + med.getSessionDate() + ")") : "Not linked",
                        med.getReason()
                );
                JTextArea area = new JTextArea(details);
                area.setEditable(false);
                area.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JOptionPane.showMessageDialog(this, new JScrollPane(area), "Medical Record Details #" + medicalId, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Record not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtMedicalId.setText("");
        txtSubmissionDate.setText(LocalDate.now().toString());
        txtStartDate.setText(LocalDate.now().toString());
        txtEndDate.setText(LocalDate.now().toString());
        txtReason.setText("");
        txtStatus.setText("SUBMITTED");
        cmbApprovalStatus.setSelectedIndex(0);
        if (cmbStudent.getItemCount() > 0) cmbStudent.setSelectedIndex(0);
        medicalTable.clearSelection();
    }
}
