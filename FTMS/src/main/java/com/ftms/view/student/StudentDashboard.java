package com.ftms.view.student;

import com.ftms.dao.MarksDAO;
import com.ftms.view.LoginFrame;
import com.ftms.model.GPA;
import com.ftms.model.Marks;
import com.ftms.model.Undergraduate;
import com.ftms.util.SessionManager;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {
    private final Undergraduate currentStudent;
    private final DashboardHomePanel homePanel;
    private final ProfilePanel profilePanel;
    private final CoursesPanel coursesPanel;
    private final AttendancePanel attendancePanel;
    private final EligibilityPanel eligibilityPanel;
    private final GradesAndGpaPanel gradesAndGpaPanel;

    public StudentDashboard() {
        int id = SessionManager.getInstance().getLoggedInUserId();
        if (id < 0) throw new IllegalStateException("Student login is required.");
        MarksDAO dao = new MarksDAO();
        currentStudent = dao.getStudentDetails(id);
        if (currentStudent == null || currentStudent.getStudentId() != id)
            throw new IllegalStateException("Logged-in student not found in database.");

        setTitle("FTMS - Student Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        homePanel = new DashboardHomePanel(currentStudent);
        profilePanel = new ProfilePanel(currentStudent);
        coursesPanel = new CoursesPanel();
        attendancePanel = new AttendancePanel();
        eligibilityPanel = new EligibilityPanel();
        gradesAndGpaPanel = new GradesAndGpaPanel();

        CardLayout layout = new CardLayout();
        JPanel content = new JPanel(layout);
        content.add(homePanel, "Dashboard");
        content.add(profilePanel, "Profile");
        content.add(coursesPanel, "Courses");
        content.add(attendancePanel, "Attendance");
        content.add(eligibilityPanel, "Eligibility");
        content.add(gradesAndGpaPanel, "Grades");
        add(new SidebarPanel(layout, content, this, profilePanel, this::refreshData), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        refreshData();
    }

    private void refreshData() {
        int id = currentStudent.getStudentId();
        MarksDAO dao = new MarksDAO();
        List<Marks> marks = dao.getMarksByStudentId(id);
        coursesPanel.loadCourses();
        attendancePanel.refreshAttendance();
        homePanel.refreshData();

        gradesAndGpaPanel.getLblStudentSummary().setText(
                "Student ID: " + currentStudent.getTgNum() +
                        " | Program: " + currentStudent.getDepartment() +
                        " | Batch: " + currentStudent.getBatch() +
                        " | Semester: " + currentStudent.getSemester() +
                        " | Academic Year: " + currentStudent.getYear());
        gradesAndGpaPanel.getMarksTableModel().setRowCount(0);
        eligibilityPanel.getEligibilityTableModel().setRowCount(0);
        double total = 0;
        boolean eligible = !marks.isEmpty();
        for (Marks m : marks) {
            total += m.getTotalMarks();
            boolean courseEligible = m.viewMyEligibility();
            eligible &= courseEligible;
            gradesAndGpaPanel.getMarksTableModel().addRow(new Object[]{
                    m.getCourseCode(), m.getCourseName(), m.getCaMarks(),
                    m.getFinalExamMarks(), m.getTotalMarks(), m.getGrade()});
            eligibilityPanel.getEligibilityTableModel().addRow(new Object[]{
                    m.getCourseCode(), m.getCourseName(), m.getCaMarks(),
                    "40.0", courseEligible ? "ELIGIBLE" : "NOT ELIGIBLE"});
        }
        gradesAndGpaPanel.getLblTotalCourses().setText("Total Courses: " + marks.size());
        gradesAndGpaPanel.getLblTotalMarks().setText(
                "Total Marks: " + String.format("%.2f", total) + " / " + (marks.size() * 100));
        gradesAndGpaPanel.getLblPercentage().setText(marks.isEmpty() ?
                "Percentage: --" : String.format("Percentage: %.2f%%", total / marks.size()));
        GPA gpa = dao.getGPAByStudentId(id);
        gradesAndGpaPanel.getLblSgpa().setText(gpa == null ? "--" : String.format("%.2f", gpa.getSgpa()));
        gradesAndGpaPanel.getLblCgpa().setText(gpa == null ? "--" : String.format("%.2f", gpa.getCgpa()));
        gradesAndGpaPanel.getLblCaStatus().setText(marks.isEmpty() ? "No Results" :
                eligible ? "Eligible" : "Not Eligible");
        gradesAndGpaPanel.getLblCaStatus().setForeground(
                marks.isEmpty() ? Color.GRAY : eligible ? new Color(0, 150, 0) : Color.RED);
        profilePanel.updateProfileData();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
