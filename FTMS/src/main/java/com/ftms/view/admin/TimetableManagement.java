
package com.ftms.view.admin;

import com.ftms.dao.TimetableDAO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class TimetableManagement extends JFrame {

    private final Color NAVY = new Color(18, 35, 58);
    private final Color TEAL = new Color(0, 160, 150);
    private final TimetableDAO dao = new TimetableDAO();

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public TimetableManagement() {
        setTitle("FTMS - Timetable Management");
        setSize(1050, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(new Color(245, 248, 251));
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel heading = new JLabel("Timetable Management");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heading.setForeground(NAVY);

        JButton back = button("Back", NAVY);
        back.addActionListener(e -> {
            new AdminDashboard();
            dispose();
        });

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(heading, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);

        searchField = new JTextField();
        searchField.setBorder(
                BorderFactory.createTitledBorder("Search Timetables")
        );

        JButton add = button("+ Add Timetable", TEAL);
        add.addActionListener(e -> showForm(false));

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);
        top.add(searchField, BorderLayout.CENTER);
        top.add(add, BorderLayout.EAST);

        model = new DefaultTableModel(
                new String[]{"ID", "Course", "Department", "Day", "Time Slot"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

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

        JButton refresh = button("Refresh", NAVY);
        JButton edit = button("Edit", TEAL);
        JButton delete = button("Delete", new Color(190, 60, 65));

        refresh.addActionListener(e -> load());
        edit.addActionListener(e -> showForm(true));
        delete.addActionListener(e -> deleteSelected());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(refresh);
        actions.add(edit);
        actions.add(delete);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.add(top, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        main.add(header, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);
        add(main);

        load();
        setVisible(true);
    }

    private JButton button(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        return b;
    }

    private void load() {
        model.setRowCount(0);
        try {
            for (Object[] row : dao.getAll()) {
                model.addRow(row);
            }
        } catch (SQLException e) {
            error(e);
        }
    }

    private void filter() {
        String text = searchField.getText().trim();
        sorter.setRowFilter(text.isEmpty() ? null :
                RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
    }

    private int selectedRow() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select a timetable first.");
            return -1;
        }

        return table.convertRowIndexToModel(row);
    }

    private void showForm(boolean editing) {
        int row = -1;
        int id = -1;

        if (editing) {
            row = selectedRow();
            if (row == -1) return;
            id = (int) model.getValueAt(row, 0);
        }

        JComboBox<String> course = new JComboBox<>();
        JTextField department = new JTextField("ICT");
        JComboBox<String> day = new JComboBox<>(
                new String[]{"Monday", "Tuesday", "Wednesday",
                        "Thursday", "Friday", "Saturday", "Sunday"}
        );
        JTextField time = new JTextField("08:00-10:00");

        try {
            for (String c : dao.getCourses()) {
                course.addItem(c);
            }
        } catch (SQLException e) {
            error(e);
            return;
        }

        if (editing) {
            course.setSelectedItem(model.getValueAt(row, 1));
            department.setText(String.valueOf(model.getValueAt(row, 2)));
            day.setSelectedItem(model.getValueAt(row, 3));
            time.setText(String.valueOf(model.getValueAt(row, 4)));
        }

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 12));
        form.add(new JLabel("Course"));
        form.add(course);
        form.add(new JLabel("Department"));
        form.add(department);
        form.add(new JLabel("Day"));
        form.add(day);
        form.add(new JLabel("Time Slot"));
        form.add(time);

        int result = JOptionPane.showConfirmDialog(
                this, form,
                editing ? "Edit Timetable" : "Add Timetable",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        String selectedCourse = (String) course.getSelectedItem();
        String dept = department.getText().trim();
        String selectedDay = (String) day.getSelectedItem();
        String slot = time.getText().trim();

        if (selectedCourse == null || dept.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Course and Department are required.");
            return;
        }

        if (!slot.matches(
                "([01]\\d|2[0-3]):[0-5]\\d-([01]\\d|2[0-3]):[0-5]\\d"
        )) {
            JOptionPane.showMessageDialog(
                    this, "Use time format: 08:00-10:00");
            return;
        }

        String[] times = slot.split("-");
        if (times[0].compareTo(times[1]) >= 0) {
            JOptionPane.showMessageDialog(
                    this, "End time must be after start time.");
            return;
        }

        try {
            boolean success = editing
                    ? dao.update(id, selectedCourse, dept, selectedDay, slot)
                    : dao.add(selectedCourse, dept, selectedDay, slot);

            if (success) {
                JOptionPane.showMessageDialog(
                        this, editing ? "Timetable updated!" : "Timetable added!");
                load();
            }
        } catch (SQLException e) {
            error(e);
        }
    }

    private void deleteSelected() {
        int row = selectedRow();
        if (row == -1) return;

        int id = (int) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this, "Delete selected timetable?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (dao.delete(id)) {
                JOptionPane.showMessageDialog(this, "Timetable deleted!");
                load();
            }
        } catch (SQLException e) {
            error(e);
        }
    }

    private void error(Exception e) {
        JOptionPane.showMessageDialog(
                this, e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TimetableManagement::new);
    }
}
