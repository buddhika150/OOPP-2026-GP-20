package com.ftms.view.technicalofficer;

import com.ftms.dao.AttendanceDAO;
import com.ftms.model.Attendance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * AddAttendancePanel - UI screen for recording student attendance sessions.
 * Matches FTMS schema: Attendance(Attendance_ID, Student_ID, Course_code,
 * Session_date, Type).
 * Integrates with AttendanceDAO for duplicate verification and persistence.
 */
public class AddAttendancePanel extends JPanel {

    // Visual Palette matching reference screenshot
    private static final Color BG_PAGE = new Color(0xF4, 0xF7, 0xFB);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xE2, 0xE8, 0xF0);
    private static final Color TEXT_PRIMARY = new Color(0x1E, 0x29, 0x3B);
    private static final Color TEXT_MUTED = new Color(0x64, 0x74, 0x8B);
    private static final Color TEAL_PRIMARY = new Color(0x00, 0xA8, 0xA8);
    private static final Color TEAL_HOVER = new Color(0x00, 0x8F, 0x8F);
    private static final Color SUCCESS_BG = new Color(0xDC, 0xFC, 0xE7);
    private static final Color SUCCESS_TEXT = new Color(0x15, 0x80, 0x3D);
    private static final Color ERROR_BG = new Color(0xFE, 0xE2, 0xE2);
    private static final Color ERROR_TEXT = new Color(0xB9, 0x1C, 0x1C);

    private final AttendanceDAO attendanceDAO;
    private JTextField studentIdField;
    private JComboBox<String> courseComboBox;
    private JTextField sessionDateField;
    private JRadioButton theoryRadio;
    private JRadioButton practicalRadio;
    private JPanel feedbackPanel;
    private JLabel feedbackLabel;
    private DefaultTableModel recentTableModel;

    public AddAttendancePanel() {
        this(new AttendanceDAO());
    }

    public AddAttendancePanel(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO != null ? attendanceDAO : new AttendanceDAO();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 20));
        setBackground(BG_PAGE);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Add Attendance");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Record student attendance for theory and practical lecture sessions");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_MUTED);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Feedback Banner
        feedbackPanel = new JPanel(new BorderLayout());
        feedbackPanel.setOpaque(true);
        feedbackPanel.setBackground(SUCCESS_BG);
        feedbackPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xBB, 0xF7, 0xD0), 1, true),
                new EmptyBorder(10, 15, 10, 15)));
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        feedbackLabel.setForeground(SUCCESS_TEXT);
        feedbackPanel.add(feedbackLabel, BorderLayout.CENTER);
        feedbackPanel.setVisible(false);

        JPanel topContainer = new JPanel(new BorderLayout(0, 12));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(feedbackPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);

        // Content Area: Left Form Card + Right Recent Entries Card
        JPanel contentGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        contentGrid.setOpaque(false);

        contentGrid.add(createFormCard());
        contentGrid.add(createRecentTableCard());

        add(contentGrid, BorderLayout.CENTER);
    }

    private JPanel createFormCard() {
        JPanel card = new JPanel(new BorderLayout(0, 18));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(24, 24, 24, 24)));

        // Card Title
        JLabel cardTitle = new JLabel("Attendance Details");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardTitle.setForeground(TEXT_PRIMARY);
        card.add(cardTitle, BorderLayout.NORTH);

        // Form Fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int row = 0;

        // Student ID
        gbc.gridy = row++;
        fieldsPanel.add(createFieldLabel("Student ID * (User ID from schema, e.g., 1 - 20)"), gbc);

        gbc.gridy = row++;
        studentIdField = createStyledTextField();
        fieldsPanel.add(studentIdField, gbc);

        // Course Code
        gbc.gridy = row++;
        fieldsPanel.add(createFieldLabel("Course / Module Code *"), gbc);

        gbc.gridy = row++;
        String[] courses = {
                "ICT2131 - Database Management Systems",
                "ICT2132 - Object Oriented Programming",
                "ICT2133 - Data Structures",
                "ICT2134 - Software Engineering",
                "ICT2135 - Web Application Development"
        };
        courseComboBox = new JComboBox<>(courses);
        courseComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseComboBox.setBackground(Color.WHITE);
        courseComboBox.setPreferredSize(new Dimension(200, 36));
        fieldsPanel.add(courseComboBox, gbc);

        // Session Date
        gbc.gridy = row++;
        fieldsPanel.add(createFieldLabel("Session Date * (YYYY-MM-DD)"), gbc);

        gbc.gridy = row++;
        JPanel dateRow = new JPanel(new BorderLayout(8, 0));
        dateRow.setOpaque(false);
        sessionDateField = createStyledTextField();
        sessionDateField.setText(LocalDate.now().toString());
        dateRow.add(sessionDateField, BorderLayout.CENTER);

        JButton todayBtn = new JButton("Today");
        todayBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        todayBtn.setBackground(new Color(0xEE, 0xF2, 0xF6));
        todayBtn.setForeground(TEXT_PRIMARY);
        todayBtn.setFocusPainted(false);
        todayBtn.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        todayBtn.setPreferredSize(new Dimension(65, 36));
        todayBtn.addActionListener(e -> sessionDateField.setText(LocalDate.now().toString()));
        dateRow.add(todayBtn, BorderLayout.EAST);

        fieldsPanel.add(dateRow, gbc);

        // Session Type (Theory vs Practical)
        gbc.gridy = row++;
        fieldsPanel.add(createFieldLabel("Session Type *"), gbc);

        gbc.gridy = row++;
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        typePanel.setOpaque(false);
        theoryRadio = new JRadioButton("THEORY", true);
        theoryRadio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        theoryRadio.setForeground(TEXT_PRIMARY);
        theoryRadio.setOpaque(false);

        practicalRadio = new JRadioButton("PRACTICAL");
        practicalRadio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        practicalRadio.setForeground(TEXT_PRIMARY);
        practicalRadio.setOpaque(false);

        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(theoryRadio);
        typeGroup.add(practicalRadio);

        typePanel.add(theoryRadio);
        typePanel.add(practicalRadio);
        fieldsPanel.add(typePanel, gbc);

        card.add(fieldsPanel, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clearBtn.setBackground(Color.WHITE);
        clearBtn.setForeground(TEXT_MUTED);
        clearBtn.setFocusPainted(false);
        clearBtn.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        clearBtn.setPreferredSize(new Dimension(85, 38));
        clearBtn.addActionListener(e -> clearForm());

        JButton saveBtn = new JButton("Save Attendance");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        saveBtn.setBackground(TEAL_PRIMARY);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorder(new EmptyBorder(8, 16, 8, 16));
        saveBtn.setPreferredSize(new Dimension(140, 38));
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> handleSaveAttendance());

        actionPanel.add(clearBtn);
        actionPanel.add(saveBtn);

        card.add(actionPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createRecentTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(24, 24, 24, 24)));

        JLabel title = new JLabel("Recently Recorded Sessions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_PRIMARY);
        card.add(title, BorderLayout.NORTH);

        String[] cols = { "Attendance ID", "Student ID", "Course", "Date", "Type" };
        recentTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(recentTableModel);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(0xF1, 0xF5, 0xF9));
        table.setSelectionBackground(new Color(0xE0, 0xF2, 0xF1));
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(0xF8, 0xFA, 0xFC));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private void handleSaveAttendance() {
        hideFeedback();

        // 1. Validate Student ID
        String studentIdText = studentIdField.getText().trim();
        if (studentIdText.isEmpty()) {
            showFeedback("Please enter a valid Student ID.", false);
            studentIdField.requestFocus();
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(studentIdText);
            if (studentId <= 0) {
                showFeedback("Student ID must be a positive integer.", false);
                return;
            }
        } catch (NumberFormatException ex) {
            showFeedback("Invalid Student ID format. Please enter numeric ID (e.g., 1 to 20).", false);
            return;
        }

        // 2. Validate Course Code
        String selectedCourseItem = (String) courseComboBox.getSelectedItem();
        if (selectedCourseItem == null || selectedCourseItem.trim().isEmpty()) {
            showFeedback("Please select a course code.", false);
            return;
        }
        String courseCode = selectedCourseItem.split(" - ")[0].trim();

        // 3. Validate Date
        String dateText = sessionDateField.getText().trim();
        LocalDate sessionDate;
        try {
            sessionDate = LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            showFeedback("Invalid date format. Please use YYYY-MM-DD.", false);
            return;
        }

        // 4. Session Type
        String sessionType = theoryRadio.isSelected() ? Attendance.TYPE_THEORY : Attendance.TYPE_PRACTICAL;

        // 5. Database Duplicate Check & Insertion via AttendanceDAO
        try {
            Attendance attendance = new Attendance(studentId, courseCode, sessionDate, sessionType);
            boolean inserted = attendanceDAO.insertAttendance(attendance);

            if (inserted) {
                showFeedback("Attendance successfully recorded! ID: #" + attendance.getAttendanceId(), true);
                recentTableModel.insertRow(0, new Object[] {
                        "#" + attendance.getAttendanceId(),
                        studentId,
                        courseCode,
                        sessionDate.toString(),
                        sessionType
                });
                studentIdField.setText("");
                studentIdField.requestFocus();
            } else {
                showFeedback("Duplicate Attendance: Already recorded for Student #" + studentId +
                        ", Course " + courseCode + ", Date " + sessionDate + " (" + sessionType + ").", false);
            }
        } catch (SQLException ex) {
            showFeedback("Database Error: " + ex.getMessage(), false);
        } catch (Exception ex) {
            showFeedback("Error recording attendance: " + ex.getMessage(), false);
        }
    }

    private void clearForm() {
        studentIdField.setText("");
        sessionDateField.setText(LocalDate.now().toString());
        theoryRadio.setSelected(true);
        hideFeedback();
    }

    private void showFeedback(String message, boolean isSuccess) {
        feedbackLabel.setText(message);
        if (isSuccess) {
            feedbackPanel.setBackground(SUCCESS_BG);
            feedbackLabel.setForeground(SUCCESS_TEXT);
            feedbackPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(0xBB, 0xF7, 0xD0), 1, true),
                    new EmptyBorder(10, 15, 10, 15)));
        } else {
            feedbackPanel.setBackground(ERROR_BG);
            feedbackLabel.setForeground(ERROR_TEXT);
            feedbackPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(0xFE, 0xCA, 0xCA), 1, true),
                    new EmptyBorder(10, 15, 10, 15)));
        }
        feedbackPanel.setVisible(true);
        revalidate();
    }

    private void hideFeedback() {
        feedbackPanel.setVisible(false);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(200, 36));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(4, 10, 4, 10)));
        return tf;
    }
}
