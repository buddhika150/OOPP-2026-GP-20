package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class CourseDAO {

    public List<Object[]> getAllCourses() throws SQLException {

        List<Object[]> courses = new ArrayList<>();

        String sql = "SELECT * FROM course ORDER BY Course_code";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                courses.add(new Object[]{
                        rs.getString("Course_code"),
                        rs.getString("Course_name"),
                        rs.getInt("Academic_year"),
                        rs.getInt("Semester"),
                        rs.getInt("Theory_credits"),
                        rs.getInt("Practical_credits"),
                        rs.getInt("Theory_hours"),
                        rs.getInt("Practical_hours"),
                        rs.getString("Department")
                });
            }
        }

        return courses;
    }

    public void addCourse(String code, String name, int year,
                          int semester, int tc, int pc,
                          int th, int ph, String department)
            throws SQLException {

        String sql = "INSERT INTO course " +
                "(Course_code, Course_name, Academic_year, Semester, " +
                "Theory_credits, Practical_credits, Theory_hours, " +
                "Practical_hours, Department) VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, name);
            ps.setInt(3, year);
            ps.setInt(4, semester);
            ps.setInt(5, tc);
            ps.setInt(6, pc);
            ps.setInt(7, th);
            ps.setInt(8, ph);
            ps.setString(9, department);

            ps.executeUpdate();
        }
    }

    public void updateCourse(String code, String name, int year,
                             int semester, int tc, int pc,
                             int th, int ph, String department)
            throws SQLException {

        String sql = "UPDATE course SET Course_name=?, Academic_year=?, " +
                "Semester=?, Theory_credits=?, Practical_credits=?, " +
                "Theory_hours=?, Practical_hours=?, Department=? " +
                "WHERE Course_code=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, year);
            ps.setInt(3, semester);
            ps.setInt(4, tc);
            ps.setInt(5, pc);
            ps.setInt(6, th);
            ps.setInt(7, ph);
            ps.setString(8, department);
            ps.setString(9, code);

            ps.executeUpdate();
        }
    }

    public void deleteCourse(String code) throws SQLException {

        String sql = "DELETE FROM course WHERE Course_code=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.executeUpdate();
        }
    }

    // Get courses by student ID
    public List<String[]> getCoursesByStudentId(int studentId) {

        List<String[]> courses = new ArrayList<>();

        String sql = "SELECT DISTINCT c.Course_code, c.Course_name " +
                "FROM Enrollment e " +
                "JOIN Course c ON e.Course_code = c.Course_code " +
                "WHERE e.Student_ID = ? " +
                "ORDER BY c.Course_code";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    courses.add(new String[]{
                            rs.getString("Course_code"),
                            rs.getString("Course_name")
                    });
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading student courses: "
                    + e.getMessage());
            e.printStackTrace();
        }

        return courses;
    }

    // Get courses by department
    public List<String> getCourseCodesByDepartment(String department)
            throws SQLException {

        List<String> courses = new ArrayList<>();

        String sql = "SELECT Course_code FROM course " +
                "WHERE Department = ? ORDER BY Course_code";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, department);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    courses.add(rs.getString("Course_code"));
                }
            }
        }

        return courses;
    }

    // Get lecturers by department
    public List<String> getLecturersByDepartment(String department)
            throws SQLException {

        List<String> lecturers = new ArrayList<>();

        String sql = "SELECT Lecturer_ID FROM lecturer " +
                "WHERE Department = ? ORDER BY Lecturer_ID";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, department);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lecturers.add(rs.getString("Lecturer_ID"));
                }
            }
        }

        return lecturers;
    }

    // Assign lecturer to course
    public void assignLecturerToCourse(int lecturerId, String courseCode)
            throws SQLException {

        String sql = "INSERT INTO lecturer_course " +
                "(Lecturer_ID, Course_code) VALUES (?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, lecturerId);
            ps.setString(2, courseCode);

            ps.executeUpdate();
        }
    }
}