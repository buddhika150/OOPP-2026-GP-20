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
import java.sql.SQLException;
import java.util.List;

/**
 * MedicalApprovalPanel - UI screen for reviewing and processing student medical records.
 * Provides status filtering (ALL, PENDING, APPROVED, REJECTED), inspection of medical details,
 * and authorized status updates via MedicalDAO.
 */
public class MedicalApprovalPanel extends JPanel {

    // Theme Palette matching reference screenshot
    private static final Color BG_PAGE = new Color(0xF4, 0xF7, 0xFB);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xE2, 0xE8, 0xF0);
    private static final Color TEXT_PRIMARY = new Color(0x1E, 0x29, 0x3B);
    private static final Color TEXT_MUTED = new Color(0x64, 0x74, 0x8B);
    private static final Color TEAL_PRIMARY = new Color(0x00, 0xA8, 0xA8);
    private static final Color SUCCESS_GREEN = new Color(0x16, 0xA3, 0x4A);
    private static final Color DANGER_RED = new Color(0xDC, 0x26, 0x26);

    // Pill Badge Colors
    private static final Color PILL_YELLOW_BG = new Color(0xFE, 0xF3, 0xC7);
    private static final Color PILL_YELLOW_TEXT = new Color(0xB4, 0x53, 0x09);
    private static final Color PILL_GREEN_BG = new Color(0xDC, 0xFC, 0xE7);
    private static final Color PILL_GREEN_TEXT = new Color(0x15, 0x80, 0x3D);
    private static final Color PILL_RED_BG = new Color(0xFE, 0xE2, 0xE2);
    private static final Color PILL_RED_TEXT = new Color(0xB9, 0x1C, 0x1C);

    private final MedicalDAO medicalDAO;

    private DefaultTableModel tableModel;
    private JTable medicalTable;
    private String currentFilter = "ALL";
    private JLabel selectedRecordLabel;
    private JTextArea selectedReasonArea;
    private JButton approveBtn;
    private JButton rejectBtn;
    private Integer selectedMedicalId = null;

    private JButton filterAllBtn;
    private JButton filterPendingBtn;
    private JButton filterApprovedBtn;
    private JButton filterRejectedBtn;

    public MedicalApprovalPanel() {
        this(new MedicalDAO());
    }

    public MedicalApprovalPanel(MedicalDAO medicalDAO) {
        this.medicalDAO = medicalDAO != null ? medicalDAO : new MedicalDAO();
        initializeUI();
        loadMedicalRecords();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(BG_PAGE);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Medical Approvals");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Review, verify medical reasons, and update approval statuses for submitted certificates");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_MUTED);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center: Filter Bar + Records Table Card
        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setOpaque(false);

        centerPanel.add(createFilterToolbar(), BorderLayout.NORTH);
        centerPanel.add(createTableCard(), BorderLayout.CENTER);
        centerPanel.add(createActionBottomCard(), BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createFilterToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel filterBtnsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBtnsPanel.setOpaque(false);

        filterAllBtn = createFilterButton("All Records", "ALL", true);
        filterPendingBtn = createFilterButton("Pending", Medical.STATUS_PENDING, false);
        filterApprovedBtn = createFilterButton("Approved", Medical.STATUS_APPROVED, false);
        filterRejectedBtn = createFilterButton("Rejected", Medical.STATUS_REJECTED, false);

        filterBtnsPanel.add(filterAllBtn);
        filterBtnsPanel.add(filterPendingBtn);
        filterBtnsPanel.add(filterApprovedBtn);
        filterBtnsPanel.add(filterRejectedBtn);

        toolbar.add(filterBtnsPanel, BorderLayout.WEST);

        // Refresh Button
        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setBackground(Color.WHITE);
        refreshBtn.setForeground(TEXT_PRIMARY);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadMedicalRecords());
        toolbar.add(refreshBtn, BorderLayout.EAST);

        return toolbar;
    }

    private JButton createFilterButton(String text, String filterKey, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        applyFilterButtonStyle(btn, isActive);

        btn.addActionListener(e -> {
            currentFilter = filterKey;
            updateFilterButtonStates();
            loadMedicalRecords();
        });

        return btn;
    }

    private void applyFilterButtonStyle(JButton btn, boolean isActive) {
        if (isActive) {
            btn.setBackground(TEAL_PRIMARY);
            btn.setForeground(Color.WHITE);
            btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(TEXT_PRIMARY);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(8, 16, 8, 16)
            ));
        }
    }

    private void updateFilterButtonStates() {
        applyFilterButtonStyle(filterAllBtn, "ALL".equals(currentFilter));
        applyFilterButtonStyle(filterPendingBtn, Medical.STATUS_PENDING.equals(currentFilter));
        applyFilterButtonStyle(filterApprovedBtn, Medical.STATUS_APPROVED.equals(currentFilter));
        applyFilterButtonStyle(filterRejectedBtn, Medical.STATUS_REJECTED.equals(currentFilter));
    }

    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        String[] cols = {"Medical ID", "Student ID", "Att. ID", "Submitted Date",
                "Leave Start", "Leave End", "Reason", "Approval Status"};

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        medicalTable = new JTable(tableModel);
        medicalTable.setRowHeight(36);
        medicalTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        medicalTable.setGridColor(new Color(0xF1, 0xF5, 0xF9));
        medicalTable.setSelectionBackground(new Color(0xE0, 0xF2, 0xF1));
        medicalTable.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = medicalTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(0xF8, 0xFA, 0xFC));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < cols.length - 2; i++) {
            medicalTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Status Pill Renderer for column 7
        medicalTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, val, sel, foc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setOpaque(true);

                String status = val != null ? val.toString() : "";
                if (Medical.STATUS_APPROVED.equalsIgnoreCase(status)) {
                    lbl.setBackground(PILL_GREEN_BG);
                    lbl.setForeground(PILL_GREEN_TEXT);
                } else if (Medical.STATUS_REJECTED.equalsIgnoreCase(status)) {
                    lbl.setBackground(PILL_RED_BG);
                    lbl.setForeground(PILL_RED_TEXT);
                } else {
                    lbl.setBackground(PILL_YELLOW_BG);
                    lbl.setForeground(PILL_YELLOW_TEXT);
                }
                return lbl;
            }
        });

        // Row selection listener to update action panel
        medicalTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = medicalTable.getSelectedRow();
                if (row >= 0) {
                    selectedMedicalId = (Integer) medicalTable.getValueAt(row, 0);
                    int studentId = (Integer) medicalTable.getValueAt(row, 1);
                    String reason = (String) medicalTable.getValueAt(row, 6);
                    String status = (String) medicalTable.getValueAt(row, 7);

                    selectedRecordLabel.setText("Selected Medical #" + selectedMedicalId +
                            " (Student #" + studentId + " | Current Status: " + status + ")");
                    selectedReasonArea.setText(reason);

                    boolean isPending = Medical.STATUS_PENDING.equalsIgnoreCase(status);
                    approveBtn.setEnabled(true);
                    rejectBtn.setEnabled(true);
                } else {
                    resetSelection();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(medicalTable);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createActionBottomCard() {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));

        // Left info
        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setOpaque(false);

        selectedRecordLabel = new JLabel("Select a record from the table above to approve or reject");
        selectedRecordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectedRecordLabel.setForeground(TEXT_PRIMARY);

        selectedReasonArea = new JTextArea(2, 40);
        selectedReasonArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        selectedReasonArea.setForeground(TEXT_MUTED);
        selectedReasonArea.setEditable(false);
        selectedReasonArea.setOpaque(false);
        selectedReasonArea.setLineWrap(true);
        selectedReasonArea.setWrapStyleWord(true);

        infoPanel.add(selectedRecordLabel, BorderLayout.NORTH);
        infoPanel.add(selectedReasonArea, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.CENTER);

        // Right Action Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        approveBtn = new JButton("Approve Medical");
        approveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        approveBtn.setBackground(SUCCESS_GREEN);
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setFocusPainted(false);
        approveBtn.setBorder(new EmptyBorder(9, 16, 9, 16));
        approveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        approveBtn.setEnabled(false);
        approveBtn.addActionListener(e -> handleUpdateStatus(Medical.STATUS_APPROVED));

        rejectBtn = new JButton("Reject Medical");
        rejectBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rejectBtn.setBackground(DANGER_RED);
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setFocusPainted(false);
        rejectBtn.setBorder(new EmptyBorder(9, 16, 9, 16));
        rejectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rejectBtn.setEnabled(false);
        rejectBtn.addActionListener(e -> handleUpdateStatus(Medical.STATUS_REJECTED));

        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);

        card.add(btnPanel, BorderLayout.EAST);

        return card;
    }

    private void handleUpdateStatus(String newStatus) {
        if (selectedMedicalId == null) {
            JOptionPane.showMessageDialog(this, "Please select a medical record from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to change Medical #" + selectedMedicalId + " status to " + newStatus + "?",
                "Confirm Status Update", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean updated;
            if (Medical.STATUS_APPROVED.equalsIgnoreCase(newStatus)) {
                updated = medicalDAO.approveMedical(selectedMedicalId);
            } else {
                updated = medicalDAO.rejectMedical(selectedMedicalId);
            }

            if (updated) {
                JOptionPane.showMessageDialog(this,
                        "Medical #" + selectedMedicalId + " successfully updated to " + newStatus + "!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMedicalRecords();
            } else {
                JOptionPane.showMessageDialog(this, "Could not update medical record status.",
                        "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadMedicalRecords() {
        resetSelection();
        tableModel.setRowCount(0);

        try {
            List<Medical> records;
            if ("ALL".equalsIgnoreCase(currentFilter)) {
                records = medicalDAO.getAllMedicals();
            } else {
                records = medicalDAO.getMedicalsByApprovalStatus(currentFilter);
            }

            if (records != null) {
                for (Medical m : records) {
                    tableModel.addRow(new Object[]{
                            m.getMedicalId(),
                            m.getStudentId(),
                            m.getAttendanceId() != null ? m.getAttendanceId() : "-",
                            m.getSubmissionDate() != null ? m.getSubmissionDate().toString() : "N/A",
                            m.getStartDate() != null ? m.getStartDate().toString() : "N/A",
                            m.getEndDate() != null ? m.getEndDate().toString() : "N/A",
                            m.getReason() != null ? m.getReason() : "",
                            m.getApprovalStatus()
                    });
                }
            }
        } catch (SQLException ex) {
            // Friendly error message if database is not reachable
            System.err.println("Could not load medical records: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Error loading medical records: " + ex.getMessage());
        }
    }

    private void resetSelection() {
        selectedMedicalId = null;
        selectedRecordLabel.setText("Select a record from the table above to approve or reject");
        selectedReasonArea.setText("");
        approveBtn.setEnabled(false);
        rejectBtn.setEnabled(false);
    }
}
