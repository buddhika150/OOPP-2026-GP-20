package com.ftms.view.student;

import com.ftms.dao.StudentInformationDAO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentNoticesPanel extends JPanel {
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Published Date", "Title"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextArea content = new JTextArea();
    private final JLabel status = new JLabel(" ");
    private final List<String[]> notices = new ArrayList<>();

    public StudentNoticesPanel() {
        setLayout(new BorderLayout(12, 14));
        setBackground(new Color(244, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Notices");
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
        table.setRowHeight(34);
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
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int selected = table.getSelectedRow();
                int index = selected < 0 ? -1 : table.convertRowIndexToModel(selected);
                content.setText(index >= 0 && index < notices.size() ? notices.get(index)[1] : "");
                content.setCaretPosition(0);
            }
        });
        content.setEditable(false);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setFont(new Font("SansSerif", Font.PLAIN, 14));
        content.setBackground(Color.WHITE);
        content.setForeground(new Color(42, 56, 72));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table), new JScrollPane(content));
        split.setResizeWeight(0.55);
        split.setBorder(BorderFactory.createLineBorder(new Color(215, 226, 237)));
        split.setDividerSize(7);
        add(split, BorderLayout.CENTER);
        status.setFont(new Font("SansSerif", Font.PLAIN, 12));
        status.setForeground(new Color(90, 108, 126));
        add(status, BorderLayout.SOUTH);
        refreshRecords();
    }

    public void refreshRecords() {
        model.setRowCount(0);
        notices.clear();
        content.setText("");
        try {
            notices.addAll(new StudentInformationDAO().getNotices());
            for (String[] notice : notices) model.addRow(new Object[]{notice[2], notice[0]});
            if (!notices.isEmpty()) table.setRowSelectionInterval(0, 0);
            status.setText(notices.isEmpty() ? "No notices available." : notices.size() + " notices available.");
        } catch (SQLException ex) {
            status.setText("Unable to load notices.");
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Notice Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
