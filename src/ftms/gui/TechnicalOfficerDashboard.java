package ftms.gui;

import ftms.model.TechnicalOfficer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Java Swing Dashboard for Technical Officer.
 * Complies with SRS 9.4:
 * - Profile
 * - Attendance
 * - Medical Records
 * - Notices
 * - Timetable
 *
 * Assigned to Member 3.
 */
public class TechnicalOfficerDashboard extends JFrame {
    private final TechnicalOfficer technicalOfficer;

    private JTabbedPane tabbedPane;

    public TechnicalOfficerDashboard(TechnicalOfficer technicalOfficer) {
        this.technicalOfficer = technicalOfficer;
        setTitle("Faculty of Technology Management System - Technical Officer Portal");
        setSize(1150, 750);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // Top Banner
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(24, 43, 73)); // Dark Navy
        pnlHeader.setBorder(new EmptyBorder(12, 20, 12, 20));

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1));
        pnlTitle.setOpaque(false);

        JLabel lblSystem = new JLabel("FACULTY OF TECHNOLOGY MANAGEMENT SYSTEM");
        lblSystem.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblSystem.setForeground(Color.WHITE);

        String officerName = technicalOfficer != null ? technicalOfficer.getFullName() : "Technical Officer";
        String dept = technicalOfficer != null && technicalOfficer.getDepartment() != null ? technicalOfficer.getDepartment() : "General";
        JLabel lblUser = new JLabel("Logged in as: " + officerName + " (" + dept + " Dept) | Role: Technical Officer");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUser.setForeground(new Color(200, 220, 240));

        pnlTitle.add(lblSystem);
        pnlTitle.add(lblUser);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        // Header Right: Logout button
        JPanel pnlHeaderRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pnlHeaderRight.setOpaque(false);

        JButton btnLogout = new JButton("Close / Exit");
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> dispose());
        pnlHeaderRight.add(btnLogout);

        pnlHeader.add(pnlHeaderRight, BorderLayout.EAST);
        contentPane.add(pnlHeader, BorderLayout.NORTH);

        // Center: Tabbed Pane for Member 3 modules
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Tab 1: Attendance Maintenance
        AttendanceManagementPanel pnlAttendance = new AttendanceManagementPanel(technicalOfficer);
        tabbedPane.addTab("Attendance Management", pnlAttendance);

        // Tab 2: Attendance Summary & Eligibility
        AttendanceSummaryPanel pnlSummary = new AttendanceSummaryPanel(technicalOfficer);
        tabbedPane.addTab("Attendance Summary & Eligibility", pnlSummary);

        // Tab 3: Medical Records & Approval
        MedicalManagementPanel pnlMedical = new MedicalManagementPanel(technicalOfficer);
        tabbedPane.addTab("Medical Records", pnlMedical);

        // Tab 4: Timetable View
        TimetableViewPanel pnlTimetable = new TimetableViewPanel(technicalOfficer);
        tabbedPane.addTab("Timetable", pnlTimetable);

        // Tab 5: Notices View
        NoticeViewPanel pnlNotice = new NoticeViewPanel();
        tabbedPane.addTab("Notices", pnlNotice);

        // Tab 6: Profile
        ProfilePanel pnlProfile = new ProfilePanel(technicalOfficer);
        tabbedPane.addTab("Profile", pnlProfile);

        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // Status bar at bottom
        JPanel pnlStatus = new JPanel(new BorderLayout());
        pnlStatus.setBorder(new EmptyBorder(5, 15, 5, 15));
        pnlStatus.setBackground(new Color(245, 245, 245));
        JLabel lblStatus = new JLabel("Member 3 Module: Technical Officer, Attendance & Medical Management System");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(Color.GRAY);
        pnlStatus.add(lblStatus, BorderLayout.WEST);
        contentPane.add(pnlStatus, BorderLayout.SOUTH);
    }

    /**
     * Standalone main method for testing and demonstration.
     */
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            // Attempt to load seed Technical Officer (Chamod Perera, User_ID 7 in db.sql)
            TechnicalOfficer to = null;
            try {
                to = TechnicalOfficer.getById(7);
            } catch (Exception e) {
                System.out.println("Could not connect to MySQL yet: " + e.getMessage());
            }

            if (to == null) {
                // Default instance for testing without active DB connection
                to = new TechnicalOfficer(7, "to01", "to123", "to01@ftms.com",
                        null, "0720000001", "Chamod", "Perera", "ICT");
            }

            to.displayDashboard();
        });
    }
}
