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
            "Theory Hours", "Practical Hours", "Department"
    };

    public CourseManagement() {

        setTitle("FTMS - Course Management");
        setSize(1100, 650);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        // ASSIGN LECTURER BUTTON
        JButton assignButton = button("Assign Lecturer", navy);
        assignButton.addActionListener(e -> assignLecturer());

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tools.setOpaque(false);
        tools.add(new CourseIcon("search"));
        tools.add(search);
        tools.add(assignButton);
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

                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e) {
                        filter();
                    }

                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e) {
                        filter();
                    }

                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e) {
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

    // LOAD COURSES
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

        if (editing) {
            row = table.convertRowIndexToModel(row);
        }

        JTextField codeField = new JTextField(18);
        JTextField nameField = new JTextField(18);

        codeField.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offset, String str,
                                     javax.swing.text.AttributeSet attr)
                    throws javax.swing.text.BadLocationException {

                if (str != null) {
                    super.insertString(offset, str.toUpperCase(java.util.Locale.ROOT), attr);
                }
            }

            @Override
            public void replace(int offset, int length, String str,
                                javax.swing.text.AttributeSet attrs)
                    throws javax.swing.text.BadLocationException {

                super.replace(offset, length,
                        str == null ? null : str.toUpperCase(java.util.Locale.ROOT),
                        attrs);
            }
        });




        JComboBox<String> level = new JComboBox<>(
                new String[]{
                        "Level 1",
                        "Level 2",
                        "Level 3",
                        "Level 4"
                });

        JComboBox<String> semester = new JComboBox<>(
                new String[]{"Semester 1", "Semester 2"});

        JComboBox<String> courseType = new JComboBox<>(
                new String[]{"Theory", "Practical"});

        JTextField theoryCredits = new JTextField("0");
        JTextField practicalCredits = new JTextField("0");
        JTextField theoryHours = new JTextField("0");
        JTextField practicalHours = new JTextField("0");

        theoryCredits.setEditable(false);
        practicalCredits.setEditable(false);
        theoryHours.setEditable(false);
        practicalHours.setEditable(false);

        JComboBox<String> department = new JComboBox<>(
                new String[]{"ICT", "ET", "BST"});

        // LOAD EXISTING DATA
        if (editing) {

            codeField.setText(String.valueOf(model.getValueAt(row, 0)));
            nameField.setText(String.valueOf(model.getValueAt(row, 1)));

            int year = Integer.parseInt(
                    model.getValueAt(row, 2).toString());

            int sem = Integer.parseInt(
                    model.getValueAt(row, 3).toString());

            int tc = Integer.parseInt(
                    model.getValueAt(row, 4).toString());

            int pc = Integer.parseInt(
                    model.getValueAt(row, 5).toString());

            level.setSelectedIndex(year == 2 ? 1 : 0);
            semester.setSelectedIndex(sem == 2 ? 1 : 0);

            courseType.setSelectedItem(
                    pc > 0 && tc == 0 ? "Practical" : "Theory");

            department.setSelectedItem(
                    String.valueOf(model.getValueAt(row, 8)));

            codeField.setEditable(false);
        }

        // AUTOMATIC CREDIT AND HOURS
        Runnable calculate = () -> {

            theoryCredits.setText("0");
            practicalCredits.setText("0");
            theoryHours.setText("0");
            practicalHours.setText("0");

            String code = codeField.getText().trim();

            if (code.isEmpty()) return;

            char last = code.charAt(code.length() - 1);

            if (!Character.isDigit(last)) return;

            int credits = Character.getNumericValue(last);
            int hours = credits * 2;

            String type = (String) courseType.getSelectedItem();

            if ("Theory".equals(type)) {
                theoryCredits.setText(String.valueOf(credits));
                theoryHours.setText(String.valueOf(hours));
            } else {
                practicalCredits.setText(String.valueOf(credits));
                practicalHours.setText(String.valueOf(hours));
            }
        };

        // WHEN COURSE CODE CHANGES
        codeField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e) {
                        calculate.run();
                    }

                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e) {
                        calculate.run();
                    }

                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e) {
                        calculate.run();
                    }
                }
        );

        // WHEN COURSE TYPE CHANGES
        courseType.addActionListener(e -> calculate.run());

        calculate.run();

        // FORM
        JPanel form = new JPanel(new GridLayout(10, 2, 10, 10));

        form.add(new JLabel("Course Code:"));
        form.add(codeField);

        form.add(new JLabel("Course Name:"));
        form.add(nameField);

        form.add(new JLabel("Level:"));
        form.add(level);

        form.add(new JLabel("Semester:"));
        form.add(semester);

        form.add(new JLabel("Course Type:"));
        form.add(courseType);

        form.add(new JLabel("Theory Credits:"));
        form.add(theoryCredits);

        form.add(new JLabel("Practical Credits:"));
        form.add(practicalCredits);

        form.add(new JLabel("Theory Hours:"));
        form.add(theoryHours);

        form.add(new JLabel("Practical Hours:"));
        form.add(practicalHours);

        form.add(new JLabel("Department:"));
        form.add(department);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                editing ? "Edit Course" : "Add Course",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {

            String code = codeField.getText().trim();
            String name = nameField.getText().trim();

            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Course code and name are required");
                return;
            }

            char last = code.charAt(code.length() - 1);

            if (!Character.isDigit(last)) {
                JOptionPane.showMessageDialog(
                        this, "Course code must end with a credit number");
                return;
            }

            int credits = Character.getNumericValue(last);

            if (credits <= 0) {
                JOptionPane.showMessageDialog(
                        this, "Credits must be greater than zero");
                return;
            }

            int year = level.getSelectedIndex() + 1;
            int sem = semester.getSelectedIndex() + 1;

            int tc = 0;
            int pc = 0;
            int th = 0;
            int ph = 0;

            String type = (String) courseType.getSelectedItem();

            if ("Theory".equals(type)) {
                tc = credits;
                th = credits * 2;
            } else {
                pc = credits;
                ph = credits * 2;
            }

            String dept = (String) department.getSelectedItem();

            if (editing) {
                dao.updateCourse(
                        code, name, year, sem,
                        tc, pc, th, ph, dept);
            } else {
                dao.addCourse(
                        code, name, year, sem,
                        tc, pc, th, ph, dept);
            }

            loadCourses();

            JOptionPane.showMessageDialog(
                    this,
                    editing ? "Course updated!" : "Course added!");

        } catch (SQLException e) {
            error(e);
        }
    }


    // ASSIGN LECTURER TO COURSE
    void assignLecturer() {

        JComboBox<String> department = new JComboBox<>(
                new String[]{"ICT", "ET", "BST"});

        JComboBox<String> lecturer = new JComboBox<>();
        JComboBox<String> course = new JComboBox<>();

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 12));

        form.add(new JLabel("Department:"));
        form.add(department);

        form.add(new JLabel("Lecturer ID:"));
        form.add(lecturer);

        form.add(new JLabel("Course Code:"));
        form.add(course);

        // LOAD LECTURERS AND COURSES
        Runnable loadOptions = () -> {

            try {

                String selectedDepartment =
                        department.getSelectedItem().toString();

                lecturer.removeAllItems();
                course.removeAllItems();

                for (String id :
                        dao.getLecturersByDepartment(selectedDepartment)) {

                    lecturer.addItem(id);
                }

                for (String code :
                        dao.getCourseCodesByDepartment(selectedDepartment)) {

                    course.addItem(code);
                }

            } catch (SQLException e) {
                error(e);
            }
        };

        department.addActionListener(e -> loadOptions.run());

        loadOptions.run();

        if (lecturer.getItemCount() == 0 ||
                course.getItemCount() == 0) {

            JOptionPane.showMessageDialog(this,
                    "No lecturers or courses available for this department.");

            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Assign Lecturer to Course",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            if (lecturer.getSelectedItem() == null ||
                    course.getSelectedItem() == null) {

                JOptionPane.showMessageDialog(this,
                        "Select a lecturer and course first.");
                return;
            }

            int lecturerId = Integer.parseInt(
                    lecturer.getSelectedItem().toString());

            String courseCode =
                    course.getSelectedItem().toString();

            dao.assignLecturerToCourse(lecturerId, courseCode);

            JOptionPane.showMessageDialog(this,
                    "Lecturer assigned to course successfully!");

        } catch (SQLException e) {

            if (e.getMessage() != null &&
                    e.getMessage().toLowerCase().contains("duplicate")) {

                JOptionPane.showMessageDialog(this,
                        "This lecturer is already assigned to this course.");

            } else {
                error(e);
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(this,
                    "Invalid Lecturer ID.");
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
                this,
                "Delete " + code + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            dao.deleteCourse(code);

            loadCourses();

            JOptionPane.showMessageDialog(this, "Course deleted!");

        } catch (SQLException e) {
            error(e);
        }
    }

    // BUTTON DESIGN
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

    // ERROR MESSAGE
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

        public int getIconWidth() {
            return 24;
        }

        public int getIconHeight() {
            return 24;
        }

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