package com.ftms.view.student;

import com.ftms.dao.MedicalDAO;
import com.ftms.model.Medical;
import com.ftms.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/** Student-only medical submission; approval is controlled by staff. */
public class MedicalSubmissionPanel extends JPanel {
    private static final Color NAVY = new Color(9, 43, 72);
    private static final Color TEAL = new Color(0, 136, 158);
    private final MedicalDAO dao = new MedicalDAO();
    private final JTextField studentField = new JTextField();
    private final JTextField attendanceField = new JTextField();
    private final JTextField submissionField = new JTextField(LocalDate.now().toString());
    private final JTextField startField = new JTextField(LocalDate.now().toString());
    private final JTextField endField = new JTextField(LocalDate.now().toString());
    private final JTextField reasonField = new JTextField();
    private final JTextField fileField = new JTextField();
    private final JLabel countLabel = new JLabel("0 records");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Medical ID", "Student ID", "Attendance ID", "Submitted", "Start Date", "End Date", "Reason", "Document", "Approval", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private File chosenFile;
    private List<Medical> records;

    public MedicalSubmissionPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(new Color(244, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));
        JLabel title = new JLabel("Medical Records Management");
        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(NAVY);
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        add(content, BorderLayout.CENTER);
        JPanel form = whitePanel();
        form.setLayout(new BorderLayout(0, 15));
        JLabel formTitle = new JLabel("Submit Student Medical Record & Certificate");
        formTitle.setFont(new Font("Arial", Font.BOLD, 17));
        formTitle.setForeground(NAVY);
        form.add(formTitle, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(3, 4, 16, 13));
        fields.setOpaque(false);
        studentField.setEditable(false);
        submissionField.setEditable(false);
        fileField.setEditable(false);
        fields.add(field("Student ID (Logged in)", studentField));
        fields.add(field("Attendance ID (Optional)", attendanceField));
        fields.add(field("Submission Date", submissionField));
        JTextField approvalField = new JTextField("PENDING");
        approvalField.setEditable(false);
        fields.add(field("Approval Status", approvalField));
        fields.add(field("Start Date (YYYY-MM-DD) *", startField));
        fields.add(field("End Date (YYYY-MM-DD) *", endField));
        fields.add(field("Reason *", reasonField));
        fields.add(field("Medical Document (PDF, PNG, JPG)", fileField));
        JButton browse = button("Browse Document...", TEAL);
        browse.addActionListener(e -> browseFile());
        JButton view = button("Open / View", new Color(38, 110, 154));
        view.addActionListener(e -> openDocument());
        JButton save = button("Submit Medical", TEAL);
        save.addActionListener(e -> submitMedical());
        JButton clear = button("Clear", new Color(104, 118, 134));
        clear.addActionListener(e -> clearForm());
        fields.add(browse);
        fields.add(view);
        fields.add(save);
        fields.add(clear);
        form.add(fields, BorderLayout.CENTER);
        content.add(form, BorderLayout.NORTH);

        JPanel history = whitePanel();
        history.setLayout(new BorderLayout(0, 12));
        JPanel historyHead = new JPanel(new BorderLayout());
        historyHead.setOpaque(false);
        JLabel historyTitle = new JLabel("My Medical Records (select a row to view its document)");
        historyTitle.setForeground(NAVY);
        historyTitle.setFont(new Font("Arial", Font.BOLD, 16));
        historyHead.add(historyTitle, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        JButton refresh = button("Refresh", new Color(38, 110, 154));
        refresh.addActionListener(e -> refreshRecords());
        JButton delete = button("Delete Selected", new Color(196, 52, 55));
        delete.addActionListener(e -> deleteSelectedMedical());
        right.add(countLabel);
        right.add(delete);
        right.add(refresh);
        historyHead.add(right, BorderLayout.EAST);
        history.add(historyHead, BorderLayout.NORTH);
        table.setRowHeight(29);
        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int idx = table.convertRowIndexToModel(row);
                    if (records != null && idx < records.size()) {
                        String path = records.get(idx).getMedicalImg();
                        fileField.setText(path == null ? "" : path);
                        chosenFile = null;
                    }
                }
            }
        });
        history.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(history, BorderLayout.CENTER);
        refreshRecords();
    }

    private JPanel whitePanel() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 226, 237)),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)));
        return p;
    }
    private JPanel field(String label, JTextField input) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(NAVY);
        p.add(l, BorderLayout.NORTH);
        input.setPreferredSize(new Dimension(100, 35));
        p.add(input, BorderLayout.CENTER);
        return p;
    }
    private JButton button(String label, Color bg) {
        JButton b = new JButton(label);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        return b;
    }
    private int studentId() { return SessionManager.getInstance().getLoggedInUserId(); }

    private void browseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Medical documents (PDF, PNG, JPG)", "pdf", "png", "jpg", "jpeg"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            chosenFile = chooser.getSelectedFile();
            fileField.setText(chosenFile.getAbsolutePath());
        }
    }
    private void openDocument() {
        String path = fileField.getText().trim();
        if (path.isEmpty()) { message("Select a document first."); return; }
        try {
            File file = new File(path);
            if (!file.isFile()) throw new IOException("File not found: " + path);
            if (!Desktop.isDesktopSupported()) throw new IOException("Desktop open not supported");
            Desktop.getDesktop().open(file);
        } catch (IOException e) { message(e.getMessage()); }
    }
    private void submitMedical() {
        int id = studentId();
        if (id < 0) { message("Please log in as a student."); return; }
        try {
            LocalDate start = LocalDate.parse(startField.getText().trim());
            LocalDate end = LocalDate.parse(endField.getText().trim());
            if (end.isBefore(start)) { message("End date must not be before start date."); return; }
            String reason = reasonField.getText().trim();
            if (reason.isEmpty()) { message("Reason is required."); return; }
            Integer attendanceId = attendanceField.getText().trim().isEmpty() ? null : Integer.valueOf(attendanceField.getText().trim());
            if (chosenFile == null || !chosenFile.isFile()) { message("Please choose a PDF, PNG, or JPG medical certificate."); return; }
            String name = chosenFile.getName().toLowerCase(java.util.Locale.ROOT);
            if (!(name.endsWith(".pdf") || name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"))) {
                message("Only PDF, PNG and JPG files are allowed."); return;
            }
            // DB medicalimg is VARCHAR(255): store a path, not binary content.
            Path directory = Path.of("medical_uploads").toAbsolutePath();
            Files.createDirectories(directory);
            String extension = name.substring(name.lastIndexOf('.'));
            Path saved = directory.resolve(java.util.UUID.randomUUID() + extension);
            Files.copy(chosenFile.toPath(), saved, StandardCopyOption.REPLACE_EXISTING);
            Medical m = new Medical(0, id, attendanceId, LocalDate.now(), start, end,
                    reason, Medical.APPROVAL_PENDING, "RECORDED", saved.toString());
            if (dao.addMedical(m)) {
                JOptionPane.showMessageDialog(this, "Medical submitted successfully. Approval: PENDING");
                clearForm();
                refreshRecords();
            } else {
                Files.deleteIfExists(saved);
                message("Could not save the medical record. Check the database and Attendance ID.");
            }
        } catch (DateTimeParseException ex) { message("Use date format YYYY-MM-DD."); }
        catch (NumberFormatException ex) { message("Attendance ID must be a number."); }
        catch (IOException ex) { message("Unable to save document: " + ex.getMessage()); }
    }
    private void deleteSelectedMedical() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            message("Please select a medical record to delete.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        if (records == null || modelRow >= records.size()) {
            message("Please refresh and select the record again.");
            return;
        }
        Medical selected = records.get(modelRow);
        int loggedInStudent = studentId();
        if (loggedInStudent < 0 || selected.getStudentId() != loggedInStudent) {
            message("You can only delete your own medical records.");
            return;
        }
        if (!Medical.APPROVAL_PENDING.equalsIgnoreCase(selected.getApprovalStatus())) {
            message("Only PENDING medical records can be deleted.");
            return;
        }
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Delete medical record #" + selected.getMedicalId() + "? This cannot be undone.",
                "Confirm Medical Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirmation != JOptionPane.YES_OPTION) return;

        if (dao.deletePendingMedicalForStudent(selected.getMedicalId(), loggedInStudent)) {
            clearForm();
            refreshRecords();
            message("Medical record deleted successfully.");
        } else {
            refreshRecords();
            message("Unable to delete this record. It may have been reviewed or removed already.");
        }
    }

    private void clearForm() {
        attendanceField.setText("");
        submissionField.setText(LocalDate.now().toString());
        startField.setText(LocalDate.now().toString());
        endField.setText(LocalDate.now().toString());
        reasonField.setText("");
        fileField.setText("");
        chosenFile = null;
        table.clearSelection();
    }
    public void refreshRecords() {
        int id = studentId();
        studentField.setText(id < 0 ? "Not logged in" : Integer.toString(id));
        model.setRowCount(0);
        records = id < 0 ? java.util.Collections.emptyList() : dao.getMedicalsByStudentId(id);
        for (Medical m : records) model.addRow(new Object[]{
                m.getMedicalId(), m.getStudentId(), m.getAttendanceId(), m.getSubmissionDate(),
                m.getStartDate(), m.getEndDate(), m.getReason(), m.getMedicalImg(),
                m.getApprovalStatus(), m.getStatus()});
        countLabel.setText(records.size() + " records");
    }
    private void message(String msg) { JOptionPane.showMessageDialog(this, msg); }
}
