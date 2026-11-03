package com.ftms.view.student;

import com.ftms.dao.MedicalDAO;
import com.ftms.model.Medical;
import com.ftms.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/** Read-only medical history for the currently logged-in student. */
public class MedicalSubmissionPanel extends JPanel {
    private static final Color NAVY = new Color(9, 43, 72);
    private static final Color TEAL = new Color(0, 136, 158);
    private final MedicalDAO dao = new MedicalDAO();
    private final JLabel countLabel = new JLabel("0 records");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Medical ID", "Student ID", "Attendance ID", "Submitted", "Start Date", "End Date", "Reason", "Document", "Approval", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private List<Medical> records = Collections.emptyList();

    public MedicalSubmissionPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(new Color(244, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        JLabel title = new JLabel("My Medical Records");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(NAVY);
        add(title, BorderLayout.NORTH);

        JPanel history = new JPanel(new BorderLayout(0, 12));
        history.setBackground(Color.WHITE);
        history.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 226, 237)),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)));

        JPanel header = new JPanel(new BorderLayout(12, 8));
        header.setOpaque(false);
        JLabel description = new JLabel("View your medical records and approval status (read only)");
        description.setFont(new Font("SansSerif", Font.BOLD, 14));
        description.setForeground(NAVY);
        header.add(description, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton open = button("Open Document", TEAL);
        open.addActionListener(e -> openSelectedDocument());
        JButton refresh = button("Refresh", new Color(38, 110, 154));
        refresh.addActionListener(e -> refreshRecords());
        actions.add(countLabel);
        actions.add(open);
        actions.add(refresh);
        header.add(actions, BorderLayout.EAST);
        history.add(header, BorderLayout.NORTH);

        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setGridColor(new Color(232, 237, 243));
        table.setSelectionBackground(new Color(219, 235, 248));
        table.setSelectionForeground(NAVY);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        history.add(new JScrollPane(table), BorderLayout.CENTER);
        add(history, BorderLayout.CENTER);
        refreshRecords();
    }

    private JButton button(String label, Color background) {
        JButton button = new JButton(label);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return button;
    }

    private int studentId() {
        return SessionManager.getInstance().getLoggedInUserId();
    }

    public void refreshRecords() {
        int id = studentId();
        model.setRowCount(0);
        records = id < 0 ? Collections.emptyList() : dao.getMedicalsByStudentId(id);
        for (Medical m : records) {
            model.addRow(new Object[]{
                    m.getMedicalId(), m.getStudentId(), m.getAttendanceId(),
                    m.getSubmissionDate(), m.getStartDate(), m.getEndDate(),
                    m.getReason(), m.getMedicalImg(), m.getApprovalStatus(), m.getStatus()
            });
        }
        countLabel.setText(records.size() + " records");
    }

    private void openSelectedDocument() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select a medical record first.");
            return;
        }
        int index = table.convertRowIndexToModel(selected);
        if (index >= records.size()) return;
        Medical medical = records.get(index);
        // Re-check ownership even though records were loaded for this student.
        if (studentId() < 0 || medical.getStudentId() != studentId()) {
            JOptionPane.showMessageDialog(this, "You can only view your own medical records.");
            return;
        }
        String path = medical.getMedicalImg();
        if (path == null || path.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No document is attached to this record.");
            return;
        }
        try {
            File file = new File(path);
            if (!file.isFile()) throw new IOException("Document not found: " + path);
            if (!Desktop.isDesktopSupported()) throw new IOException("Opening documents is not supported on this computer.");
            Desktop.getDesktop().open(file);
        } catch (IOException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Unable to open document: " + e.getMessage());
        }
    }
}
