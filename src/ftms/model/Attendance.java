package ftms.model;

import ftms.db.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Attendance class representing attendance records and business logic.
 * Assigned to Member 3.
 *
 * Implements:
 * - addAttendance()
 * - updateAttendance()
 * - deleteAttendance()
 * - calculateAttendancePercentage()
 * - viewAttendanceSummary()
 */
public class Attendance {
    public static final int TOTAL_THEORY_SESSIONS = 15;
    public static final int TOTAL_PRACTICAL_SESSIONS = 15;
    public static final int TOTAL_SESSIONS = TOTAL_THEORY_SESSIONS + TOTAL_PRACTICAL_SESSIONS; // 30
    public static final double ELIGIBILITY_THRESHOLD = 80.0;

    private int attendanceId;
    private int studentId;
    private String courseCode;
    private LocalDate sessionDate;
    private String type; // 'THEORY' or 'PRACTICAL'

    // Additional display fields
    private String tgNum;
    private String studentName;
    private String courseName;

    public Attendance() {
    }

    public Attendance(int attendanceId, int studentId, String courseCode, LocalDate sessionDate, String type) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.sessionDate = sessionDate;
        this.type = type != null ? type.toUpperCase() : null;
    }

    public Attendance(int studentId, String courseCode, LocalDate sessionDate, String type) {
        this(0, studentId, courseCode, sessionDate, type);
    }

    // Getters and Setters demonstrating Encapsulation
    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type != null) {
            String upper = type.toUpperCase();
            if (upper.equals("THEORY") || upper.equals("PRACTICAL")) {
                this.type = upper;
            } else {
                throw new IllegalArgumentException("Type must be either THEORY or PRACTICAL");
            }
        }
    }

    public String getTgNum() {
        return tgNum;
    }

    public void setTgNum(String tgNum) {
        this.tgNum = tgNum;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // ==========================================
    // Core Assigned Methods for Member 3
    // ==========================================

    /**
     * Adds an attendance record to the database.
     * Prevents duplicate attendance for the same student, course, date, and type.
     */
    public boolean addAttendance() throws SQLException {
        if (studentId <= 0 || courseCode == null || sessionDate == null || type == null) {
            throw new IllegalArgumentException("Invalid attendance details. All fields are required.");
        }

        String checkSql = "SELECT COUNT(*) FROM Attendance WHERE Student_ID = ? AND Course_code = ? AND Session_date = ? AND Type = ?";
        String insertSql = "INSERT INTO Attendance (Student_ID, Course_code, Session_date, Type) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, this.studentId);
                checkStmt.setString(2, this.courseCode);
                checkStmt.setDate(3, Date.valueOf(this.sessionDate));
                checkStmt.setString(4, this.type);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException("Attendance for student " + this.studentId + " on " + this.sessionDate + " (" + this.type + ") already exists.");
                    }
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, this.studentId);
                insertStmt.setString(2, this.courseCode);
                insertStmt.setDate(3, Date.valueOf(this.sessionDate));
                insertStmt.setString(4, this.type);

                int affected = insertStmt.executeUpdate();
                if (affected > 0) {
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            this.attendanceId = generatedKeys.getInt(1);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Updates an existing attendance record.
     */
    public boolean updateAttendance() throws SQLException {
        if (this.attendanceId <= 0) {
            throw new IllegalArgumentException("Attendance ID is required for update.");
        }

        String sql = "UPDATE Attendance SET Student_ID = ?, Course_code = ?, Session_date = ?, Type = ? WHERE Attendance_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.studentId);
            stmt.setString(2, this.courseCode);
            stmt.setDate(3, Date.valueOf(this.sessionDate));
            stmt.setString(4, this.type);
            stmt.setInt(5, this.attendanceId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes an attendance record from the database.
     */
    public boolean deleteAttendance() throws SQLException {
        if (this.attendanceId <= 0) {
            throw new IllegalArgumentException("Attendance ID is required for deletion.");
        }

        String sql = "DELETE FROM Attendance WHERE Attendance_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.attendanceId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Static helper to delete attendance by ID.
     */
    public static boolean deleteAttendance(int attendanceId) throws SQLException {
        Attendance att = new Attendance();
        att.setAttendanceId(attendanceId);
        return att.deleteAttendance();
    }

    /**
     * Calculates attendance percentage based on attended sessions and total scheduled sessions (15 per type, 30 combined).
     *
     * @param attendedSessions Number of attended sessions.
     * @param totalSessions Total sessions (15 for theory/practical, 30 for combined).
     * @return attendance percentage (0.0 - 100.0)
     */
    public static double calculateAttendancePercentage(int attendedSessions, int totalSessions) {
        if (totalSessions <= 0) {
            return 0.0;
        }
        double percentage = ((double) attendedSessions / totalSessions) * 100.0;
        return Math.min(100.0, Math.round(percentage * 100.0) / 100.0);
    }

    /**
     * Data Transfer Object for Attendance Summary representation complying with SRS Section 4.
     */
    public static class AttendanceSummaryDTO {
        private int studentId;
        private String tgNum;
        private String studentName;
        private String courseCode;
        private String courseName;

        private int theoryAttended;
        private int practicalAttended;
        private int totalAttended;

        private int theoryApprovedMedicals;
        private int practicalApprovedMedicals;
        private int totalApprovedMedicals;

        private double theoryPercentage;
        private double practicalPercentage;
        private double combinedPercentage;

        private double effectiveCombinedPercentage;
        private String eligibilityStatus;

        public int getStudentId() { return studentId; }
        public void setStudentId(int studentId) { this.studentId = studentId; }
        public String getTgNum() { return tgNum; }
        public void setTgNum(String tgNum) { this.tgNum = tgNum; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public int getTheoryAttended() { return theoryAttended; }
        public void setTheoryAttended(int theoryAttended) { this.theoryAttended = theoryAttended; }
        public int getPracticalAttended() { return practicalAttended; }
        public void setPracticalAttended(int practicalAttended) { this.practicalAttended = practicalAttended; }
        public int getTotalAttended() { return totalAttended; }
        public void setTotalAttended(int totalAttended) { this.totalAttended = totalAttended; }
        public int getTheoryApprovedMedicals() { return theoryApprovedMedicals; }
        public void setTheoryApprovedMedicals(int theoryApprovedMedicals) { this.theoryApprovedMedicals = theoryApprovedMedicals; }
        public int getPracticalApprovedMedicals() { return practicalApprovedMedicals; }
        public void setPracticalApprovedMedicals(int practicalApprovedMedicals) { this.practicalApprovedMedicals = practicalApprovedMedicals; }
        public int getTotalApprovedMedicals() { return totalApprovedMedicals; }
        public void setTotalApprovedMedicals(int totalApprovedMedicals) { this.totalApprovedMedicals = totalApprovedMedicals; }
        public double getTheoryPercentage() { return theoryPercentage; }
        public void setTheoryPercentage(double theoryPercentage) { this.theoryPercentage = theoryPercentage; }
        public double getPracticalPercentage() { return practicalPercentage; }
        public void setPracticalPercentage(double practicalPercentage) { this.practicalPercentage = practicalPercentage; }
        public double getCombinedPercentage() { return combinedPercentage; }
        public void setCombinedPercentage(double combinedPercentage) { this.combinedPercentage = combinedPercentage; }
        public double getEffectiveCombinedPercentage() { return effectiveCombinedPercentage; }
        public void setEffectiveCombinedPercentage(double effectiveCombinedPercentage) { this.effectiveCombinedPercentage = effectiveCombinedPercentage; }
        public String getEligibilityStatus() { return eligibilityStatus; }
        public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }
    }

    /**
     * Views attendance summary for an individual student in a course or for the whole batch.
     * Complies with SRS 4:
     * - 15 theory sessions & 15 practical sessions
     * - >80%, =80%, <80% without medicals, >80% with medicals, <80% with medicals.
     *
     * @param courseCode specific course code or null for all courses
     * @param studentId specific student ID or 0 for whole batch
     * @return List of AttendanceSummaryDTO
     */
    public static List<AttendanceSummaryDTO> viewAttendanceSummary(String courseCode, int studentId) throws SQLException {
        List<AttendanceSummaryDTO> summaries = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT s.Student_ID, s.TG_num, u.Fname, u.Lname, c.Course_code, c.Course_name, ")
           .append("COUNT(CASE WHEN a.Type = 'THEORY' THEN 1 END) AS theory_count, ")
           .append("COUNT(CASE WHEN a.Type = 'PRACTICAL' THEN 1 END) AS practical_count, ")
           .append("COUNT(a.Attendance_ID) AS total_count, ")
           .append("COUNT(CASE WHEN m.Approval_status = 'APPROVED' AND a.Type = 'THEORY' THEN 1 END) AS approved_theory_medicals, ")
           .append("COUNT(CASE WHEN m.Approval_status = 'APPROVED' AND a.Type = 'PRACTICAL' THEN 1 END) AS approved_practical_medicals, ")
           .append("COUNT(CASE WHEN m.Approval_status = 'APPROVED' THEN 1 END) AS approved_total_medicals ")
           .append("FROM Student s ")
           .append("JOIN User u ON s.Student_ID = u.User_ID ")
           .append("JOIN Course c ON 1=1 ");

        if (courseCode != null && !courseCode.trim().isEmpty()) {
            sql.append("AND c.Course_code = ? ");
        }

        sql.append("LEFT JOIN Attendance a ON s.Student_ID = a.Student_ID AND c.Course_code = a.Course_code ")
           .append("LEFT JOIN Medical m ON a.Attendance_ID = m.Attendance_ID ");

        if (studentId > 0) {
            sql.append("WHERE s.Student_ID = ? ");
        }

        sql.append("GROUP BY s.Student_ID, s.TG_num, u.Fname, u.Lname, c.Course_code, c.Course_name ")
           .append("ORDER BY c.Course_code, s.TG_num");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (courseCode != null && !courseCode.trim().isEmpty()) {
                stmt.setString(paramIndex++, courseCode.trim());
            }
            if (studentId > 0) {
                stmt.setInt(paramIndex++, studentId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceSummaryDTO dto = new AttendanceSummaryDTO();
                    dto.setStudentId(rs.getInt("Student_ID"));
                    dto.setTgNum(rs.getString("TG_num"));
                    String fname = rs.getString("Fname");
                    String lname = rs.getString("Lname");
                    dto.setStudentName((fname != null ? fname : "") + " " + (lname != null ? lname : "").trim());
                    dto.setCourseCode(rs.getString("Course_code"));
                    dto.setCourseName(rs.getString("Course_name"));

                    int theoryAtt = rs.getInt("theory_count");
                    int practicalAtt = rs.getInt("practical_count");
                    int totalAtt = rs.getInt("total_count");

                    int approvedTheoryMed = rs.getInt("approved_theory_medicals");
                    int approvedPracticalMed = rs.getInt("approved_practical_medicals");
                    int approvedTotalMed = rs.getInt("approved_total_medicals");

                    dto.setTheoryAttended(theoryAtt);
                    dto.setPracticalAttended(practicalAtt);
                    dto.setTotalAttended(totalAtt);

                    dto.setTheoryApprovedMedicals(approvedTheoryMed);
                    dto.setPracticalApprovedMedicals(approvedPracticalMed);
                    dto.setTotalApprovedMedicals(approvedTotalMed);

                    double thPct = calculateAttendancePercentage(theoryAtt, TOTAL_THEORY_SESSIONS);
                    double prPct = calculateAttendancePercentage(practicalAtt, TOTAL_PRACTICAL_SESSIONS);
                    double combPct = calculateAttendancePercentage(totalAtt, TOTAL_SESSIONS);

                    // Effective calculation with approved medicals
                    double effTotalAtt = totalAtt + approvedTotalMed;
                    double effCombPct = calculateAttendancePercentage((int) effTotalAtt, TOTAL_SESSIONS);

                    dto.setTheoryPercentage(thPct);
                    dto.setPracticalPercentage(prPct);
                    dto.setCombinedPercentage(combPct);
                    dto.setEffectiveCombinedPercentage(effCombPct);

                    // SRS Scenarios determination:
                    // >80%, =80%, <80% without medicals, >80% with medicals, <80% with medicals
                    String scenario;
                    if (combPct >= ELIGIBILITY_THRESHOLD) {
                        if (Math.abs(combPct - ELIGIBILITY_THRESHOLD) < 0.01) {
                            scenario = "Exactly 80% (Eligible)";
                        } else {
                            scenario = "Above 80% (Eligible)";
                        }
                    } else {
                        // Less than 80% raw
                        if (approvedTotalMed > 0) {
                            if (effCombPct >= ELIGIBILITY_THRESHOLD) {
                                scenario = "Above 80% with Medicals (Eligible)";
                            } else {
                                scenario = "Below 80% with Medicals (Not Eligible)";
                            }
                        } else {
                            scenario = "Below 80% without Medicals (Not Eligible)";
                        }
                    }
                    dto.setEligibilityStatus(scenario);

                    summaries.add(dto);
                }
            }
        }

        return summaries;
    }

    /**
     * Retrieves attendance records matching filter criteria.
     */
    public static List<Attendance> getAttendanceRecords(String courseCode, Integer studentId, String type) throws SQLException {
        List<Attendance> records = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.Attendance_ID, a.Student_ID, a.Course_code, a.Session_date, a.Type, ")
           .append("s.TG_num, u.Fname, u.Lname, c.Course_name ")
           .append("FROM Attendance a ")
           .append("JOIN Student s ON a.Student_ID = s.Student_ID ")
           .append("JOIN User u ON s.Student_ID = u.User_ID ")
           .append("JOIN Course c ON a.Course_code = c.Course_code ")
           .append("WHERE 1=1 ");

        if (courseCode != null && !courseCode.trim().isEmpty()) {
            sql.append("AND a.Course_code = ? ");
        }
        if (studentId != null && studentId > 0) {
            sql.append("AND a.Student_ID = ? ");
        }
        if (type != null && !type.trim().isEmpty() && !type.equalsIgnoreCase("ALL")) {
            sql.append("AND a.Type = ? ");
        }
        sql.append("ORDER BY a.Session_date DESC, a.Attendance_ID DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (courseCode != null && !courseCode.trim().isEmpty()) {
                stmt.setString(paramIndex++, courseCode.trim());
            }
            if (studentId != null && studentId > 0) {
                stmt.setInt(paramIndex++, studentId);
            }
            if (type != null && !type.trim().isEmpty() && !type.equalsIgnoreCase("ALL")) {
                stmt.setString(paramIndex++, type.toUpperCase().trim());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendance att = new Attendance();
                    att.setAttendanceId(rs.getInt("Attendance_ID"));
                    att.setStudentId(rs.getInt("Student_ID"));
                    att.setCourseCode(rs.getString("Course_code"));
                    Date d = rs.getDate("Session_date");
                    att.setSessionDate(d != null ? d.toLocalDate() : null);
                    att.setType(rs.getString("Type"));
                    att.setTgNum(rs.getString("TG_num"));
                    String fname = rs.getString("Fname");
                    String lname = rs.getString("Lname");
                    att.setStudentName((fname != null ? fname : "") + " " + (lname != null ? lname : "").trim());
                    att.setCourseName(rs.getString("Course_name"));
                    records.add(att);
                }
            }
        }
        return records;
    }
}
