package com.ftms.view.technicalofficer;

import com.ftms.dao.MedicalDAO;
import com.ftms.model.Medical;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panel for adding, updating, deleting, and viewing student medical records,
 * storing the uploaded medical document/image path in the medicalimg column.
 */
public class AddMedicalPanel extends JPanel {

    private static final Color BG_MAIN = new Color(245, 248, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(224, 232, 240);
    private static final Color NAVY_DARK = new Color(11, 46, 74);
    private static final Color TEAL_ACCENT = new Color(0, 131, 155);
    private static final Color BTN_DANGER = new Color(205, 58, 58);
    private static final Color BTN_NEUTRAL = new Color(108, 122, 137);
    private static final Color GREEN_OK = new Color(22, 135, 75);
    private static final Color AMBER_PENDING = new Color(195, 125, 15);

    private static final String UPLOAD_DIR = "uploads/medicals";

    private final MedicalDAO medicalDAO;

    private JTextField txtMedicalId;
    private JTextField txtStudentId;
    private JTextField txtAttendanceId;
    private JTextField txtSubmissionDate;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTextField txtReason;
    private JComboBox<String> cmbApprovalStatus;

    // Document / Image upload controls for medicalimg column
    private JTextField txtDocumentPath;
    private File selectedDocumentFile;

    private JTable medicalTable;
    private DefaultTableModel tableModel;
    private JLabel lblRecordCount;

    public AddMedicalPanel() {
        this.medicalDAO = new MedicalDAO();
        initComponents();
        loadMedicalRecords();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(4, 0, 0, 0));

        // 1. Form Card (Top)
        JPanel formCard = new JPanel(new BorderLayout(0, 14));
        formCard.setBackground(CARD_BG);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(18, 22, 18, 22)
        ));

        JLabel lblTitle = new JLabel("Submit / Manage Student Medical Record & Certificate (medicalimg)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(NAVY_DARK);
        formCard.add(lblTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtMedicalId = createTextField();
        txtMedicalId.setEditable(false);
        txtMedicalId.setBackground(new Color(240, 244, 248));
        txtMedicalId.setText("Auto");

        txtStudentId = createTextField();
        txtStudentId.setToolTipText("Required Student ID (e.g., 11 - 30)");

        txtAttendanceId = createTextField();
        txtAttendanceId.setToolTipText("Optional Attendance ID (leave blank if not linked to a single session)");

        String today = LocalDate.now().toString();
        txtSubmissionDate = createTextField();
        txtSubmissionDate.setText(today);

        txtStartDate = createTextField();
        txtStartDate.setText(today);

        txtEndDate = createTextField();
        txtEndDate.setText(today);

        txtReason = createTextField();
        txtReason.setToolTipText("Reason or medical diagnosis notes");

        cmbApprovalStatus = new JComboBox<>(new String[]{
                Medical.APPROVAL_PENDING,
                Medical.APPROVAL_APPROVED,
                Medical.APPROVAL_REJECTED
        });
        cmbApprovalStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbApprovalStatus.setBackground(Color.WHITE);
        cmbApprovalStatus.setForeground(NAVY_DARK);

        // Row 1: Medical ID, Student ID, Attendance ID, Approval Status
        addFormField(fieldsPanel, gbc, 0, 0, 1, "Medical ID", txtMedicalId);
        addFormField(fieldsPanel, gbc, 1, 0, 1, "Student ID *", txtStudentId);
        addFormField(fieldsPanel, gbc, 2, 0, 1, "Attendance ID (Optional)", txtAttendanceId);
        addFormField(fieldsPanel, gbc, 3, 0, 1, "Approval Status *", cmbApprovalStatus);

        // Row 2: Submission Date, Start Date, End Date, Reason
        addFormField(fieldsPanel, gbc, 0, 1, 1, "Submission Date (YYYY-MM-DD)", txtSubmissionDate);
        addFormField(fieldsPanel, gbc, 1, 1, 1, "Start Date (YYYY-MM-DD) *", txtStartDate);
        addFormField(fieldsPanel, gbc, 2, 1, "End Date (YYYY-MM-DD) *", txtEndDate);
        addFormField(fieldsPanel, gbc, 3, 1, 1, "Reason *", txtReason);

        // Row 3: Medical Certificate Document / Image Chooser (medicalimg column)
        JPanel docPickerPanel = new JPanel(new BorderLayout(8, 0));
        docPickerPanel.setOpaque(false);

        txtDocumentPath = createTextField();
        txtDocumentPath.setEditable(false);
        txtDocumentPath.setBackground(new Color(248, 250, 252));
        txtDocumentPath.setToolTipText("Selected medical image/document path stored in the 'medicalimg' column");

        JPanel docButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        docButtons.setOpaque(false);

        JButton btnBrowseDoc = createButton("Browse Document...", TEAL_ACCENT, 155);
        JButton btnViewDoc = createButton("Open / View", new Color(36, 113, 163), 120);
        JButton btnRemoveDoc = createButton("Remove", BTN_NEUTRAL, 90);

        btnBrowseDoc.addActionListener(e -> chooseMedicalDocument());
        btnViewDoc.addActionListener(e -> openAttachedDocument());
        btnRemoveDoc.addActionListener(e -> {
            selectedDocumentFile = null;
            txtDocumentPath.setText("");
        });

        docButtons.add(btnBrowseDoc);
        docButtons.add(btnViewDoc);
        docButtons.add(btnRemoveDoc);

        docPickerPanel.add(txtDocumentPath, BorderLayout.CENTER);
        docPickerPanel.add(docButtons, BorderLayout.EAST);

        addFormField(fieldsPanel, gbc, 0, 2, 4, "Medical Document / Image (medicalimg - PDF, PNG, JPG)", docPickerPanel);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Action Buttons Bar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton btnSave = createButton("Save", TEAL_ACCENT, 105);
        JButton btnUpdate = createButton("Update", NAVY_DARK, 105);
        JButton btnDelete = createButton("Delete", BTN_DANGER, 105);
        JButton btnClear = createButton("Clear", BTN_NEUTRAL, 105);
        JButton btnRefresh = createButton("Refresh", new Color(36, 113, 163), 105);

        btnSave.addActionListener(e -> saveMedical());
        btnUpdate.addActionListener(e -> updateMedical());
        btnDelete.addActionListener(e -> deleteMedical());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadMedicalRecords());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);

        formCard.add(buttonPanel, BorderLayout.SOUTH);
        add(formCard, BorderLayout.NORTH);

        // 2. Table Card (Center)
        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(CARD_BG);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(16, 22, 18, 22)
        ));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Medical Records (Select a row and click 'Open / View' to inspect medicalimg)");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTableTitle.setForeground(NAVY_DARK);

        lblRecordCount = new JLabel("0 records");
        lblRecordCount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRecordCount.setForeground(TEAL_ACCENT);

        topBar.add(lblTableTitle, BorderLayout.WEST);
        topBar.add(lblRecordCount, BorderLayout.EAST);
        tableCard.add(topBar, BorderLayout.NORTH);

        String[] columns = {
                "Medical ID",
                "Student ID",
                "Attendance ID",
                "Submission Date",
                "Start Date",
                "End Date",
                "Reason",
                "Medical Document (medicalimg)",
                "Approval Status",
                "Record Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medicalTable = new JTable(tableModel);
        styleTable(medicalTable);

        medicalTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFormFromSelectedRow();
            }
        });

        JScrollPane scrollPane = new JScrollPane(medicalTable);
        scrollPane.setBorder(new LineBorder(CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int col, int row, String labelText, JComponent comp) {
        addFormField(panel, gbc, col, row, 1, labelText, comp);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int col, int row, int width,
                              String labelText, JComponent comp) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));
        wrapper.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(NAVY_DARK);

        comp.setPreferredSize(new Dimension(160, 34));
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(comp, BorderLayout.CENTER);

        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = width;
        panel.add(wrapper, gbc);
        gbc.gridwidth = 1;
    }

    private void chooseMedicalDocument() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Medical Certificate Document / Image");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Medical Documents & Images (PDF, PNG, JPG, Word)",
                "pdf", "png", "jpg", "jpeg", "doc", "docx"
        );
        chooser.setFileFilter(filter);

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedDocumentFile = chooser.getSelectedFile();
            txtDocumentPath.setText(selectedDocumentFile.getAbsolutePath());
        }
    }

    private void openAttachedDocument() {
        String docPathStr = txtDocumentPath.getText().trim();
        if (docPathStr.isEmpty() || "No Document".equalsIgnoreCase(docPathStr)) {
            JOptionPane.showMessageDialog(this,
                    "No medical document/image is attached to this record.",
                    "No Document", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File file = new File(docPathStr);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this,
                    "Document file not found at:\n" + file.getAbsolutePath(),
                    "File Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Unable to open document: " + ex.getMessage(),
                        "Open Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Document stored at:\n" + file.getAbsolutePath(),
                    "Document Location", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String storeDocumentFileIfNeeded(int studentId) {
        String currentPath = txtDocumentPath.getText().trim();
        if (currentPath.isEmpty() || "No Document".equalsIgnoreCase(currentPath)) {
            return null;
        }

        if (selectedDocumentFile != null && selectedDocumentFile.exists()) {
            try {
                Path uploadFolder = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadFolder);

                String safeFileName = "stu" + studentId + "_" + System.currentTimeMillis() + "_"
                        + selectedDocumentFile.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
                Path destination = uploadFolder.resolve(safeFileName);
                Files.copy(selectedDocumentFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                return destination.toString().replace('\\', '/');
            } catch (IOException ex) {
                return selectedDocumentFile.getAbsolutePath().replace('\\', '/');
            }
        }

        return currentPath.replace('\\', '/');
    }

    public void loadMedicalRecords() {
        List<Medical> list = medicalDAO.getAllMedicals();
        tableModel.setRowCount(0);
        for (Medical m : list) {
            String docImg = m.getMedicalImg();
            tableModel.addRow(new Object[]{
                    m.getMedicalId(),
                    m.getStudentId(),
                    m.getAttendanceId() != null ? m.getAttendanceId() : "",
                    m.getSubmissionDate() != null ? m.getSubmissionDate().toString() : "",
                    m.getStartDate() != null ? m.getStartDate().toString() : "",
                    m.getEndDate() != null ? m.getEndDate().toString() : "",
                    m.getReason() != null ? m.getReason() : "",
                    (docImg != null && !docImg.isEmpty()) ? docImg : "No Document",
                    m.getApprovalStatus(),
                    m.getStatus()
            });
        }
        lblRecordCount.setText(list.size() + " records");
    }

    private void populateFormFromSelectedRow() {
        int row = medicalTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        selectedDocumentFile = null;
        txtMedicalId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtStudentId.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        Object attObj = tableModel.getValueAt(row, 2);
        txtAttendanceId.setText(attObj != null ? attObj.toString() : "");
        txtSubmissionDate.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtStartDate.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txtEndDate.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        txtReason.setText(String.valueOf(tableModel.getValueAt(row, 6)));

        String docVal = String.valueOf(tableModel.getValueAt(row, 7));
        txtDocumentPath.setText("No Document".equalsIgnoreCase(docVal) ? "" : docVal);

        cmbApprovalStatus.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 8)));
    }

    private void saveMedical() {
        try {
            Medical medical = buildMedicalFromForm(false);
            if (medicalDAO.addMedical(medical)) {
                JOptionPane.showMessageDialog(this,
                        "Medical record saved successfully! (ID: " + medical.getMedicalId() + ")",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadMedicalRecords();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to save medical record. Verify Student ID (11-30) and Attendance ID.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateMedical() {
        String idStr = txtMedicalId.getText().trim();
        if (idStr.isEmpty() || "Auto".equalsIgnoreCase(idStr)) {
            JOptionPane.showMessageDialog(this, "Please select a medical record from the table to update.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Medical medical = buildMedicalFromForm(true);
            if (medicalDAO.updateMedical(medical)) {
                JOptionPane.showMessageDialog(this, "Medical record updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadMedicalRecords();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update medical record.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteMedical() {
        String idStr = txtMedicalId.getText().trim();
        if (idStr.isEmpty() || "Auto".equalsIgnoreCase(idStr)) {
            JOptionPane.showMessageDialog(this, "Please select a medical record from the table to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Medical ID " + idStr + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int medicalId = Integer.parseInt(idStr);
            if (medicalDAO.deleteMedical(medicalId)) {
                JOptionPane.showMessageDialog(this, "Medical record deleted successfully!",
                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadMedicalRecords();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete medical record.",
                        "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Medical ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Medical buildMedicalFromForm(boolean includeId) {
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

        Integer attendanceId = null;
        String attStr = txtAttendanceId.getText().trim();
        if (!attStr.isEmpty()) {
            try {
                attendanceId = Integer.parseInt(attStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Attendance ID must be a valid integer or left blank.");
            }
        }

        LocalDate submissionDate = parseDateField(txtSubmissionDate.getText().trim(), "Submission Date");
        LocalDate startDate = parseDateField(txtStartDate.getText().trim(), "Start Date");
        LocalDate endDate = parseDateField(txtEndDate.getText().trim(), "End Date");

        String reason = txtReason.getText().trim();
        if (reason.isEmpty()) {
            throw new IllegalArgumentException("Reason is required.");
        }

        String savedMedicalImg = storeDocumentFileIfNeeded(studentId);
        String approvalStatus = String.valueOf(cmbApprovalStatus.getSelectedItem());

        if (includeId) {
            int medicalId = Integer.parseInt(txtMedicalId.getText().trim());
            return new Medical(medicalId, studentId, attendanceId, submissionDate,
                    startDate, endDate, reason, approvalStatus, Medical.STATUS_RECORDED, savedMedicalImg);
        }
        return new Medical(studentId, attendanceId, submissionDate,
                startDate, endDate, reason, approvalStatus, Medical.STATUS_RECORDED, savedMedicalImg);
    }

    private LocalDate parseDateField(String text, String fieldName) {
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required (YYYY-MM-DD).");
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " must be in YYYY-MM-DD format.");
        }
    }

    private void clearForm() {
        String today = LocalDate.now().toString();
        txtMedicalId.setText("Auto");
        txtStudentId.setText("");
        txtAttendanceId.setText("");
        txtSubmissionDate.setText(today);
        txtStartDate.setText(today);
        txtEndDate.setText(today);
        txtReason.setText("");
        txtDocumentPath.setText("");
        selectedDocumentFile = null;
        cmbApprovalStatus.setSelectedIndex(0);
        medicalTable.clearSelection();
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

    private JButton createButton(String text, Color bg, int width) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 34));
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

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 6 && i != 7 && i != 8) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Medical Document column renderer
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                String text = value != null ? value.toString() : "No Document";
                if (!"No Document".equalsIgnoreCase(text)) {
                    lbl.setForeground(TEAL_ACCENT);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    lbl.setForeground(BTN_NEUTRAL);
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                }
                return lbl;
            }
        });

        // Approval Status Renderer
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String status = value != null ? value.toString() : "";
                if (Medical.APPROVAL_APPROVED.equalsIgnoreCase(status)) {
                    lbl.setForeground(GREEN_OK);
                } else if (Medical.APPROVAL_REJECTED.equalsIgnoreCase(status)) {
                    lbl.setForeground(BTN_DANGER);
                } else {
                    lbl.setForeground(AMBER_PENDING);
                }
                return lbl;
            }
        });
    }
}
