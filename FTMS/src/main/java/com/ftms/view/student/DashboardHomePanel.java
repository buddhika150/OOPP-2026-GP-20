package com.ftms.view.student;

import com.ftms.dao.AttendanceDAO;
import com.ftms.dao.CourseDAO;
import com.ftms.dao.MarksDAO;
import com.ftms.model.AttendanceRecord;
import com.ftms.model.GPA;
import com.ftms.model.Marks;
import com.ftms.model.Undergraduate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.io.File;
import java.util.List;

public class DashboardHomePanel extends JPanel {
    private static final Color BACKGROUND = new Color(245, 247, 252);
    private static final Color NAVY = new Color(28, 45, 76);
    private static final Color MUTED = new Color(99, 113, 135);
    private final Undergraduate student;
    private final JTextArea coursesText = new JTextArea();
    private final JTextArea eligibilityText = new JTextArea();
    private final JTextArea gradesText = new JTextArea();
    private final JLabel attendanceText = new JLabel("Attendance: --");
    private final JProgressBar attendanceBar = new JProgressBar(0, 100);

    public DashboardHomePanel(Undergraduate student) {
        this.student = student;
        setLayout(new BorderLayout(0, 22));
        setBorder(new EmptyBorder(24, 28, 28, 28));
        setBackground(BACKGROUND);

        JPanel heading = new JPanel(new BorderLayout(0, 15));
        heading.setOpaque(false);
        JLabel title = new JLabel("Student Dashboard");
        title.setForeground(NAVY);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        heading.add(title, BorderLayout.NORTH);

        RoundedPanel welcomeCard = new RoundedPanel(new Color(230, 239, 255), 22);
        welcomeCard.setLayout(new BorderLayout(16, 0));
        welcomeCard.setBorder(new EmptyBorder(20, 26, 20, 22));
        welcomeCard.setPreferredSize(new Dimension(0, 155));
        JPanel welcomeText = new JPanel();
        welcomeText.setOpaque(false);
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));
        welcomeText.add(Box.createVerticalGlue());
        JLabel eyebrow = new JLabel("WELCOME TO FTMS");
        eyebrow.setFont(new Font("SansSerif", Font.BOLD, 12));
        eyebrow.setForeground(new Color(69, 101, 156));
        welcomeText.add(eyebrow);
        welcomeText.add(Box.createVerticalStrut(9));
        JLabel welcome = new JLabel("Welcome Back, " + student.getFname() + " " + student.getLname() + "!");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        welcome.setForeground(NAVY);
        welcomeText.add(welcome);
        welcomeText.add(Box.createVerticalStrut(8));
        JLabel subtitle = new JLabel("Here is your academic overview.");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcomeText.add(subtitle);
        welcomeText.add(Box.createVerticalGlue());
        welcomeCard.add(welcomeText, BorderLayout.CENTER);

        JLabel illustration = new JLabel();
        illustration.setHorizontalAlignment(SwingConstants.CENTER);
        illustration.setPreferredSize(new Dimension(155, 125));
        ImageIcon icon = loadGraduatedIcon();
        if (icon != null) illustration.setIcon(icon);
        welcomeCard.add(illustration, BorderLayout.EAST);
        heading.add(welcomeCard, BorderLayout.CENTER);
        add(heading, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 18, 18));
        grid.setOpaque(false);
        grid.add(textCard("My Courses", coursesText));

        RoundedPanel att = new RoundedPanel(Color.WHITE, 18);
        att.setLayout(new BorderLayout(10, 14));
        att.setBorder(new EmptyBorder(20, 22, 22, 22));
        JLabel attTitle = new JLabel("Attendance");
        attTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        attTitle.setForeground(NAVY);
        att.add(attTitle, BorderLayout.NORTH);
        JPanel attBody = new JPanel(new GridLayout(2, 1, 0, 12));
        attBody.setOpaque(false);
        attendanceText.setForeground(MUTED);
        attendanceText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        attBody.add(attendanceText);
        attendanceBar.setStringPainted(true);
        attendanceBar.setForeground(new Color(38, 157, 144));
        attendanceBar.setBackground(new Color(231, 238, 244));
        attendanceBar.setBorderPainted(false);
        attendanceBar.setPreferredSize(new Dimension(0, 20));
        attBody.add(attendanceBar);
        att.add(attBody, BorderLayout.CENTER);
        grid.add(att);
        grid.add(textCard("CA Eligibility", eligibilityText));
        grid.add(textCard("Grades & GPA", gradesText));
        add(grid, BorderLayout.CENTER);
    }

    private ImageIcon loadGraduatedIcon() {
        try {
            URL resource = getClass().getResource("/images/graduated.png");
            ImageIcon source = resource != null ? new ImageIcon(resource) : null;
            if (source == null) {
                File local = new File("src/main/resources/images/graduated.png");
                if (local.isFile()) source = new ImageIcon(local.getAbsolutePath());
            }
            if (source == null || source.getIconWidth() <= 0) return null;
            Image scaled = source.getImage().getScaledInstance(145, 125, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            return null;
        }
    }

    private JPanel textCard(String title, JTextArea area) {
        RoundedPanel card = new RoundedPanel(Color.WHITE, 18);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(20, 22, 20, 22));
        JLabel heading = new JLabel(title);
        heading.setForeground(NAVY);
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        card.add(heading, BorderLayout.NORTH);
        area.setEditable(false);
        area.setOpaque(false);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setForeground(MUTED);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        card.add(new JScrollPane(area) {{
            setBorder(null);
            setOpaque(false);
            getViewport().setOpaque(false);
        }}, BorderLayout.CENTER);
        return card;
    }

    private static class RoundedPanel extends JPanel {
        private final Color fill;
        private final int arc;
        RoundedPanel(Color fill, int arc) {
            this.fill = fill;
            this.arc = arc;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill);
            g.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
            g.dispose();
            super.paintComponent(graphics);
        }
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
                    eligibilityText.setText(
                            marks.isEmpty() ? "No CA marks available" :
                                    eligible
                                            ? "CA Status: ELIGIBLE\nYou are eligible for the examination."
                                            : "CA Status: NOT ELIGIBLE\nYou are not eligible for the examination."
                    );
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
