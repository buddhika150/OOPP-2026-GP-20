
package com.ftms.view.admin;

import com.ftms.dao.CourseDAO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class CourseManagement extends JFrame {

    Color navy = new Color(18, 44, 78);
    Color teal = new Color(21, 130, 153);
    Color bg = new Color(237, 243, 248);

    CourseDAO dao = new CourseDAO();
    JTable table;
    DefaultTableModel model;
    JLabel total;

    String[] columns = {
            "Code", "Course Name", "Year", "Semester",
            "Theory Credits", "Practical Credits",
            "Theory Hours", "Practical Hours"
    };

    public CourseManagement() {
        setTitle("FTMS - Course Management");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(bg);
        main.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        add(main);

        // HEADER
        JLabel title = new JLabel("Course Management");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(navy);

        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleBox.setOpaque(false);
        titleBox.add(new CourseIcon("book"));
        titleBox.add(title);

        JButton back = button("Dashboard", navy);
        back.addActionListener(e -> {
            dispose();
            new AdminDashboard();
        });

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titleBox, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        // CONTENT
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        total = new JLabel("Total Courses: 0");
        total.setFont(new Font("Arial", Font.BOLD, 17));
        total.setForeground(navy);

        JTextField search = new JTextField(16);
        search.setToolTipText("Search courses");

        JButton addButton = button("+ Add Course", teal);
        addButton.addActionListener(e -> courseForm(false));

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tools.setOpaque(false);
        tools.add(new CourseIcon("search"));
        tools.add(search);
        tools.add(addButton);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(total, BorderLayout.WEST);
        top.add(tools, BorderLayout.EAST);
        content.add(top, BorderLayout.NORTH);

        // TABLE
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(220, 242, 247));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setFillsViewportHeight(true);

        JTableHeader th = table.getTableHeader();
        th.setBackground(navy);
        th.setForeground(Color.WHITE);
        th.setFont(new Font("Arial", Font.BOLD, 12));
        th.setPreferredSize(new Dimension(0, 45));

        content.add(new JScrollPane(table), BorderLayout.CENTER);

        // EDIT / DELETE
        JButton edit = button("Edit Course", teal);
        JButton delete = button("Delete Course", new Color(210, 60, 70));

        edit.setIcon(new CourseIcon("edit"));
        delete.setIcon(new CourseIcon("delete"));

        edit.addActionListener(e -> courseForm(true));
        delete.addActionListener(e -> deleteCourse());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(edit);
        actions.add(delete);
        content.add(actions, BorderLayout.SOUTH);

        main.add(content, BorderLayout.CENTER);

        // SEARCH
        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        search.getDocument().addDocumentListener(
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
                    void filter() {
                        String text = search.getText().trim();
                        sorter.setRowFilter(text.isEmpty() ? null :
                                RowFilter.regexFilter(
                                        "(?i)" + Pattern.quote(text), 0, 1));
                    }
                }
        );

        loadCourses();
        setVisible(true);
    }

    // MYSQL COURSES
    void loadCourses() {
        try {
            model.setRowCount(0);
            for (Object[] course : dao.getAllCourses()) {
                model.addRow(course);
            }
            total.setText("Total Courses: " + model.getRowCount());
        } catch (SQLException e) {
            error(e);
        }
    }

    // ADD / EDIT COURSE
    void courseForm(boolean editing) {
        int row = table.getSelectedRow();

        if (editing && row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course first");
            return;
        }

        if (editing) row = table.convertRowIndexToModel(row);

        JTextField[] fields = new JTextField[8];
        JPanel form = new JPanel(new GridLayout(8, 2, 10, 10));

        for (int i = 0; i < 8; i++) {
            fields[i] = new JTextField(18);
            form.add(new JLabel(columns[i] + ":"));
            form.add(fields[i]);

            if (editing) {
                fields[i].setText(
                        String.valueOf(model.getValueAt(row, i)));
            }
        }

        if (editing) fields[0].setEditable(false);

        int result = JOptionPane.showConfirmDialog(
                this, form,
                editing ? "Edit Course" : "Add Course",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            String code = fields[0].getText().trim();
            String name = fields[1].getText().trim();

            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Course code and name are required");
                return;
            }

            int year = Integer.parseInt(fields[2].getText().trim());
            int sem = Integer.parseInt(fields[3].getText().trim());
            int tc = Integer.parseInt(fields[4].getText().trim());
            int pc = Integer.parseInt(fields[5].getText().trim());
            int th = Integer.parseInt(fields[6].getText().trim());
            int ph = Integer.parseInt(fields[7].getText().trim());

            if (year < 1 || sem < 1 || sem > 2 ||
                    tc < 0 || pc < 0 || th < 0 || ph < 0) {
                JOptionPane.showMessageDialog(this,
                        "Enter valid course details");
                return;
            }

            if (editing) {
                dao.updateCourse(code, name, year, sem, tc, pc, th, ph);
            } else {
                dao.addCourse(code, name, year, sem, tc, pc, th, ph);
            }

            loadCourses();
            JOptionPane.showMessageDialog(this,
                    editing ? "Course updated!" : "Course added!");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Enter valid numbers");
        } catch (SQLException e) {
            error(e);
        }
    }

    // DELETE COURSE
    void deleteCourse() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course first");
            return;
        }

        row = table.convertRowIndexToModel(row);
        String code = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this, "Delete " + code + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            dao.deleteCourse(code);
            loadCourses();
            JOptionPane.showMessageDialog(this, "Course deleted!");
        } catch (SQLException e) {
            error(e);
        }
    }

    JButton button(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(11, 16, 11, 16));
        return b;
    }

    void error(SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Database error:\n" + e.getMessage());
    }

    // DRAW ICONS
    class CourseIcon extends JComponent implements Icon {
        String type;

        CourseIcon(String type) {
            this.type = type;
            setPreferredSize(new Dimension(30, 30));
        }

        public int getIconWidth() { return 24; }
        public int getIconHeight() { return 24; }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D d = (Graphics2D) g.create();
            d.translate(x, y);
            d.setColor(c instanceof JButton ? Color.WHITE : teal);
            d.setStroke(new BasicStroke(2.2f));
            d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (type.equals("book")) {
                d.drawRect(3, 3, 18, 18);
                d.drawLine(12, 3, 12, 21);
            } else if (type.equals("search")) {
                d.drawOval(2, 2, 14, 14);
                d.drawLine(15, 16, 22, 23);
            } else if (type.equals("edit")) {
                d.drawLine(4, 19, 18, 5);
                d.drawLine(7, 21, 21, 7);
                d.drawLine(4, 19, 7, 21);
            } else if (type.equals("delete")) {
                d.drawRect(6, 7, 12, 14);
                d.drawLine(4, 5, 20, 5);
                d.drawLine(9, 3, 15, 3);
            }
            d.dispose();
        }

        protected void paintComponent(Graphics g) {
            paintIcon(this, g, 3, 3);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CourseManagement::new);
    }
}
