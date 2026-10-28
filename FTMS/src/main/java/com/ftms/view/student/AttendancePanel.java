package com.ftms.view.student;

import com.ftms.model.AttendanceRecord;
import com.ftms.dao.AttendanceDAO;
import com.ftms.util.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AttendancePanel extends JPanel {
    private DefaultTableModel attendanceTableModel;
    private JPanel gridPanel;
    private JLabel lblOverview;
    private JLabel tagBadge;

    public AttendancePanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setBackground(new Color(240, 242, 245));

        // Title Section
        JLabel lblTitle = new JLabel("My Attendance Records");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(33, 43, 54));
        add(lblTitle, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setOpaque(false);

        // 1. Top Card - Overall Status
        JPanel topCard = createRoundedPanel(15);
        topCard.setLayout(new BorderLayout());
        topCard.setBackground(Color.WHITE);
        topCard.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        topCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        topCard.setPreferredSize(new Dimension(800, 100));

        JPanel leftStatus = new JPanel(new GridLayout(2, 1, 0, 5));
        leftStatus.setOpaque(false);
        JLabel lblStatusHeader = new JLabel("Overall Status");
        lblStatusHeader.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblStatusHeader.setForeground(new Color(100, 110, 120));

        lblOverview = new JLabel("Overall Attendance: 0%");
        lblOverview.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblOverview.setForeground(new Color(33, 43, 54));

        leftStatus.add(lblStatusHeader);
        leftStatus.add(lblOverview);

        JPanel rightStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        rightStatus.setOpaque(false);

        tagBadge = new JLabel("Attendance Summary");
        tagBadge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tagBadge.setForeground(new Color(30, 100, 60));
        tagBadge.setBackground(new Color(205, 240, 218));
        tagBadge.setOpaque(true);
        tagBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 220, 180), 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        rightStatus.add(tagBadge);

        topCard.add(leftStatus, BorderLayout.WEST);
        topCard.add(rightStatus, BorderLayout.EAST);

        contentContainer.add(topCard);
        contentContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Course Cards Grid Panel (Dynamic)
        gridPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        gridPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        contentContainer.add(scrollPane);

        add(contentContainer, BorderLayout.CENTER);

        String[] columns = {"Course Code", "Course Name", "Total Lectures", "Attended", "Percentage (%)"};
        attendanceTableModel = new DefaultTableModel(columns, 0);
        refreshAttendance();
    }

    public void refreshAttendance() {
        int id = SessionManager.getInstance().getLoggedInUserId();
        if (id < 0) {
            loadAttendanceData(null);
            tagBadge.setText("Not Logged In");
            return;
        }
        tagBadge.setText("Loading...");
        new SwingWorker<List<AttendanceRecord>, Void>() {
            @Override protected List<AttendanceRecord> doInBackground() {
                return new AttendanceDAO().getAttendanceRecordsForStudent(id);
            }
            @Override protected void done() {
                try {
                    loadAttendanceData(get());
                    tagBadge.setText("Attendance Summary");
                } catch (Exception ex) {
                    loadAttendanceData(null);
                    tagBadge.setText("Load Error");
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    public void loadAttendanceData(List<AttendanceRecord> attendanceList) {
        gridPanel.removeAll();
        attendanceTableModel.setRowCount(0);
        lblOverview.setText("Overall Attendance: --");

        if (attendanceList == null || attendanceList.isEmpty()) {
            JLabel emptyLabel = new JLabel("No attendance records found.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(100, 110, 125));
            gridPanel.add(emptyLabel);
        } else {
            int totalSessions = 0;
            int attendedSessions = 0;

            for (AttendanceRecord record : attendanceList) {
                String courseName = record.getCourseName() != null ? record.getCourseName() : "-";
                String code = record.getCourseCode() != null ? record.getCourseCode() : "-";
                String total = String.valueOf(record.getTotalSessions());
                String attended = String.valueOf(record.getAttendedSessions());
                String pct = String.format("%.2f%%", record.getPercentage());

                JPanel card = createCourseCard(courseName, code, total, attended, pct, (int) record.getPercentage());
                gridPanel.add(card);

                totalSessions += record.getTotalSessions();
                attendedSessions += record.getAttendedSessions();
                attendanceTableModel.addRow(new Object[]{code, courseName, record.getTotalSessions(), record.getAttendedSessions(), record.getPercentage()});
            }

            if (totalSessions > 0) {
                double overallAvg = 100.0 * attendedSessions / totalSessions;
                lblOverview.setText(String.format("Overall Attendance: %.2f%%", overallAvg));
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel createCourseCard(String courseName, String code, String total, String attended, String pct, int progressVal) {
        JPanel card = createRoundedPanel(15);
        card.setLayout(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel lblSubtitle = new JLabel("Course Name");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.GRAY);

        JLabel lblName = new JLabel(courseName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblName.setForeground(new Color(33, 43, 54));

        JLabel lblCode = new JLabel(code);
        lblCode.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCode.setForeground(new Color(100, 110, 120));

        header.add(lblSubtitle);
        header.add(Box.createRigidArea(new Dimension(0, 2)));
        header.add(lblName);
        header.add(Box.createRigidArea(new Dimension(0, 2)));
        header.add(lblCode);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);

        JPanel details = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        details.setOpaque(false);

        JPanel p1 = new JPanel(new GridLayout(2, 1));
        p1.setOpaque(false);
        JLabel l1 = new JLabel("Total Lectures:");
        l1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JLabel v1 = new JLabel(total);
        v1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        p1.add(l1);
        p1.add(v1);

        JPanel p2 = new JPanel(new GridLayout(2, 1));
        p2.setOpaque(false);
        JLabel l2 = new JLabel("Attended:");
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JLabel v2 = new JLabel(attended);
        v2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        p2.add(l2);
        p2.add(v2);

        details.add(p1);
        details.add(p2);

        JPanel pctPanel = new JPanel(new BorderLayout(0, 5));
        pctPanel.setOpaque(false);
        JLabel lblPct = new JLabel("Attendance: " + pct);
        lblPct.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPct.setForeground(new Color(33, 43, 54));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(progressVal);
        bar.setPreferredSize(new Dimension(110, 8));
        bar.setForeground(new Color(60, 105, 130));
        bar.setBackground(new Color(215, 225, 235));
        bar.setBorderPainted(false);

        pctPanel.add(lblPct, BorderLayout.NORTH);
        pctPanel.add(bar, BorderLayout.SOUTH);

        body.add(details, BorderLayout.WEST);
        body.add(pctPanel, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createRoundedPanel(int radius) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.setColor(new Color(220, 228, 238));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g2.dispose();
            }
        };
    }

    public DefaultTableModel getAttendanceTableModel() {
        return attendanceTableModel;
    }
}