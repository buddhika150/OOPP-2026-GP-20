package com.ftms;

import com.ftms.dao.AttendanceDAO;
import com.ftms.dao.MedicalDAO;
import com.ftms.database.DatabaseConnection;
import com.ftms.model.Attendance;
import com.ftms.model.Medical;
import com.ftms.model.TechnicalOfficer;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        // 1. Test Database Connection
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Database connected: " + (conn != null));
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
            return;
        }

        // 2. Test AttendanceDAO
        AttendanceDAO attendanceDAO = new AttendanceDAO();
        Attendance attendance = new Attendance(11, "ICT2132", LocalDate.now(), "THEORY", true);
        boolean attSaved = attendanceDAO.addAttendance(attendance);
        System.out.println("Attendance saved: " + attSaved + " (ID: " + attendance.getAttendanceId() + ")");

        // 3. Test MedicalDAO
        MedicalDAO medicalDAO = new MedicalDAO();
        Medical medical = new Medical(11, null, LocalDate.now(), LocalDate.now(), LocalDate.now(), "Fever", "PENDING",
                "RECORDED");
        boolean medSaved = medicalDAO.addMedical(medical);
        System.out.println("Medical saved: " + medSaved + " (ID: " + medical.getMedicalId() + ")");

        // 4. Open Technical Officer Dashboard UI
        TechnicalOfficer to = new TechnicalOfficer(
                7, "to01", "to123", "to01@gmail.com", "TECHNICAL_OFFICER",
                "Ruwan", "Rathnayake", "0704121966", null, "ICT");
        to.viewDashboard();
    }
}
