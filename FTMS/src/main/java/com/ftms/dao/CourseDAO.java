
package com.ftms.dao;

import com.ftms.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<String[]> getCoursesByStudentId(int studentId) {

        List<String[]> courses = new ArrayList<>();

        String sql =
                "SELECT DISTINCT c.Course_code, c.Course_name " +
                        "FROM Enrollment e " +
                        "JOIN Course c ON e.Course_code = c.Course_code " +
                        "WHERE e.Student_ID = ? " +
                        "ORDER BY c.Course_code";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    String courseCode =
                            rs.getString("Course_code");

                    String courseName =
                            rs.getString("Course_name");

                    courses.add(new String[]{
                            courseCode,
                            courseName
                    });
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error loading student courses: " + e.getMessage()
            );

            e.printStackTrace();
        }

        return courses;
    }
}
