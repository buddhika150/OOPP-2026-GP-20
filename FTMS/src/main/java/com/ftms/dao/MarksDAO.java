package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import com.ftms.model.Marks;
import com.ftms.model.GPA;
import com.ftms.model.Undergraduate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarksDAO {

    public List<Marks> getMarksByStudentId(int studentId) {
        List<Marks> marksList = new ArrayList<>();
        String query = "SELECT m.Course_code, c.Course_name, m.CA_marks, m.Final_exam_marks, m.Total_marks, m.Grade " +
                "FROM Marks m JOIN Course c ON m.Course_code = c.Course_code WHERE m.Student_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                marksList.add(new Marks(
                        rs.getString("Course_code"),
                        rs.getString("Course_name"),
                        rs.getDouble("CA_marks"),
                        rs.getDouble("Final_exam_marks"),
                        rs.getDouble("Total_marks"),
                        rs.getString("Grade")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marksList;
    }

    public GPA getGPAByStudentId(int studentId) {
        GPA gpa = null;
        String query = "SELECT SGPA, CGPA, Semester FROM GPA WHERE Student_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                gpa = new GPA(rs.getDouble("SGPA"), rs.getDouble("CGPA"), rs.getInt("Semester"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gpa;
    }

    public Undergraduate getStudentDetails(int studentId) {
        Undergraduate student = null;
        String query = "SELECT s.Student_ID, s.TG_num, s.Batch, s.Department, s.NIC, s.Status, s.Year, s.Semester, " +
                "u.Fname, u.Lname, u.Email, u.Phone " +
                "FROM Student s JOIN `User` u ON s.Student_ID = u.User_ID WHERE s.Student_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                student = new Undergraduate(
                        rs.getInt("Student_ID"),
                        rs.getString("TG_num"),
                        rs.getString("Batch"),
                        rs.getString("Department"),
                        rs.getString("NIC"),
                        rs.getString("Status"),
                        rs.getInt("Year"),
                        rs.getInt("Semester"),
                        rs.getString("Fname"),
                        rs.getString("Lname"),
                        rs.getString("Email"),
                        rs.getString("Phone")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }
}