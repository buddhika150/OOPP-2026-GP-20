package com.ftms.view.student;

import com.ftms.dao.AttendanceDAO;
import com.ftms.dao.CourseDAO;
import com.ftms.dao.MarksDAO;
import com.ftms.model.AttendanceRecord;
import com.ftms.model.GPA;
import com.ftms.model.Marks;
import com.ftms.model.Undergraduate;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardHomePanel extends JPanel {
    private final Undergraduate student;
    private final JTextArea coursesText = new JTextArea();
    private final JTextArea eligibilityText = new JTextArea();
    private final JTextArea gradesText = new JTextArea();
    private final JLabel attendanceText = new JLabel("Attendance: --");
    private final JProgressBar attendanceBar = new JProgressBar(0, 100);

    public DashboardHomePanel(Undergraduate student) {
        this.student = student;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 10));
        header.setOpaque(false);
        JLabel title = new JLabel("Student Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        header.add(title);
        JLabel welcome = new JLabel("Welcome Back, " + student.getFname() + " " + student.getLname() + "!");
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(welcome);
        add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 15, 15));
        grid.setOpaque(false);
        grid.add(textCard("My Courses", coursesText));
        JPanel att = new JPanel(new BorderLayout(10, 10));
        att.setBackground(Color.WHITE);
        att.setBorder(BorderFactory.createTitledBorder("Attendance"));
        attendanceBar.setStringPainted(true);
        attendanceBar.setForeground(new Color(0, 150, 150));
        att.add(attendanceText, BorderLayout.NORTH);
        att.add(attendanceBar, BorderLayout.CENTER);
        grid.add(att);
        grid.add(textCard("CA Eligibility", eligibilityText));
        grid.add(textCard("Grades & GPA", gradesText));
        add(grid, BorderLayout.CENTER);
    }

    private JPanel textCard(String title, JTextArea area) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createTitledBorder(title));
        area.setEditable(false);
        area.setOpaque(false);
        area.setFont(new Font("Arial", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        card.add(area, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        int id = student.getStudentId();
        new SwingWorker<Object[], Void>() {
            @Override protected Object[] doInBackground() throws Exception {
                List<String[]> courses = new CourseDAO().getCoursesByStudentId(id);
                List<AttendanceRecord> attendance = new AttendanceDAO().getAttendanceRecordsForStudent(id);
                MarksDAO dao = new MarksDAO();
                return new Object[]{courses, attendance, dao.getMarksByStudentId(id), dao.getGPAByStudentId(id)};
            }
            @SuppressWarnings("unchecked")
            @Override protected void done() {
                try {
                    Object[] result = get();
                    List<String[]> courses = (List<String[]>) result[0];
                    List<AttendanceRecord> attendance = (List<AttendanceRecord>) result[1];
                    List<Marks> marks = (List<Marks>) result[2];
                    GPA gpa = (GPA) result[3];
                    StringBuilder courseLines = new StringBuilder();
                    for (String[] c : courses) courseLines.append(c[0]).append(" - ").append(c[1]).append("\n");
                    coursesText.setText(courses.isEmpty() ? "No enrolled courses" : courseLines.toString());
                    int sessions = 0, present = 0;
                    for (AttendanceRecord r : attendance) {
                        sessions += r.getTotalSessions(); present += r.getAttendedSessions();
                    }
                    if (sessions == 0) {
                        attendanceText.setText("Attendance: No sessions recorded");
                        attendanceBar.setValue(0);
                        attendanceBar.setString("--");
                    } else {
                        double pct = 100.0 * present / sessions;
                        attendanceText.setText(String.format("Present: %.2f%% | Absent: %.2f%%", pct, 100 - pct));
                        attendanceBar.setValue((int) Math.round(pct));
                        attendanceBar.setString(String.format("%.2f%%", pct));
                    }
                    boolean eligible = !marks.isEmpty();
                    for (Marks m : marks) eligible &= m.viewMyEligibility();
                    eligibilityText.setText(marks.isEmpty() ? "No CA marks available" :
                            "CA status: " + (eligible ? "ELIGIBLE" : "NOT ELIGIBLE") +
                                    "\nBased on the existing Marks.viewMyEligibility() rule");
                    gradesText.setText(gpa == null ? "GPA: No records available" :
                            String.format("SGPA: %.2f\nCGPA: %.2f\nSemester: %d", gpa.getSgpa(), gpa.getCgpa(), gpa.getSemester()));
                } catch (Exception ex) {
                    coursesText.setText("Could not load database data");
                    eligibilityText.setText("Could not load database data");
                    gradesText.setText("Could not load database data");
                    attendanceText.setText("Could not load attendance");
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
}
