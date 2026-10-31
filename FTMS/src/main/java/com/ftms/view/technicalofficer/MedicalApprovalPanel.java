package com.ftms.view.technicalofficer;

import com.ftms.dao.MedicalDAO;
import com.ftms.model.Medical;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Panel for filtering, reviewing, approving, and rejecting student medical records,
 * including opening the attached medicalimg document/image.
 */
public class MedicalApprovalPanel extends JPanel {

    private static final Color BG_MAIN = new Color(245, 248, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(224, 232, 240);
    private static final Color NAVY_DARK = new Color(11, 46, 74);
    private static final Color TEAL_ACCENT = new Color(0, 131, 155);
    private static final Color TEXT_MUTED = new Color(110, 125, 140);
    private static final Color GREEN_OK = new Color(22, 135, 75);
    private static final Color RED_REJECT = new Color(205, 58, 58);
    private static final Color AMBER_PENDING = new Color(195, 125, 15);

    private final MedicalDAO medicalDAO;

    private JComboBox<String> cmbStatusFilter;
    private JTextField txtStudentIdFilter;

    private JLabel lblPendingCount;
    private JLabel lblApprovedCount;
    private JLabel lblRejectedCount;
    private JLabel lblSelectedRecordInfo;

    private JTable approvalTable;
    private DefaultTableModel tableModel;

    public MedicalApprovalPanel() {
        this.medicalDAO = new MedicalDAO();
        initComponents();
        loadRecords();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(4, 0, 0, 0));

        // Top section: Filter + Summary KPI Cards
        JPanel topContainer = new JPanel(new BorderLayout(0, 14));
        topContainer.setOpaque(false);

        // 1. Filter Bar Card
        JPanel filterCard = new JPanel(new BorderLayout(16, 0));
        filterCard.setBackground(CARD_BG);
        filterCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(16, 22, 16, 22)
        ));

        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftControls.setOpaque(false);

        JLabel lblFilterStatus = new JLabel("Approval Status:");
        lblFilterStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFilterStatus.setForeground(NAVY_DARK);

        cmbStatusFilter = new JComboBox<>(new String[]{
                "ALL",
                Medical.APPROVAL_PENDING,
                Medical.APPROVAL_APPROVED,
                Medical.APPROVAL_REJECTED
        });
        cmbStatusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbStatusFilter.setBackground(Color.WHITE);
        cmbStatusFilter.setPreferredSize(new Dimension(150, 34));
        cmbStatusFilter.addActionListener(e -> loadRecords());

        JLabel lblStudent = new JLabel("Student ID:");
        lblStudent.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStudent.setForeground(NAVY_DARK);

        txtStudentIdFilter = new JTextField();
        txtStudentIdFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtStudentIdFilter.setPreferredSize(new Dimension(110, 34));
        txtStudentIdFilter.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(206, 216, 226), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        JButton btnFilter = createButton("Filter", TEAL_ACCENT, 95);
        JButton btnReset = createButton("Reset", new Color(108, 122, 137), 95);

        btnFilter.addActionListener(e -> loadRecords());
        btnReset.addActionListener(e -> {
            cmbStatusFilter.setSelectedIndex(0);
            txtStudentIdFilter.setText("");
            loadRecords();
        });

        leftControls.add(lblFilterStatus);
        leftControls.add(cmbStatusFilter);
        leftControls.add(Box.createHorizontalStrut(8));
        leftControls.add(lblStudent);
        leftControls.add(txtStudentIdFilter);
        leftControls.add(btnFilter);
        leftControls.add(btnReset);

        filterCard.add(leftControls, BorderLayout.WEST);
        topContainer.add(filterCard, BorderLayout.NORTH);

        // 2. KPI Summary Cards
        JPanel kpiRow = new JPanel(new GridLayout(1, 3, 14, 0));
        kpiRow.setOpaque(false);

        lblPendingCount = new JLabel("0");
        lblApprovedCount = new JLabel("0");
        lblRejectedCount = new JLabel("0");

        kpiRow.add(createStatusCard("Pending Review", lblPendingCount, AMBER_PENDING));
        kpiRow.add(createStatusCard("Approved Medicals", lblApprovedCount, GREEN_OK));
        kpiRow.add(createStatusCard("Rejected Medicals", lblRejectedCount, RED_REJECT));

        topContainer.add(kpiRow, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // 3. Table + Action Bar Card
        JPanel tableCard = new JPanel(new BorderLayout(0, 14));
        tableCard.setBackground(CARD_BG);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(16, 22, 18, 22)
        ));

        JLabel lblTableTitle = new JLabel("Select a Medical Record to View Document, Approve, or Reject");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTableTitle.setForeground(NAVY_DARK);
        tableCard.add(lblTableTitle, BorderLayout.NORTH);

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
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        approvalTable = new JTable(tableModel);
        styleTable(approvalTable);

        approvalTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedInfo();
            }
        });

        JScrollPane scrollPane = new JScrollPane(approvalTable);
        scrollPane.setBorder(new LineBorder(CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel actionBar = new JPanel(new BorderLayout(12, 0));
        actionBar.setOpaque(false);

        lblSelectedRecordInfo = new JLabel("No medical record selected");
        lblSelectedRecordInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSelectedRecordInfo.setForeground(TEXT_MUTED);
        actionBar.add(lblSelectedRecordInfo, BorderLayout.WEST);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton btnViewDoc = createButton("View Document", TEAL_ACCENT, 135);
        JButton btnApprove = createButton("Approve Selected", GREEN_OK, 150);
        JButton btnReject = createButton("Reject Selected", RED_REJECT, 145);
        JButton btnPending = createButton("Mark Pending", AMBER_PENDING, 130);
        JButton btnRefresh = createButton("Refresh", NAVY_DARK, 105);

        btnViewDoc.addActionListener(e -> viewSelectedDocument());
        btnApprove.addActionListener(e -> changeSelectedStatus(Medical.APPROVAL_APPROVED));
        btnReject.addActionListener(e -> changeSelectedStatus(Medical.APPROVAL_REJECTED));
        btnPending.addActionListener(e -> changeSelectedStatus(Medical.APPROVAL_PENDING));
        btnRefresh.addActionListener(e -> loadRecords());

        actionButtons.add(btnViewDoc);
        actionButtons.add(btnApprove);
        actionButtons.add(btnReject);
        actionButtons.add(btnPending);
        actionButtons.add(btnRefresh);

        actionBar.add(actionButtons, BorderLayout.EAST);
        tableCard.add(actionBar, BorderLayout.SOUTH);

        add(tableCard, BorderLayout.CENTER);
    }

    private JPanel createStatusCard(String title, JLabel countLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(12, 18, 12, 18)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(TEXT_MUTED);

        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        countLabel.setForeground(accentColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);
        return card;
    }

    public void loadRecords() {
        List<Medical> allMedicals = medicalDAO.getAllMedicals();

        int pending = 0;
        int approved = 0;
        int rejected = 0;

        for (Medical m : allMedicals) {
            if (Medical.APPROVAL_APPROVED.equalsIgnoreCase(m.getApprovalStatus())) {
                approved++;
            } else if (Medical.APPROVAL_REJECTED.equalsIgnoreCase(m.getApprovalStatus())) {
                rejected++;
            } else {
                pending++;
            }
        }

        lblPendingCount.setText(String.valueOf(pending));
        lblApprovedCount.setText(String.valueOf(approved));
        lblRejectedCount.setText(String.valueOf(rejected));

        String selectedStatus = String.valueOf(cmbStatusFilter.getSelectedItem());
        String studentInput = txtStudentIdFilter.getText().trim();
        Integer studentFilter = null;

        if (!studentInput.isEmpty()) {
            try {
                studentFilter = Integer.parseInt(studentInput);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Student ID filter must be a valid integer.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        List<Medical> filteredList;
        if (!"ALL".equalsIgnoreCase(selectedStatus)) {
            filteredList = medicalDAO.getMedicalsByApprovalStatus(selectedStatus);
        } else {
            filteredList = allMedicals;
        }

        tableModel.setRowCount(0);
        for (Medical m : filteredList) {
            if (studentFilter != null && m.getStudentId() != studentFilter) {
                continue;
            }
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

        lblSelectedRecordInfo.setText("Showing " + tableModel.getRowCount() + " record(s)");
    }

    private void updateSelectedInfo() {
        int row = approvalTable.getSelectedRow();
        if (row < 0) {
            lblSelectedRecordInfo.setText("No medical record selected");
            return;
        }
        Object medId = tableModel.getValueAt(row, 0);
        Object stuId = tableModel.getValueAt(row, 1);
        Object status = tableModel.getValueAt(row, 8);
        lblSelectedRecordInfo.setText("Selected: Medical ID #" + medId + " | Student ID: " + stuId + " | Status: " + status);
    }

    private void viewSelectedDocument() {
        int row = approvalTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a medical record from the table first.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String docPath = String.valueOf(tableModel.getValueAt(row, 7)).trim();
        if (docPath.isEmpty() || "No Document".equalsIgnoreCase(docPath)) {
            JOptionPane.showMessageDialog(this, "No medical document/image is attached to this record.",
                    "No Document", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File file = new File(docPath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Document file not found at:\n" + file.getAbsolutePath(),
                    "File Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Unable to open document: " + ex.getMessage(),
                        "Open Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeSelectedStatus(String newStatus) {
        int row = approvalTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a medical record from the table first.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int medicalId = Integer.parseInt(String.valueOf(tableModel.getValueAt(row, 0)));
        boolean updated;

        if (Medical.APPROVAL_APPROVED.equalsIgnoreCase(newStatus)) {
            updated = medicalDAO.approveMedical(medicalId);
        } else if (Medical.APPROVAL_REJECTED.equalsIgnoreCase(newStatus)) {
            updated = medicalDAO.rejectMedical(medicalId);
        } else {
            updated = medicalDAO.updateApprovalStatus(medicalId, newStatus);
        }

        if (updated) {
            JOptionPane.showMessageDialog(this,
                    "Medical ID #" + medicalId + " status updated to " + newStatus + ".",
                    "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            loadRecords();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update medical approval status.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
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
                    lbl.setForeground(RED_REJECT);
                } else {
                    lbl.setForeground(AMBER_PENDING);
                }
                return lbl;
            }
        });
    }
}
