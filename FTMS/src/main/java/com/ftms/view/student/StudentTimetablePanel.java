package com.ftms.view.student;

import com.ftms.dao.StudentInformationDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class StudentTimetablePanel extends JPanel {
    private final String department;
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Course Code", "Day", "Time Slot"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JLabel status = new JLabel(" ");

    public StudentTimetablePanel(String department) {
        this.department = department;
        setLayout(new BorderLayout(12, 14));
        setBackground(new Color(244, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("My Timetable - " + (department == null ? "Unknown Department" : department));
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(9, 43, 72));
        JButton refresh = new JButton("Refresh");
        refresh.setBackground(new Color(38, 110, 154));
        refresh.setForeground(Color.WHITE);
        refresh.setFocusPainted(false);
        refresh.setFont(new Font("SansSerif", Font.BOLD, 12));
        refresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refresh.setBorder(BorderFactory.createEmptyBorder(9, 17, 9, 17));
        refresh.addActionListener(e -> refreshRecords());
        header.add(title, BorderLayout.WEST);
        header.add(refresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        JTable table = new JTable(model);
        table.setRowHeight(34);
        styleTable(table);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(215, 226, 237)));
        add(scroll, BorderLayout.CENTER);
        status.setFont(new Font("SansSerif", Font.PLAIN, 12));
        status.setForeground(new Color(90, 108, 126));
        add(status, BorderLayout.SOUTH);
        refreshRecords();
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setGridColor(new Color(232, 237, 243));
        table.setSelectionBackground(new Color(219, 235, 248));
        table.setSelectionForeground(new Color(9, 43, 72));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setBackground(new Color(9, 43, 72));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
    }

    public void refreshRecords() {
        model.setRowCount(0);
        try {
            for (String[] row : new StudentInformationDAO().getTimetableByDepartment(department)) model.addRow(row);
            status.setText(model.getRowCount() == 0 ? "No timetable records for your department." :
                    model.getRowCount() + " timetable entries found.");
        } catch (SQLException ex) {
            status.setText("Unable to load timetable.");
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Timetable Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
