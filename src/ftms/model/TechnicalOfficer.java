package ftms.model;

import ftms.db.DatabaseConnection;
import ftms.gui.TechnicalOfficerDashboard;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TechnicalOfficer class extending User.
 * Assigned to Member 3.
 *
 * Demonstrates:
 * - Inheritance: Subclass of abstract class User.
 * - Polymorphism: Overrides displayDashboard() and getRoleName().
 * - Encapsulation: Private member variables with controlled access.
 *
 * Implements assigned methods:
 * - addAttendance()
 * - maintainAttendance()
 * - viewAttendanceSummary()
 * - addMedical()
 * - maintainMedical()
 * - viewTimetable()
 */
public class TechnicalOfficer extends User {
    private String department;

    public TechnicalOfficer() {
        super();
        setRole("TECHNICAL_OFFICER");
    }

    public TechnicalOfficer(int userId, String username, String password, String email,
                            String profilePic, String phone, String fname, String lname, String department) {
        super(userId, username, password, "TECHNICAL_OFFICER", email, profilePic, phone, fname, lname);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getRoleName() {
        return "Technical Officer";
    }

    /**
     * Polymorphic method to display the Technical Officer Java Swing GUI dashboard.
     */
    @Override
    public void displayDashboard() {
        java.awt.EventQueue.invokeLater(() -> {
            TechnicalOfficerDashboard dashboard = new TechnicalOfficerDashboard(this);
            dashboard.setVisible(true);
        });
    }

    /**
     * Loads a Technical Officer by user ID.
     */
    public static TechnicalOfficer getById(int userId) throws SQLException {
        String sql = "SELECT u.User_ID, u.Username, u.Password, u.Role, u.Email, u.Profile_pic, " +
                     "u.Phone, u.Fname, u.Lname, t.Department " +
                     "FROM User u " +
                     "JOIN Technical_Officer t ON u.User_ID = t.TO_ID " +
                     "WHERE u.User_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TechnicalOfficer to = new TechnicalOfficer();
                    to.setUserId(rs.getInt("User_ID"));
                    to.setUsername(rs.getString("Username"));
                    to.setPassword(rs.getString("Password"));
                    to.setRole(rs.getString("Role"));
                    to.setEmail(rs.getString("Email"));
                    to.setProfilePic(rs.getString("Profile_pic"));
                    to.setPhone(rs.getString("Phone"));
                    to.setFname(rs.getString("Fname"));
                    to.setLname(rs.getString("Lname"));
                    to.setDepartment(rs.getString("Department"));
                    return to;
                }
            }
        }
        return null;
    }

    // ==========================================
    // Assigned Member 3 Methods
    // ==========================================

    /**
     * addAttendance() - delegates to Attendance model.
     */
    public boolean addAttendance(int studentId, String courseCode, LocalDate sessionDate, String type) throws SQLException {
        Attendance att = new Attendance(studentId, courseCode, sessionDate, type);
        return att.addAttendance();
    }

    /**
     * maintainAttendance() - update or delete attendance records.
     */
    public boolean maintainAttendance(int attendanceId, int studentId, String courseCode, LocalDate sessionDate, String type) throws SQLException {
        Attendance att = new Attendance(attendanceId, studentId, courseCode, sessionDate, type);
        return att.updateAttendance();
    }

    /**
     * maintainAttendance() - overload to delete attendance record.
     */
    public boolean deleteAttendance(int attendanceId) throws SQLException {
        return Attendance.deleteAttendance(attendanceId);
    }

    /**
     * viewAttendanceSummary() - retrieves attendance summary.
     */
    public List<Attendance.AttendanceSummaryDTO> viewAttendanceSummary(String courseCode, int studentId) throws SQLException {
        return Attendance.viewAttendanceSummary(courseCode, studentId);
    }

    /**
     * addMedical() - adds medical certificate record.
     */
    public boolean addMedical(int studentId, Integer attendanceId, LocalDate submissionDate,
                              LocalDate startDate, LocalDate endDate, String reason, String approvalStatus, String status) throws SQLException {
        Medical med = new Medical(0, studentId, attendanceId, submissionDate, startDate, endDate, reason, approvalStatus, status);
        return med.addMedical();
    }

    /**
     * maintainMedical() - updates existing medical record.
     */
    public boolean maintainMedical(int medicalId, int studentId, Integer attendanceId, LocalDate submissionDate,
                                   LocalDate startDate, LocalDate endDate, String reason, String approvalStatus, String status) throws SQLException {
        Medical med = new Medical(medicalId, studentId, attendanceId, submissionDate, startDate, endDate, reason, approvalStatus, status);
        return med.updateMedical();
    }

    /**
     * deleteMedical() - deletes existing medical record.
     */
    public boolean deleteMedical(int medicalId) throws SQLException {
        Medical med = new Medical();
        med.setMedicalId(medicalId);
        return med.deleteMedical();
    }

    /**
     * approveMedical() - changes approval status of a medical record.
     */
    public boolean approveMedical(int medicalId, String newStatus) throws SQLException {
        Medical med = new Medical();
        med.setMedicalId(medicalId);
        return med.approveMedical(newStatus);
    }

    /**
     * DTO for Timetable records.
     */
    public static class TimetableEntry {
        private int timetableId;
        private String courseCode;
        private String courseName;
        private String department;
        private String day;
        private String timeSlot;

        public int getTimetableId() { return timetableId; }
        public void setTimetableId(int timetableId) { this.timetableId = timetableId; }
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getDay() { return day; }
        public void setDay(String day) { this.day = day; }
        public String getTimeSlot() { return timeSlot; }
        public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    }

    /**
     * viewTimetable() - views timetable of their department according to SRS 2.3 & 9.4.
     */
    public List<TimetableEntry> viewTimetable() throws SQLException {
        return viewTimetable(this.department);
    }

    /**
     * viewTimetable(department) - queries timetable records for department.
     */
    public static List<TimetableEntry> viewTimetable(String dept) throws SQLException {
        List<TimetableEntry> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t.Timetable_ID, t.Course_code, c.Course_name, t.Department, t.Day, t.Time_slot ")
           .append("FROM Timetable t ")
           .append("JOIN Course c ON t.Course_code = c.Course_code ");

        if (dept != null && !dept.trim().isEmpty() && !dept.equalsIgnoreCase("ALL")) {
            sql.append("WHERE t.Department = ? ");
        }
        sql.append("ORDER BY FIELD(t.Day, 'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'), t.Time_slot");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (dept != null && !dept.trim().isEmpty() && !dept.equalsIgnoreCase("ALL")) {
                stmt.setString(1, dept.trim());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TimetableEntry entry = new TimetableEntry();
                    entry.setTimetableId(rs.getInt("Timetable_ID"));
                    entry.setCourseCode(rs.getString("Course_code"));
                    entry.setCourseName(rs.getString("Course_name"));
                    entry.setDepartment(rs.getString("Department"));
                    entry.setDay(rs.getString("Day"));
                    entry.setTimeSlot(rs.getString("Time_slot"));
                    list.add(entry);
                }
            }
        }
        return list;
    }

    /**
     * DTO for Notice records.
     */
    public static class NoticeEntry {
        private int noticeId;
        private String title;
        private String content;
        private LocalDate publishedDate;

        public int getNoticeId() { return noticeId; }
        public void setNoticeId(int noticeId) { this.noticeId = noticeId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDate getPublishedDate() { return publishedDate; }
        public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }
    }

    /**
     * View notices published for all users including Technical Officers (SRS 2.3, 9.4).
     */
    public static List<NoticeEntry> viewNotices() throws SQLException {
        List<NoticeEntry> list = new ArrayList<>();
        String sql = "SELECT Notice_ID, Title, Content, Published_date FROM Notice ORDER BY Published_date DESC, Notice_ID DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                NoticeEntry n = new NoticeEntry();
                n.setNoticeId(rs.getInt("Notice_ID"));
                n.setTitle(rs.getString("Title"));
                n.setContent(rs.getString("Content"));
                Date pd = rs.getDate("Published_date");
                n.setPublishedDate(pd != null ? pd.toLocalDate() : null);
                list.add(n);
            }
        }
        return list;
    }
}
