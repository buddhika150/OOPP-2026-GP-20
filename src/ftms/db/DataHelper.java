package ftms.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper utility to populate dropdowns and UI lists from existing database tables.
 */
public class DataHelper {

    public static class StudentItem {
        private final int studentId;
        private final String tgNum;
        private final String fullName;

        public StudentItem(int studentId, String tgNum, String fullName) {
            this.studentId = studentId;
            this.tgNum = tgNum;
            this.fullName = fullName;
        }

        public int getStudentId() { return studentId; }
        public String getTgNum() { return tgNum; }
        public String getFullName() { return fullName; }

        @Override
        public String toString() {
            return tgNum + " - " + fullName;
        }
    }

    public static class CourseItem {
        private final String courseCode;
        private final String courseName;

        public CourseItem(String courseCode, String courseName) {
            this.courseCode = courseCode;
            this.courseName = courseName;
        }

        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }

        @Override
        public String toString() {
            return courseCode + " - " + courseName;
        }
    }

    public static class AttendanceItem {
        private final int attendanceId;
        private final String info;

        public AttendanceItem(int attendanceId, String info) {
            this.attendanceId = attendanceId;
            this.info = info;
        }

        public int getAttendanceId() { return attendanceId; }
        public String getInfo() { return info; }

        @Override
        public String toString() {
            return info;
        }
    }

    public static List<StudentItem> getAllStudents() {
        List<StudentItem> list = new ArrayList<>();
        String sql = "SELECT s.Student_ID, s.TG_num, u.Fname, u.Lname " +
                     "FROM Student s JOIN User u ON s.Student_ID = u.User_ID " +
                     "ORDER BY s.TG_num";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("Student_ID");
                String tg = rs.getString("TG_num");
                String fn = rs.getString("Fname");
                String ln = rs.getString("Lname");
                String name = ((fn != null ? fn : "") + " " + (ln != null ? ln : "")).trim();
                list.add(new StudentItem(id, tg, name));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students: " + e.getMessage());
        }
        return list;
    }

    public static List<CourseItem> getAllCourses() {
        List<CourseItem> list = new ArrayList<>();
        String sql = "SELECT Course_code, Course_name FROM Course ORDER BY Course_code";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new CourseItem(rs.getString("Course_code"), rs.getString("Course_name")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching courses: " + e.getMessage());
        }
        return list;
    }

    public static List<AttendanceItem> getAttendanceForStudent(int studentId) {
        List<AttendanceItem> list = new ArrayList<>();
        String sql = "SELECT Attendance_ID, Course_code, Session_date, Type " +
                     "FROM Attendance WHERE Student_ID = ? ORDER BY Session_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("Attendance_ID");
                    String desc = "ID: " + id + " | " + rs.getString("Course_code") + " | " +
                                  rs.getDate("Session_date") + " | " + rs.getString("Type");
                    list.add(new AttendanceItem(id, desc));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student attendance: " + e.getMessage());
        }
        return list;
    }
}
