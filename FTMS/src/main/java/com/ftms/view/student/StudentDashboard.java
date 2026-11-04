package com.ftms.view.student;

import com.ftms.dao.MarksDAO;
import com.ftms.dao.AttendanceDAO;
import com.ftms.view.LoginFrame;
import com.ftms.model.GPA;
import com.ftms.model.Marks;
import com.ftms.model.AttendanceRecord;
import com.ftms.model.Undergraduate;
import com.ftms.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

public class StudentDashboard extends JFrame {
    private final Undergraduate currentStudent;
    private final DashboardHomePanel homePanel;
    private final ProfilePanel profilePanel;
    private final CoursesPanel coursesPanel;
    private final AttendancePanel attendancePanel;
    private final EligibilityPanel eligibilityPanel;
    private final GradesAndGpaPanel gradesAndGpaPanel;
    private final MedicalSubmissionPanel medicalSubmissionPanel;
    private final StudentTimetablePanel timetablePanel;
    private final StudentNoticesPanel noticesPanel;

    public StudentDashboard() {
        int id = SessionManager.getInstance().getLoggedInUserId();
        if (id < 0) throw new IllegalStateException("Student login is required.");

        MarksDAO dao = new MarksDAO();
        currentStudent = dao.getStudentDetails(id);
        if (currentStudent == null || currentStudent.getStudentId() != id) {
            throw new IllegalStateException("Logged-in student not found in database.");
        }

        setTitle("FTMS - Student Dashboard");
        setSize(1200, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        homePanel = new DashboardHomePanel(currentStudent);
        profilePanel = new ProfilePanel(currentStudent);
        coursesPanel = new CoursesPanel();
        attendancePanel = new AttendancePanel();
        eligibilityPanel = new EligibilityPanel();
        gradesAndGpaPanel = new GradesAndGpaPanel();
        medicalSubmissionPanel = new MedicalSubmissionPanel();
        timetablePanel = new StudentTimetablePanel(currentStudent.getDepartment());
        noticesPanel = new StudentNoticesPanel();

        CardLayout layout = new CardLayout();
        JPanel content = new JPanel(layout);
        content.add(homePanel, "Dashboard");
        content.add(profilePanel, "Profile");
        content.add(coursesPanel, "Courses");
        content.add(attendancePanel, "Attendance");
        content.add(eligibilityPanel, "Eligibility");
        content.add(gradesAndGpaPanel, "Grades");
        content.add(medicalSubmissionPanel, "Medical");
        content.add(timetablePanel, "Timetable");
        content.add(noticesPanel, "Notices");

        add(new SidebarPanel(layout, content, this, profilePanel, this::refreshData), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        refreshData();
    }

    private String eligibilityStatus(Double ca, Double theory, Double practical) {
        if ((ca != null && ca < 16.0)
                || (theory != null && theory < 80.0)
                || (practical != null && practical < 80.0)) {
            return "NOT ELIGIBLE";
        }
        if (ca == null || theory == null || practical == null) return "PENDING";
        return "ELIGIBLE";
    }

    private void refreshData() {
        int id = currentStudent.getStudentId();
        MarksDAO dao = new MarksDAO();
        List<Marks> marks = dao.getMarksByStudentId(id);

        Map<String, AttendanceRecord> attendanceByCourse = new HashMap<>();
        for (AttendanceRecord record : new AttendanceDAO().getMedicalAdjustedAttendanceForStudent(id)) {
            if (record.getCourseCode() != null) {
                attendanceByCourse.put(record.getCourseCode().trim().toUpperCase(Locale.ROOT), record);
            }
        }

        coursesPanel.loadCourses();
        attendancePanel.refreshAttendance();
        homePanel.refreshData();

        gradesAndGpaPanel.getLblStudentSummary().setText(
                "Student ID: " + currentStudent.getTgNum()
                        + " | Program: " + currentStudent.getDepartment()
                        + " | Batch: " + currentStudent.getBatch()
                        + " | Semester: " + currentStudent.getSemester()
                        + " | Academic Year: " + currentStudent.getYear());

        gradesAndGpaPanel.getMarksTableModel().setRowCount(0);
        eligibilityPanel.getEligibilityTableModel().setRowCount(0);

        double total = 0;
        int eligibleCourses = 0;
        int pendingCourses = 0;

        for (Marks m : marks) {
            total += m.getTotalMarks();
            AttendanceRecord attendance = m.getCourseCode() == null ? null
                    : attendanceByCourse.get(m.getCourseCode().trim().toUpperCase(Locale.ROOT));

            Double theory = attendance != null && attendance.getTheoryTotal() > 0
                    ? attendance.getTheoryPercentage() : null;
            Double practical = attendance != null && attendance.getPracticalTotal() > 0
                    ? attendance.getPracticalPercentage() : null;

            // The existing Marks model exposes primitive doubles. A missing CA mark
            // may be converted to 0 by the DAO; update DAO/model if NULL must mean PENDING.
            Double ca = m.getCaMarks();
            String status = eligibilityStatus(ca, theory, practical);
            if ("ELIGIBLE".equals(status)) eligibleCourses++;
            if ("PENDING".equals(status)) pendingCourses++;

            gradesAndGpaPanel.getMarksTableModel().addRow(new Object[]{
                    m.getCourseCode(), m.getCourseName(), m.getCaMarks(),
                    m.getFinalExamMarks(), m.getTotalMarks(), m.getGrade(), status
            });

            eligibilityPanel.getEligibilityTableModel().addRow(new Object[]{
                    m.getCourseCode(), m.getCourseName(), m.getCaMarks(),
                    theory == null ? "--" : String.format(Locale.US, "%.2f%%", theory),
                    practical == null ? "--" : String.format(Locale.US, "%.2f%%", practical),
                    "CA >= 16/40; Theory >= 80%; Practical >= 80%", status
            });
        }

        gradesAndGpaPanel.getLblTotalCourses().setText("Total Courses: " + marks.size());
        gradesAndGpaPanel.getLblTotalMarks().setText(
                "Total Marks: " + String.format(Locale.US, "%.2f", total)
                        + " / " + (marks.size() * 100));
        gradesAndGpaPanel.getLblPercentage().setText(marks.isEmpty() ? "Percentage: --"
                : String.format(Locale.US, "Percentage: %.2f%%", total / marks.size()));

        GPA gpa = dao.getGPAByStudentId(id);
        gradesAndGpaPanel.getLblSgpa().setText(gpa == null ? "--"
                : String.format(Locale.US, "%.2f", gpa.getSgpa()));
        gradesAndGpaPanel.getLblCgpa().setText(gpa == null ? "--"
                : String.format(Locale.US, "%.2f", gpa.getCgpa()));

        JLabel summary = gradesAndGpaPanel.getLblCaStatus();
        if (marks.isEmpty()) {
            summary.setText("No Results");
            summary.setForeground(Color.GRAY);
        } else {
            summary.setText("Eligible Courses: " + eligibleCourses + " / " + marks.size());
            summary.setForeground(pendingCourses > 0 ? new Color(146, 64, 14)
                    : new Color(0, 130, 130));
        }

        profilePanel.updateProfileData();
        medicalSubmissionPanel.refreshRecords();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
