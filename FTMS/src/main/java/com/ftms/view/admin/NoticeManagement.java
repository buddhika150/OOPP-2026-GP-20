
package com.ftms.view.admin;

import com.ftms.dao.NoticeDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class NoticeManagement extends JFrame {

    private final Color NAVY = new Color(18, 35, 58);
    private final Color TEAL = new Color(0, 160, 150);

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private NoticeDAO dao = new NoticeDAO();

    public NoticeManagement() {

        setTitle("FTMS - Notice Management");
        setSize(1050, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(new Color(245, 248, 251));
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Notice Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(NAVY);

        JButton back = createButton("Back", NAVY);
        back.addActionListener(e -> {
            new AdminDashboard();
            dispose();
        });

        header.add(title, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createTitledBorder("Search Notices"));

        JButton add = createButton("+ Add Notice", TEAL);
        add.addActionListener(e -> showNoticeForm(false));

        top.add(searchField, BorderLayout.CENTER);
        top.add(add, BorderLayout.EAST);

        model = new DefaultTableModel(
                new String[]{"ID", "Title", "Content", "Published Date"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton edit = createButton("Edit Notice", TEAL);
        JButton delete = createButton("Delete Notice", new Color(190, 60, 65));
        JButton refresh = createButton("Refresh", NAVY);

        edit.addActionListener(e -> showNoticeForm(true));
        delete.addActionListener(e -> deleteSelected());
        refresh.addActionListener(e -> loadNotices());

        actions.add(refresh);
        actions.add(edit);
        actions.add(delete);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.add(top, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        main.add(header, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);

        add(main);

        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        filter();
                    }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        filter();
                    }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        filter();
                    }
                }
        );

        loadNotices();
        setVisible(true);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadNotices() {
        model.setRowCount(0);

        try (ResultSet rs = dao.getNotices()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("Notice_ID"),
                        rs.getString("Title"),
                        rs.getString("Content"),
                        rs.getDate("Published_date")
                });
            }
            filter();
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void filter() {
        String text = searchField.getText().trim();

        javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                new javax.swing.table.TableRowSorter<>(model);

        table.setRowSorter(sorter);

        if (!text.isEmpty()) {
            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + java.util.regex.Pattern.quote(text)
                    )
            );
        }
    }

    private void showNoticeForm(boolean editing) {

        int id = -1;
        String oldTitle = "";
        String oldContent = "";

        if (editing) {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a notice first.");
                return;
            }

            int modelRow = table.convertRowIndexToModel(row);

            id = (int) model.getValueAt(modelRow, 0);
            oldTitle = String.valueOf(model.getValueAt(modelRow, 1));
            oldContent = String.valueOf(model.getValueAt(modelRow, 2));
        }

        JTextField titleField = new JTextField(oldTitle, 25);
        JTextArea contentArea = new JTextArea(oldContent, 6, 25);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new BorderLayout(8, 8));

        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titlePanel.add(new JLabel("Notice Title"), BorderLayout.NORTH);
        titlePanel.add(titleField, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(new JLabel("Notice Content"), BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);

        form.add(titlePanel, BorderLayout.NORTH);
        form.add(contentPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                this, form,
                editing ? "Edit Notice" : "Add Notice",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Title and Content are required.");
            return;
        }

        if (title.length() > 150) {
            JOptionPane.showMessageDialog(this,
                    "Title must be 150 characters or less.");
            return;
        }

        try {
            boolean success;

            if (editing) {
                success = dao.updateNotice(id, title, content);
            } else {
                success = dao.addNotice(title, content);
            }

            if (success) {
                JOptionPane.showMessageDialog(this,
                        editing ? "Notice updated!" : "Notice added!");
                loadNotices();
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void deleteSelected() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a notice first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) model.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this notice?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (dao.deleteNotice(id)) {
                JOptionPane.showMessageDialog(this, "Notice deleted!");
                loadNotices();
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(
                this, e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NoticeManagement::new);
    }
}
