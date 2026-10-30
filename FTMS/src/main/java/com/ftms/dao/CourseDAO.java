
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
                        rs.getInt("Practical_hours")
                });
            }
        }
        return courses;
    }

    public void addCourse(String code, String name, int year,
                          int semester, int tc, int pc,
                          int th, int ph) throws SQLException {

        String sql = "INSERT INTO course VALUES (?,?,?,?,?,?,?,?)";

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
            ps.executeUpdate();
        }
    }

    public void updateCourse(String code, String name, int year,
                             int semester, int tc, int pc,
                             int th, int ph) throws SQLException {

        String sql = "UPDATE course SET Course_name=?, Academic_year=?, " +
                "Semester=?, Theory_credits=?, Practical_credits=?, " +
                "Theory_hours=?, Practical_hours=? WHERE Course_code=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, year);
            ps.setInt(3, semester);
            ps.setInt(4, tc);
            ps.setInt(5, pc);
            ps.setInt(6, th);
            ps.setInt(7, ph);
            ps.setString(8, code);
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
}
