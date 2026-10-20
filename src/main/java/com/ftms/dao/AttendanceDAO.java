package com.ftms.dao;

import com.ftms.model.Attendance;
import com.ftms.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Access Object (DAO) for managing Attendance records in the database.
 * Encapsulates all SQL operations for the Attendance table in db.sql:
 * Attendance(Attendance_ID, Student_ID, Course_code, Session_date, Type).
 *
 * Demonstrates:
 * - DAO Pattern & Encapsulation: Database queries and updates are strictly isolated from GUI and domain logic.
 * - Robust Resource Management: Standard try-with-resources for Connection, PreparedStatement, and ResultSet.
 * - Defensive Data Integrity: Parameterized queries against SQL injection and duplicate entry detection.
 * - Transaction Management: Batch operations execute atomically with rollback upon failure.
 * - Safe Math Handling: Safe division preventing ArithmeticException/NaN when zero sessions exist.
 * - SRS Compliance: Explicit evaluation for attendance thresholds (above 80%, exactly 80%, and below 80%).
 */
public class AttendanceDAO {

    /**
     * Enumeration representing student exam eligibility status based on the 80% attendance requirement.
     */
    public enum EligibilityStatus {
        ABOVE_80("Eligible - Attendance Above 80%"),
        EXACTLY_80("Eligible - Attendance Exactly 80% (Meets Minimum Threshold)"),
        BELOW_80("Ineligible - Attendance Below 80% (Barred from Final Exam)");

        private final String description;

        EligibilityStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public boolean isEligible() {
            return this == ABOVE_80 || this == EXACTLY_80;
        }
    }

    /**
     * Data Transfer Object (DTO) encapsulating an individual student's attendance summary.
     */
    public static class AttendanceSummary {
        private final int studentId;
        private final String tgNumber;
        private final String studentName;
        private final String courseCode;
        private final int attendedTheory;
        private final int totalTheory;
        private final double theoryPercentage;
        private final int attendedPractical;
        private final int totalPractical;
        private final double practicalPercentage;
        private final int totalAttended;
        private final int totalSessions;
        private final double combinedPercentage;
        private final EligibilityStatus eligibilityStatus;

        public AttendanceSummary(int studentId, String tgNumber, String studentName, String courseCode,
                                 int attendedTheory, int totalTheory, double theoryPercentage,
                                 int attendedPractical, int totalPractical, double practicalPercentage,
                                 int totalAttended, int totalSessions, double combinedPercentage,
                                 EligibilityStatus eligibilityStatus) {
            this.studentId = studentId;
            this.tgNumber = tgNumber;
            this.studentName = studentName;
            this.courseCode = courseCode;
            this.attendedTheory = attendedTheory;
            this.totalTheory = totalTheory;
            this.theoryPercentage = theoryPercentage;
            this.attendedPractical = attendedPractical;
            this.totalPractical = totalPractical;
            this.practicalPercentage = practicalPercentage;
            this.totalAttended = totalAttended;
            this.totalSessions = totalSessions;
            this.combinedPercentage = combinedPercentage;
            this.eligibilityStatus = eligibilityStatus;
        }

        public int getStudentId() { return studentId; }
        public String getTgNumber() { return tgNumber; }
        public String getStudentName() { return studentName; }
        public String getCourseCode() { return courseCode; }
        public int getAttendedTheory() { return attendedTheory; }
        public int getTotalTheory() { return totalTheory; }
        public double getTheoryPercentage() { return theoryPercentage; }
        public int getAttendedPractical() { return attendedPractical; }
        public int getTotalPractical() { return totalPractical; }
        public double getPracticalPercentage() { return practicalPercentage; }
        public int getTotalAttended() { return totalAttended; }
        public int getTotalSessions() { return totalSessions; }
        public double getCombinedPercentage() { return combinedPercentage; }
        public EligibilityStatus getEligibilityStatus() { return eligibilityStatus; }
        public boolean isEligible() { return eligibilityStatus != null && eligibilityStatus.isEligible(); }

        @Override
        public String toString() {
            return "AttendanceSummary{" +
                    "studentId=" + studentId +
                    ", tgNumber='" + tgNumber + '\'' +
                    ", courseCode='" + courseCode + '\'' +
                    ", theory=" + attendedTheory + "/" + totalTheory + " (" + String.format("%.2f", theoryPercentage) + "%)" +
                    ", practical=" + attendedPractical + "/" + totalPractical + " (" + String.format("%.2f", practicalPercentage) + "%)" +
                    ", combined=" + totalAttended + "/" + totalSessions + " (" + String.format("%.2f", combinedPercentage) + "%)" +
                    ", status=" + eligibilityStatus +
                    '}';
        }
    }

    // Optional injected connection (supports dependency injection for testing and external transaction control)
    private Connection connection;

    /**
     * Default constructor. Uses DatabaseConnection.getConnection() for each operation.
     */
    public AttendanceDAO() {
    }

    /**
     * Constructor with dependency injection for connection reuse or unit testing.
     *
     * @param connection active JDBC Connection
     */
    public AttendanceDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Obtains a valid database connection, either using the injected connection or
     * requesting a new connection from the centralized DatabaseConnection utility.
     */
    protected Connection getConnection() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            return this.connection;
        }
        return DatabaseConnection.getConnection();
    }

    // =========================================================================
    // INSERT OPERATIONS
    // =========================================================================

    /**
     * Inserts a single attendance record.
     * Avoids duplicate entries: checks if an identical attendance record
     * (same student, course, date, and type) already exists.
     *
     * @param attendance the Attendance model to persist
     * @return true if successfully inserted, false if duplicate detected
     * @throws SQLException if a database access error occurs
     */
    public boolean insertAttendance(Attendance attendance) throws SQLException {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance object cannot be null.");
        }

        // Avoid duplicate attendance entries
        if (isAttendanceRecorded(attendance.getStudentId(), attendance.getCourseCode(),
                attendance.getSessionDate(), attendance.getType())) {
            return false;
        }

        String sql = "INSERT INTO Attendance (Student_ID, Course_code, Session_date, Type) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, attendance.getStudentId());
            ps.setString(2, attendance.getCourseCode());
            ps.setDate(3, attendance.getSessionDateAsSqlDate());
            ps.setString(4, attendance.getType());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        attendance.setAttendanceId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Inserts a batch of attendance records within a single database transaction.
     * If any insert encounters an error, the entire batch is rolled back to maintain consistency.
     *
     * @param attendanceList list of attendance records to insert
     * @return number of records successfully inserted
     * @throws SQLException if transaction fails or connection error occurs
     */
    public int insertBatchAttendance(List<Attendance> attendanceList) throws SQLException {
        if (attendanceList == null || attendanceList.isEmpty()) {
            return 0;
        }

        String sql = "INSERT INTO Attendance (Student_ID, Course_code, Session_date, Type) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        boolean originalAutoCommit = true;
        int insertedCount = 0;

        try {
            conn = getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false); // Begin transaction

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (Attendance att : attendanceList) {
                    if (att == null) continue;

                    // Check for duplicates before adding to batch
                    if (!isAttendanceRecordedWithConnection(conn, att.getStudentId(), att.getCourseCode(),
                            att.getSessionDate(), att.getType())) {
                        ps.setInt(1, att.getStudentId());
                        ps.setString(2, att.getCourseCode());
                        ps.setDate(3, att.getSessionDateAsSqlDate());
                        ps.setString(4, att.getType());

                        ps.executeUpdate();
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (rs.next()) {
                                att.setAttendanceId(rs.getInt(1));
                            }
                        }
                        insertedCount++;
                    }
                }
            }

            conn.commit(); // Commit transaction
            return insertedCount;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on failure
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(originalAutoCommit);
                    if (this.connection == null) {
                        conn.close();
                    }
                } catch (SQLException ignored) {
                }
            }
        }
    }

    // =========================================================================
    // DUPLICATE CHECKING
    // =========================================================================

    /**
     * Checks if an attendance entry already exists for a student, course, date, and type.
     */
    public boolean isAttendanceRecorded(int studentId, String courseCode, LocalDate sessionDate, String type) throws SQLException {
        try (Connection conn = getConnection()) {
            return isAttendanceRecordedWithConnection(conn, studentId, courseCode, sessionDate, type);
        }
    }

    private boolean isAttendanceRecordedWithConnection(Connection conn, int studentId, String courseCode,
                                                       LocalDate sessionDate, String type) throws SQLException {
        String sql = "SELECT 1 FROM Attendance WHERE Student_ID = ? AND Course_code = ? AND Session_date = ? AND Type = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            ps.setDate(3, sessionDate != null ? Date.valueOf(sessionDate) : null);
            ps.setString(4, type);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // =========================================================================
    // RETRIEVAL OPERATIONS (STUDENT & COURSE)
    // =========================================================================

    /**
     * Retrieves an Attendance record by its primary key Attendance_ID.
     */
    public Attendance getAttendanceById(int attendanceId) throws SQLException {
        String sql = "SELECT Attendance_ID, Student_ID, Course_code, Session_date, Type FROM Attendance WHERE Attendance_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attendanceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAttendance(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all attendance records for an individual student and specific course.
     */
    public List<Attendance> getAttendanceByStudentAndCourse(int studentId, String courseCode) throws SQLException {
        String sql = "SELECT Attendance_ID, Student_ID, Course_code, Session_date, Type " +
                     "FROM Attendance WHERE Student_ID = ? AND Course_code = ? ORDER BY Session_date ASC, Attendance_ID ASC";
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAttendance(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves all attendance records for an individual student across all courses.
     */
    public List<Attendance> getAttendanceByStudent(int studentId) throws SQLException {
        String sql = "SELECT Attendance_ID, Student_ID, Course_code, Session_date, Type " +
                     "FROM Attendance WHERE Student_ID = ? ORDER BY Session_date ASC, Course_code ASC";
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAttendance(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves all attendance records for a course across all students.
     */
    public List<Attendance> getAttendanceByCourse(String courseCode) throws SQLException {
        String sql = "SELECT Attendance_ID, Student_ID, Course_code, Session_date, Type " +
                     "FROM Attendance WHERE Course_code = ? ORDER BY Session_date ASC, Student_ID ASC";
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAttendance(rs));
                }
            }
        }
        return list;
    }

    // =========================================================================
    // FILTERING (THEORY & PRACTICAL)
    // =========================================================================

    /**
     * Retrieves attendance records filtered by session type (THEORY or PRACTICAL) for a student and course.
     */
    public List<Attendance> getAttendanceByStudentCourseAndType(int studentId, String courseCode, String type) throws SQLException {
        String sql = "SELECT Attendance_ID, Student_ID, Course_code, Session_date, Type " +
                     "FROM Attendance WHERE Student_ID = ? AND Course_code = ? AND Type = ? ORDER BY Session_date ASC";
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            ps.setString(3, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAttendance(rs));
                }
            }
        }
        return list;
    }

    /**
     * Counts how many sessions of a specific type (THEORY or PRACTICAL) a student attended for a course.
     */
    public int getAttendedSessionsCount(int studentId, String courseCode, String type) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Attendance WHERE Student_ID = ? AND Course_code = ? AND Type = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            ps.setString(3, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Counts total sessions (THEORY + PRACTICAL) attended by a student for a course.
     */
    public int getTotalAttendedSessionsCount(int studentId, String courseCode) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Attendance WHERE Student_ID = ? AND Course_code = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // =========================================================================
    // CONDUCTED SESSIONS IN SCHEMA
    // =========================================================================

    /**
     * Calculates the total number of distinct sessions conducted for a course of a given type.
     * In the database schema, total conducted sessions correspond to distinct dates recorded in Attendance for that course.
     */
    public int getTotalConductedSessionsCount(String courseCode, String type) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT Session_date) FROM Attendance WHERE Course_code = ? AND Type = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ps.setString(2, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Calculates total distinct conducted sessions (both Theory and Practical combined) for a course.
     */
    public int getTotalConductedSessionsCount(String courseCode) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT Session_date, Type) FROM Attendance WHERE Course_code = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Calculates total distinct conducted sessions for a specific batch and course.
     */
    public int getTotalConductedSessionsForBatch(String batch, String courseCode, String type) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT a.Session_date) FROM Attendance a " +
                     "JOIN Student s ON a.Student_ID = s.Student_ID " +
                     "WHERE s.Batch = ? AND a.Course_code = ? AND a.Type = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, batch);
            ps.setString(2, courseCode);
            ps.setString(3, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // =========================================================================
    // PERCENTAGE CALCULATIONS & ZERO SESSIONS HANDLING
    // =========================================================================

    /**
     * Calculates Theory attendance percentage for a student and course against total conducted sessions in the database.
     * Safely handles zero recorded sessions by returning 0.0%.
     */
    public double calculateTheoryAttendancePercentage(int studentId, String courseCode) throws SQLException {
        int totalConducted = getTotalConductedSessionsCount(courseCode, Attendance.TYPE_THEORY);
        int attended = getAttendedSessionsCount(studentId, courseCode, Attendance.TYPE_THEORY);
        return calculatePercentageSafely(attended, totalConducted);
    }

    /**
     * Calculates Theory attendance percentage given an explicit total session count (e.g., from academic timetable/syllabus).
     * Safely handles zero recorded sessions by returning 0.0%.
     */
    public double calculateTheoryAttendancePercentage(int studentId, String courseCode, int totalTheorySessions) throws SQLException {
        int attended = getAttendedSessionsCount(studentId, courseCode, Attendance.TYPE_THEORY);
        return calculatePercentageSafely(attended, totalTheorySessions);
    }

    /**
     * Calculates Practical attendance percentage for a student and course against total conducted sessions in the database.
     * Safely handles zero recorded sessions by returning 0.0%.
     */
    public double calculatePracticalAttendancePercentage(int studentId, String courseCode) throws SQLException {
        int totalConducted = getTotalConductedSessionsCount(courseCode, Attendance.TYPE_PRACTICAL);
        int attended = getAttendedSessionsCount(studentId, courseCode, Attendance.TYPE_PRACTICAL);
        return calculatePercentageSafely(attended, totalConducted);
    }

    /**
     * Calculates Practical attendance percentage given an explicit total session count.
     * Safely handles zero recorded sessions by returning 0.0%.
     */
    public double calculatePracticalAttendancePercentage(int studentId, String courseCode, int totalPracticalSessions) throws SQLException {
        int attended = getAttendedSessionsCount(studentId, courseCode, Attendance.TYPE_PRACTICAL);
        return calculatePercentageSafely(attended, totalPracticalSessions);
    }

    /**
     * Calculates Combined (Theory + Practical) attendance percentage against database conducted sessions.
     * Safely handles zero recorded sessions by returning 0.0%.
     */
    public double calculateCombinedAttendancePercentage(int studentId, String courseCode) throws SQLException {
        int totalTheory = getTotalConductedSessionsCount(courseCode, Attendance.TYPE_THEORY);
        int totalPractical = getTotalConductedSessionsCount(courseCode, Attendance.TYPE_PRACTICAL);
        int totalSessions = totalTheory + totalPractical;

        int attended = getTotalAttendedSessionsCount(studentId, courseCode);
        return calculatePercentageSafely(attended, totalSessions);
    }

    /**
     * Calculates Combined attendance percentage given explicit total session counts.
     * Safely handles zero recorded sessions by returning 0.0%.
     */
    public double calculateCombinedAttendancePercentage(int studentId, String courseCode,
                                                        int totalTheorySessions, int totalPracticalSessions) throws SQLException {
        int totalSessions = totalTheorySessions + totalPracticalSessions;
        int attended = getTotalAttendedSessionsCount(studentId, courseCode);
        return calculatePercentageSafely(attended, totalSessions);
    }

    /**
     * Safe percentage calculation ensuring zero-session denominator results in 0.0 instead of NaN/Infinity.
     * Clamps result to [0.0, 100.0].
     */
    public static double calculatePercentageSafely(int attended, int total) {
        if (total <= 0 || attended <= 0) {
            return 0.0;
        }
        double percentage = (attended * 100.0) / total;
        return Math.min(percentage, 100.0);
    }

    // =========================================================================
    // SRS SCENARIOS (ABOVE 80%, EXACTLY 80%, BELOW 80%)
    // =========================================================================

    /**
     * Evaluates attendance percentage according to SRS threshold rules:
     * - Above 80%: percentage > 80.0 -> EligibilityStatus.ABOVE_80 (Eligible)
     * - Exactly 80%: percentage == 80.0 (within floating point precision) -> EligibilityStatus.EXACTLY_80 (Eligible)
     * - Below 80%: percentage < 80.0 -> EligibilityStatus.BELOW_80 (Ineligible/Barred)
     *
     * @param percentage attendance percentage value
     * @return EligibilityStatus
     */
    public static EligibilityStatus evaluateEligibility(double percentage) {
        if (Double.isNaN(percentage) || percentage < 0.0) {
            return EligibilityStatus.BELOW_80;
        }
        // Floating point comparison with 1e-4 tolerance
        if (Math.abs(percentage - 80.0) < 0.0001) {
            return EligibilityStatus.EXACTLY_80;
        } else if (percentage > 80.0) {
            return EligibilityStatus.ABOVE_80;
        } else {
            return EligibilityStatus.BELOW_80;
        }
    }

    /**
     * Convenience check returning true if the student meets or exceeds the mandatory 80% threshold.
     */
    public static boolean isEligibleForExam(double percentage) {
        return evaluateEligibility(percentage).isEligible();
    }

    /**
     * Generates a complete AttendanceSummary DTO for an individual student and course.
     */
    public AttendanceSummary getStudentAttendanceSummary(int studentId, String courseCode) throws SQLException {
        String studentInfoSql = "SELECT s.TG_num, u.Fname, u.Lname FROM Student s " +
                                "JOIN User u ON s.Student_ID = u.User_ID WHERE s.Student_ID = ?";
        String tgNum = "";
        String studentName = "";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(studentInfoSql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tgNum = rs.getString("TG_num");
                    studentName = rs.getString("Fname") + " " + rs.getString("Lname");
                }
            }
        }

        int attendedTheory = getAttendedSessionsCount(studentId, courseCode, Attendance.TYPE_THEORY);
        int totalTheory = getTotalConductedSessionsCount(courseCode, Attendance.TYPE_THEORY);
        double theoryPct = calculatePercentageSafely(attendedTheory, totalTheory);

        int attendedPractical = getAttendedSessionsCount(studentId, courseCode, Attendance.TYPE_PRACTICAL);
        int totalPractical = getTotalConductedSessionsCount(courseCode, Attendance.TYPE_PRACTICAL);
        double practicalPct = calculatePercentageSafely(attendedPractical, totalPractical);

        int totalAttended = attendedTheory + attendedPractical;
        int totalSessions = totalTheory + totalPractical;
        double combinedPct = calculatePercentageSafely(totalAttended, totalSessions);

        EligibilityStatus status = evaluateEligibility(combinedPct);

        return new AttendanceSummary(studentId, tgNum, studentName, courseCode,
                attendedTheory, totalTheory, theoryPct,
                attendedPractical, totalPractical, practicalPct,
                totalAttended, totalSessions, combinedPct, status);
    }

    // =========================================================================
    // BATCH ATTENDANCE & BATCH SUMMARIES
    // =========================================================================

    /**
     * Retrieves attendance records for all students within a specified batch for a course.
     * Uses Student.Batch column supported in db.sql.
     */
    public List<Attendance> getAttendanceByBatchAndCourse(String batch, String courseCode) throws SQLException {
        String sql = "SELECT a.Attendance_ID, a.Student_ID, a.Course_code, a.Session_date, a.Type " +
                     "FROM Attendance a " +
                     "JOIN Student s ON a.Student_ID = s.Student_ID " +
                     "WHERE s.Batch = ? AND a.Course_code = ? " +
                     "ORDER BY a.Session_date ASC, a.Student_ID ASC";
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, batch);
            ps.setString(2, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAttendance(rs));
                }
            }
        }
        return list;
    }

    /**
     * Generates a batch summary report listing attendance statistics and 80% eligibility for every student in a batch.
     */
    public List<AttendanceSummary> getBatchAttendanceSummaries(String batch, String courseCode) throws SQLException {
        String sql = "SELECT s.Student_ID, s.TG_num, u.Fname, u.Lname FROM Student s " +
                     "JOIN User u ON s.Student_ID = u.User_ID " +
                     "WHERE s.Batch = ? ORDER BY s.TG_num ASC";
        List<AttendanceSummary> summaries = new ArrayList<>();

        int totalTheory = getTotalConductedSessionsForBatch(batch, courseCode, Attendance.TYPE_THEORY);
        int totalPractical = getTotalConductedSessionsForBatch(batch, courseCode, Attendance.TYPE_PRACTICAL);
        int totalSessions = totalTheory + totalPractical;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, batch);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int studentId = rs.getInt("Student_ID");
                    String tgNum = rs.getString("TG_num");
                    String studentName = rs.getString("Fname") + " " + rs.getString("Lname");

                    int attendedTheory = getAttendedSessionsCount(studentId, courseCode, Attendance.TYPE_THEORY);
                    int attendedPractical = getAttendedSessionsCount(studentId, courseCode, Attendance.TYPE_PRACTICAL);
                    int totalAttended = attendedTheory + attendedPractical;

                    double theoryPct = calculatePercentageSafely(attendedTheory, totalTheory);
                    double practicalPct = calculatePercentageSafely(attendedPractical, totalPractical);
                    double combinedPct = calculatePercentageSafely(totalAttended, totalSessions);

                    EligibilityStatus status = evaluateEligibility(combinedPct);

                    summaries.add(new AttendanceSummary(studentId, tgNum, studentName, courseCode,
                            attendedTheory, totalTheory, theoryPct,
                            attendedPractical, totalPractical, practicalPct,
                            totalAttended, totalSessions, combinedPct, status));
                }
            }
        }
        return summaries;
    }

    // =========================================================================
    // DELETE OPERATION
    // =========================================================================

    /**
     * Deletes an attendance record by primary key.
     */
    public boolean deleteAttendance(int attendanceId) throws SQLException {
        String sql = "DELETE FROM Attendance WHERE Attendance_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attendanceId);
            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // HELPER MAPPING METHOD
    // =========================================================================

    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        int id = rs.getInt("Attendance_ID");
        int studentId = rs.getInt("Student_ID");
        String courseCode = rs.getString("Course_code");
        Date sessionDate = rs.getDate("Session_date");
        String type = rs.getString("Type");

        LocalDate localDate = (sessionDate != null) ? sessionDate.toLocalDate() : null;
        return new Attendance(id, studentId, courseCode, localDate, type);
    }
}
