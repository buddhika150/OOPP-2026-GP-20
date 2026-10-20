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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * AddMedicalPanel - UI screen for registering student medical certificates.
 * Matches FTMS schema: Medical(Medical_ID, Student_ID, Attendance_ID, Submission_date,
 * Start_date, End_date, Reason, Approval_status, Status).
 * Integrates with MedicalDAO for persistence and validation.
 */
public class AddMedicalPanel extends JPanel {

    // Theme Palette matching reference screenshot
    private static final Color BG_PAGE = new Color(0xF4, 0xF7, 0xFB);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xE2, 0xE8, 0xF0);
    private static final Color TEXT_PRIMARY = new Color(0x1E, 0x29, 0x3B);
    private static final Color TEXT_MUTED = new Color(0x64, 0x74, 0x8B);
    private static final Color TEAL_PRIMARY = new Color(0x00, 0xA8, 0xA8);
    private static final Color SUCCESS_BG = new Color(0xDC, 0xFC, 0xE7);
    private static final Color SUCCESS_TEXT = new Color(0x15, 0x80, 0x3D);
    private static final Color ERROR_BG = new Color(0xFE, 0xE2, 0xE2);
    private static final Color ERROR_TEXT = new Color(0xB9, 0x1C, 0x1C);
    private static final Color PILL_YELLOW_BG = new Color(0xFE, 0xF3, 0xC7);
    private static final Color PILL_YELLOW_TEXT = new Color(0xB4, 0x53, 0x09);

    private final MedicalDAO medicalDAO;

    private JTextField studentIdField;
    private JTextField attendanceIdField;
    private JTextField submissionDateField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextArea reasonArea;
    private JPanel feedbackPanel;
    private JLabel feedbackLabel;
    private DefaultTableModel recentTableModel;

    public AddMedicalPanel() {
        this(new MedicalDAO());
    }

    public AddMedicalPanel(MedicalDAO medicalDAO) {
        this.medicalDAO = medicalDAO != null ? medicalDAO : new MedicalDAO();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 20));
        setBackground(BG_PAGE);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Add Medical Record");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Submit student medical certificate details for lecture sessions and exams");
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
                new EmptyBorder(10, 15, 10, 15)
        ));
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

        // Content Area: Form Card (Left) + Recent Medical Submissions (Right)
        JPanel contentGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        contentGrid.setOpaque(false);

        contentGrid.add(createFormCard());
        contentGrid.add(createRecentTableCard());

        add(contentGrid, BorderLayout.CENTER);
    }

    private JPanel createFormCard() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(22, 24, 22, 24)
        ));

        JLabel cardTitle = new JLabel("Medical Certificate Information");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardTitle.setForeground(TEXT_PRIMARY);
        card.add(cardTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int row = 0;

        // Student ID
        gbc.gridy = row++;
        fieldsPanel.add(createFieldLabel("Student ID * (User ID from schema, e.g., 1 - 20)"), gbc);
        gbc.gridy = row++;
        studentIdField = createStyledTextField();
        fieldsPanel.add(studentIdField, gbc);

        // Attendance ID (Optional)
        gbc.gridy = row++;
        fieldsPanel.add(createFieldLabel("Linked Attendance ID (Optional, leave blank if general leave)"), gbc);
        gbc.gridy = row++;
        attendanceIdField = createStyledTextField();
        fieldsPanel.add(attendanceIdField, gbc);

        // Submission Date
        gbc.gridy = row++;
        fieldsPanel.add(createFieldLabel("Submission Date * (YYYY-MM-DD)"), gbc);
        gbc.gridy = row++;
        submissionDateField = createStyledTextField();
        submissionDateField.setText(LocalDate.now().toString());
        fieldsPanel.add(submissionDateField, gbc);

        // Date Range (Start Date and End Date in 2 columns)
        gbc.gridy = row++;
        fieldsPanel.add(createFieldLabel("Medical Leave Period * (YYYY-MM-DD)"), gbc);

        gbc.gridy = row++;
        JPanel dateRangePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        dateRangePanel.setOpaque(false);

        startDateField = createStyledTextField();
        startDateField.setText(LocalDate.now().toString());

        endDateField = createStyledTextField();
        endDateField.setText(LocalDate.now().plusDays(2).toString());

        dateRangePanel.add(startDateField);
        dateRangePanel.add(endDateField);
        fieldsPanel.add(dateRangePanel, gbc);

        // Reason Area
        gbc.gridy = row++;
        fieldsPanel.add(createFieldLabel("Medical Reason / Doctor Diagnosis *"), gbc);

        gbc.gridy = row++;
        reasonArea = new JTextArea(4, 20);
        reasonArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        reasonScroll.setPreferredSize(new Dimension(200, 85));
        fieldsPanel.add(reasonScroll, gbc);

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

        JButton submitBtn = new JButton("Submit Medical Record");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        submitBtn.setBackground(TEAL_PRIMARY);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setBorder(new EmptyBorder(8, 16, 8, 16));
        submitBtn.setPreferredSize(new Dimension(170, 38));
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitBtn.addActionListener(e -> handleSubmitMedical());

        actionPanel.add(clearBtn);
        actionPanel.add(submitBtn);

        card.add(actionPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createRecentTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(22, 24, 22, 24)
        ));

        JLabel title = new JLabel("Recent Medical Submissions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_PRIMARY);
        card.add(title, BorderLayout.NORTH);

        String[] cols = {"Medical ID", "Student ID", "Att. ID", "Period", "Status"};
        recentTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(recentTableModel);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(0xF1, 0xF5, 0xF9));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(0xF8, 0xFA, 0xFC));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < cols.length - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setOpaque(true);
                lbl.setBackground(PILL_YELLOW_BG);
                lbl.setForeground(PILL_YELLOW_TEXT);
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private void handleSubmitMedical() {
        hideFeedback();

        // 1. Validate Student ID
        String studentIdText = studentIdField.getText().trim();
        if (studentIdText.isEmpty()) {
            showFeedback("Please enter a Student ID.", false);
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
            showFeedback("Invalid Student ID format. Must be numeric.", false);
            return;
        }

        // 2. Attendance ID (Optional)
        String attendanceIdText = attendanceIdField.getText().trim();
        Integer attendanceId = null;
        if (!attendanceIdText.isEmpty()) {
            try {
                attendanceId = Integer.parseInt(attendanceIdText);
                if (attendanceId <= 0) {
                    showFeedback("Attendance ID must be a positive integer if provided.", false);
                    return;
                }
            } catch (NumberFormatException ex) {
                showFeedback("Invalid Attendance ID format.", false);
                return;
            }
        }

        // 3. Submission Date
        String subDateText = submissionDateField.getText().trim();
        LocalDate submissionDate;
        try {
            submissionDate = LocalDate.parse(subDateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            showFeedback("Invalid Submission Date. Format must be YYYY-MM-DD.", false);
            return;
        }

        // 4. Start Date & End Date
        String startDateText = startDateField.getText().trim();
        String endDateText = endDateField.getText().trim();
        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.parse(startDateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            showFeedback("Invalid Start Date. Format must be YYYY-MM-DD.", false);
            return;
        }

        try {
            endDate = LocalDate.parse(endDateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            showFeedback("Invalid End Date. Format must be YYYY-MM-DD.", false);
            return;
        }

        if (startDate.isAfter(endDate)) {
            showFeedback("Start Date cannot be after End Date.", false);
            return;
        }

        // 5. Reason
        String reason = reasonArea.getText().trim();
        if (reason.isEmpty()) {
            showFeedback("Please provide a medical reason or diagnosis.", false);
            reasonArea.requestFocus();
            return;
        }

        // 6. Build model and insert via MedicalDAO
        try {
            Medical medical = new Medical(studentId, attendanceId, submissionDate, startDate, endDate, reason);
            boolean inserted = medicalDAO.insertMedical(medical);

            if (inserted) {
                showFeedback("Medical certificate successfully registered! ID: #" + medical.getMedicalId(), true);
                recentTableModel.insertRow(0, new Object[]{
                        "#" + medical.getMedicalId(),
                        studentId,
                        attendanceId != null ? "#" + attendanceId : "-",
                        startDate + " to " + endDate,
                        medical.getApprovalStatus()
                });
                clearFormAfterSuccess();
            } else {
                showFeedback("Failed to insert medical record. Please verify constraints.", false);
            }
        } catch (SQLException ex) {
            showFeedback("Database Error: " + ex.getMessage(), false);
        } catch (Exception ex) {
            showFeedback("Error: " + ex.getMessage(), false);
        }
    }

    private void clearFormAfterSuccess() {
        studentIdField.setText("");
        attendanceIdField.setText("");
        reasonArea.setText("");
        submissionDateField.setText(LocalDate.now().toString());
        startDateField.setText(LocalDate.now().toString());
        endDateField.setText(LocalDate.now().plusDays(2).toString());
    }

    private void clearForm() {
        clearFormAfterSuccess();
        hideFeedback();
    }

    private void showFeedback(String message, boolean isSuccess) {
        feedbackLabel.setText(message);
        if (isSuccess) {
            feedbackPanel.setBackground(SUCCESS_BG);
            feedbackLabel.setForeground(SUCCESS_TEXT);
            feedbackPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(0xBB, 0xF7, 0xD0), 1, true),
                    new EmptyBorder(10, 15, 10, 15)
            ));
        } else {
            feedbackPanel.setBackground(ERROR_BG);
            feedbackLabel.setForeground(ERROR_TEXT);
            feedbackPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(0xFE, 0xCA, 0xCA), 1, true),
                    new EmptyBorder(10, 15, 10, 15)
            ));
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
                new EmptyBorder(4, 10, 4, 10)
        ));
        return tf;
    }
}
