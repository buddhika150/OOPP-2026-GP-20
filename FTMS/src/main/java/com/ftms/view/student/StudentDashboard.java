package com.ftms.view.student;

import com.ftms.dao.MarksDAO;
import com.ftms.model.GPA;
import com.ftms.model.Marks;
import com.ftms.model.Undergraduate;
import com.ftms.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {
    private Undergraduate currentStudent;
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    private ProfilePanel profilePanel;
    private CoursesPanel coursesPanel;
    private AttendancePanel attendancePanel;
    private EligibilityPanel eligibilityPanel;
    private GradesAndGpaPanel gradesAndGpaPanel;

    public StudentDashboard() {
        int loggedInUserId = SessionManager.getInstance().getLoggedInUserId();
        if (loggedInUserId == -1) {
            loggedInUserId = 11; // Default fallback for testing
        }

        MarksDAO dao = new MarksDAO();
        this.currentStudent = dao.getStudentDetails(loggedInUserId);

        if (currentStudent == null) {
            this.currentStudent = dao.getStudentDetails(11);
        }

        setTitle("FTMS - Student Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize Panels
        DashboardHomePanel dashboardHomePanel = new DashboardHomePanel(currentStudent);
        profilePanel = new ProfilePanel(currentStudent);
        coursesPanel = new CoursesPanel();
        attendancePanel = new AttendancePanel();
        eligibilityPanel = new EligibilityPanel();
        gradesAndGpaPanel = new GradesAndGpaPanel();

        // Main Content Area with CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        mainContentPanel.add(dashboardHomePanel, "Dashboard");
        mainContentPanel.add(profilePanel, "Profile");
        mainContentPanel.add(coursesPanel, "Courses");
        mainContentPanel.add(attendancePanel, "Attendance");
        mainContentPanel.add(eligibilityPanel, "Eligibility");
        mainContentPanel.add(gradesAndGpaPanel, "Grades");

        // Left Sidebar Panel
        SidebarPanel sidebarPanel = new SidebarPanel(cardLayout, mainContentPanel, this, profilePanel, this::loadStudentDataIntoUI);
        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        loadStudentDataIntoUI();
    }

    private void loadStudentDataIntoUI() {
        if (currentStudent == null) return;

        gradesAndGpaPanel.getLblStudentSummary().setText("Student ID: " + currentStudent.getTgNum() +
                " | Program: " + currentStudent.getDepartment() +
                " | Batch: " + currentStudent.getBatch() +
                " | Semester: " + currentStudent.getSemester() +
                " | Academic Year: " + currentStudent.getYear());

        MarksDAO dao = new MarksDAO();
        List<Marks> marksList = dao.getMarksByStudentId(currentStudent.getStudentId());

        gradesAndGpaPanel.getMarksTableModel().setRowCount(0);
        if (coursesPanel.getCoursesTableModel() != null) coursesPanel.getCoursesTableModel().setRowCount(0);
        if (attendancePanel.getAttendanceTableModel() != null) attendancePanel.getAttendanceTableModel().setRowCount(0);
        if (eligibilityPanel.getEligibilityTableModel() != null) eligibilityPanel.getEligibilityTableModel().setRowCount(0);

        double grandTotal = 0;
        int courseCount = marksList.size();
        boolean allEligible = true;

        for (Marks m : marksList) {
            grandTotal += m.getTotalMarks();
            boolean isEligible = m.viewMyEligibility();
            if (!isEligible) allEligible = false;

            // 1. Grades Table Row
            gradesAndGpaPanel.getMarksTableModel().addRow(new Object[]{
                    m.getCourseCode(),
                    m.getCourseName(),
                    m.getCaMarks(),
                    m.getFinalExamMarks(),
                    m.getTotalMarks(),
                    m.getGrade()
            });

            // 2. Courses Table Row
            if (coursesPanel.getCoursesTableModel() != null) {
                coursesPanel.getCoursesTableModel().addRow(new Object[]{m.getCourseCode(), m.getCourseName()});
            }

            // 3. Attendance Table Row
            if (attendancePanel.getAttendanceTableModel() != null) {
                attendancePanel.getAttendanceTableModel().addRow(new Object[]{m.getCourseCode(), m.getCourseName(), 30, 28, "93.33%"});
            }

            // 4. Eligibility Table Row
            if (eligibilityPanel.getEligibilityTableModel() != null) {
                eligibilityPanel.getEligibilityTableModel().addRow(new Object[]{
                        m.getCourseCode(),
                        m.getCourseName(),
                        m.getCaMarks(),
                        "40.0",
                        isEligible ? "ELIGIBLE" : "NOT ELIGIBLE"
                });
            }
        }

        gradesAndGpaPanel.getLblTotalCourses().setText("Total Courses: " + courseCount);
        gradesAndGpaPanel.getLblTotalMarks().setText("Total Marks: " + (int)grandTotal + "/" + (courseCount > 0 ? courseCount * 100 : 100));
        if (courseCount > 0) {
            gradesAndGpaPanel.getLblPercentage().setText(String.format("Percentage: %.2f%%", grandTotal / courseCount));
        }

        GPA gpa = dao.getGPAByStudentId(currentStudent.getStudentId());
        if (gpa != null) {
            gradesAndGpaPanel.getLblSgpa().setText(String.format("%.2f", gpa.getSgpa()));
            gradesAndGpaPanel.getLblCgpa().setText(String.format("%.2f", gpa.getCgpa()));
        }

        if (allEligible) {
            gradesAndGpaPanel.getLblCaStatus().setText("Eligible");
            gradesAndGpaPanel.getLblCaStatus().setForeground(new Color(0, 150, 0));
        } else {
            gradesAndGpaPanel.getLblCaStatus().setText("Not Eligible");
            gradesAndGpaPanel.getLblCaStatus().setForeground(Color.RED);
        }

        profilePanel.updateProfileData();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboard().setVisible(true));
    }
}