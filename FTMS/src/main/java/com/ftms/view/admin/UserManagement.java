
package com.ftms.view.admin;

import com.ftms.dao.UserDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class UserManagement extends JFrame {

    private final Color NAVY = new Color(18, 44, 78);
    private final Color TEAL = new Color(21, 130, 153);
    private final Color BG = new Color(237, 243, 248);
    private final Color GRAY = new Color(105, 122, 145);
    private final Color RED = new Color(210, 60, 70);

    private final UserDAO dao = new UserDAO();

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private JLabel totalCount, studentCount, lecturerCount, officerCount, footer;

    public UserManagement() {
        setTitle("FTMS - User Management");
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(22, 28, 22, 28));
        setContentPane(root);

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(NAVY);

        JLabel subtitle = new JLabel("Manage faculty user accounts");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(GRAY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitle);

        JButton dashboard = makeButton("Dashboard", NAVY);
        dashboard.addActionListener(e -> {
            dispose();
            new AdminDashboard();
        });

        header.add(titlePanel, BorderLayout.WEST);
        header.add(dashboard, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        // STATISTICS CARDS
        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(0, 125));

        totalCount = new JLabel("00");
        studentCount = new JLabel("00");
        lecturerCount = new JLabel("00");
        officerCount = new JLabel("00");

        stats.add(statCard("Total Users", totalCount, 0));
        stats.add(statCard("Students", studentCount, 1));
        stats.add(statCard("Lecturers", lecturerCount, 2));
        stats.add(statCard("Officers", officerCount, 3));

        content.add(stats, BorderLayout.NORTH);

        // TABLE PANEL
        JPanel tablePanel = new RoundPanel();
        tablePanel.setLayout(new BorderLayout(0, 16));
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel toolbar = new JPanel(new BorderLayout(15, 0));
        toolbar.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setToolTipText("Search users");
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 232, 240)),
                new EmptyBorder(11, 14, 11, 14)
        ));

        JLabel searchIcon = new IconLabel(4);
        searchIcon.setPreferredSize(new Dimension(32, 35));

        JButton addButton = makeButton("+ Add User", TEAL);
        addButton.addActionListener(e -> userForm(-1));

        toolbar.add(searchIcon, BorderLayout.WEST);
        toolbar.add(searchField, BorderLayout.CENTER);
        toolbar.add(addButton, BorderLayout.EAST);
        tablePanel.add(toolbar, BorderLayout.NORTH);

        // USER TABLE
        model = new DefaultTableModel(
                new String[]{"ID", "Name", "Username", "Role",
                        "Email", "Phone", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 6;
            }
        };

        table = new JTable(model);
        table.setRowHeight(45);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setForeground(NAVY);
        table.setSelectionBackground(new Color(220, 242, 247));
        table.setSelectionForeground(NAVY);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(233, 239, 245));
        table.setFillsViewportHeight(true);

        JTableHeader th = table.getTableHeader();
        th.setPreferredSize(new Dimension(0, 45));
        th.setFont(new Font("Arial", Font.BOLD, 13));
        th.setBackground(NAVY);
        th.setForeground(Color.WHITE);
        th.setReorderingAllowed(false);

        int[] widths = {40, 150, 120, 120, 190, 100, 130};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean selected,
                    boolean focus, int row, int col) {

                super.getTableCellRendererComponent(
                        t, value, selected, focus, row, col);

                setBorder(new EmptyBorder(0, 10, 0, 5));
                setForeground(col == 1 ? NAVY : GRAY);

                if (!selected) setBackground(Color.WHITE);
                return this;
            }
        };

        for (int i = 0; i < 6; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        table.getColumnModel().getColumn(6)
                .setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(6)
                .setCellEditor(new ActionEditor());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scroll, BorderLayout.CENTER);

        footer = new JLabel("Loading users...");
        footer.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.setForeground(GRAY);
        tablePanel.add(footer, BorderLayout.SOUTH);

        content.add(tablePanel, BorderLayout.CENTER);
        root.add(content, BorderLayout.CENTER);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        search();
                    }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        search();
                    }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        search();
                    }
                });

        loadUsers();
        setVisible(true);
    }

    // STATISTICS CARD
    private JPanel statCard(String label, JLabel count, int iconType) {
        JPanel card = new RoundPanel();
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(new EmptyBorder(12, 18, 14, 18));

        JLabel icon = new IconLabel(iconType);
        icon.setPreferredSize(new Dimension(40, 35));

        count.setFont(new Font("Arial", Font.BOLD, 27));
        count.setForeground(NAVY);

        JLabel text = new JLabel(label);
        text.setFont(new Font("Arial", Font.PLAIN, 13));
        text.setForeground(GRAY);

        card.add(icon, BorderLayout.NORTH);
        card.add(count, BorderLayout.CENTER);
        card.add(text, BorderLayout.SOUTH);
        return card;
    }

    // DRAW ICONS WITHOUT EMOJIS OR PNG FILES
    class IconLabel extends JLabel {
        private final int type;

        IconLabel(int type) {
            this.type = type;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D d = (Graphics2D) g.create();
            d.setColor(TEAL);
            d.setStroke(new BasicStroke(2.2f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (type == 0) { // USERS
                d.drawOval(13, 4, 10, 10);
                d.drawArc(9, 17, 18, 14, 0, 180);
                d.drawOval(2, 9, 8, 8);
                d.drawOval(26, 9, 8, 8);
                d.drawArc(0, 20, 10, 10, 0, 180);
                d.drawArc(26, 20, 10, 10, 0, 180);

            } else if (type == 1) { // STUDENTS
                d.drawPolygon(new int[]{3, 18, 33, 18},
                        new int[]{12, 4, 12, 20}, 4);
                d.drawLine(10, 19, 10, 25);
                d.drawArc(10, 17, 16, 14, 180, 180);

            } else if (type == 2) { // LECTURERS
                d.drawRect(5, 5, 28, 25);
                d.drawLine(19, 5, 19, 30);

            } else if (type == 3) { // OFFICERS
                d.drawOval(10, 4, 13, 13);
                d.drawArc(5, 18, 25, 16, 0, 180);
                d.drawOval(27, 22, 7, 7);

            } else if (type == 4) { // SEARCH
                d.drawOval(4, 4, 17, 17);
                d.drawLine(18, 19, 29, 30);
            }

            d.dispose();
        }
    }

    // LOAD USERS FROM MYSQL
    private void loadUsers() {
        try {
            List<Object[]> users = dao.getAllUsers();
            model.setRowCount(0);

            for (Object[] user : users) {
                model.addRow(new Object[]{
                        user[0], user[1], user[2],
                        user[3], user[4], user[5], ""
                });
            }

            updateStats();
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void updateStats() {
        int students = 0, lecturers = 0, officers = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String role = String.valueOf(model.getValueAt(i, 3));

            if (role.equalsIgnoreCase("STUDENT")) students++;
            if (role.equalsIgnoreCase("LECTURER")) lecturers++;
            if (role.equalsIgnoreCase("TECHNICAL_OFFICER")) officers++;
        }

        totalCount.setText(String.format("%02d", model.getRowCount()));
        studentCount.setText(String.format("%02d", students));
        lecturerCount.setText(String.format("%02d", lecturers));
        officerCount.setText(String.format("%02d", officers));
        footer.setText("Showing " + table.getRowCount() + " users");
    }

    private void search() {
        String value = searchField.getText().trim();

        sorter.setRowFilter(value.isEmpty() ? null :
                RowFilter.regexFilter("(?i)" + Pattern.quote(value),
                        1, 2, 3, 4, 5));

        footer.setText("Showing " + table.getRowCount() + " users");
    }

    // ADD / EDIT USER
    private void userForm(int row) {
        boolean editing = row >= 0;

        JTextField username = new JTextField(20);
        JTextField fname = new JTextField(20);
        JTextField lname = new JTextField(20);
        JTextField email = new JTextField(20);
        JTextField phone = new JTextField(20);
        JPasswordField password = new JPasswordField(20);

        JComboBox<String> role = new JComboBox<>(
                new String[]{"ADMIN", "LECTURER",
                        "STUDENT", "TECHNICAL_OFFICER"});

        int id = -1;

        if (editing) {
            id = Integer.parseInt(model.getValueAt(row, 0).toString());
            username.setText(String.valueOf(model.getValueAt(row, 2)));
            email.setText(String.valueOf(model.getValueAt(row, 4)));
            phone.setText(String.valueOf(model.getValueAt(row, 5)));
            role.setSelectedItem(model.getValueAt(row, 3));

            String fullName = String.valueOf(
                    model.getValueAt(row, 1)).trim();
            String[] names = fullName.split(" ", 2);
            fname.setText(names[0]);
            if (names.length > 1) lname.setText(names[1]);
        }

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 12));
        form.setBorder(new EmptyBorder(12, 10, 12, 10));

        form.add(new JLabel("Username:"));
        form.add(username);
        form.add(new JLabel("First Name:"));
        form.add(fname);
        form.add(new JLabel("Last Name:"));
        form.add(lname);
        form.add(new JLabel("Email:"));
        form.add(email);
        form.add(new JLabel("Phone:"));
        form.add(phone);
        form.add(new JLabel("Role:"));
        form.add(role);
        form.add(new JLabel(
                editing ? "New Password (optional):" : "Password:"));
        form.add(password);

        int result = JOptionPane.showConfirmDialog(
                this, form, editing ? "Edit User" : "Add User",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        String userValue = username.getText().trim();
        String firstValue = fname.getText().trim();
        String lastValue = lname.getText().trim();
        String emailValue = email.getText().trim();
        String phoneValue = phone.getText().trim();
        String passwordValue = new String(password.getPassword());
        String roleValue = role.getSelectedItem().toString();

        if (userValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required.");
            return;
        }

        if (!editing && passwordValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required.");
            return;
        }

        if (!emailValue.isEmpty() &&
                !emailValue.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            JOptionPane.showMessageDialog(this, "Invalid email.");
            return;
        }

        try {
            if (editing) {
                dao.updateUser(id, userValue, firstValue, lastValue,
                        roleValue, emailValue, phoneValue, passwordValue);
            } else {
                dao.addUser(userValue, passwordValue, firstValue,
                        lastValue, roleValue, emailValue, phoneValue);
            }

            loadUsers();
            JOptionPane.showMessageDialog(this,
                    editing ? "User updated!" : "User added!");

        } catch (SQLException e) {
            showError(e);
        }
    }

    // DELETE USER
    private void deleteUser(int row) {
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String name = String.valueOf(model.getValueAt(row, 1));

        int confirm = JOptionPane.showConfirmDialog(
                this, "Delete user: " + name + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            dao.deleteUser(id);
            loadUsers();
            JOptionPane.showMessageDialog(this, "User deleted!");
        } catch (SQLException e) {
            showError(e);
        }
    }

    // EDIT / DELETE BUTTONS
    private JPanel actionPanel() {
        JPanel panel = new JPanel(new FlowLayout(
                FlowLayout.CENTER, 8, 5));
        panel.setBackground(Color.WHITE);

        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");

        edit.setForeground(TEAL);
        delete.setForeground(RED);

        for (JButton b : new JButton[]{edit, delete}) {
            b.setFont(new Font("Arial", Font.BOLD, 12));
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(
                    Cursor.HAND_CURSOR));
        }

        panel.add(edit);
        panel.add(delete);
        return panel;
    }

    class ActionRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object value, boolean selected,
                boolean focus, int row, int col) {
            return actionPanel();
        }
    }

    class ActionEditor extends AbstractCellEditor
            implements TableCellEditor {

        private final JPanel panel = actionPanel();
        private int currentRow;

        ActionEditor() {
            JButton edit = (JButton) panel.getComponent(0);
            JButton delete = (JButton) panel.getComponent(1);

            edit.addActionListener(e -> {
                int row = currentRow;
                fireEditingStopped();
                userForm(row);
            });

            delete.addActionListener(e -> {
                int row = currentRow;
                fireEditingStopped();
                deleteUser(row);
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable t, Object value, boolean selected,
                int row, int col) {
            currentRow = t.convertRowIndexToModel(row);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private JButton makeButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));
        return button;
    }

    private void showError(SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Database error:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    class RoundPanel extends JPanel {
        RoundPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D d = (Graphics2D) g.create();
            d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            d.setColor(Color.WHITE);
            d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            d.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserManagement::new);
    }
}
