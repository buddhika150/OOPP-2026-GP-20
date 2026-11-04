
package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class UserDAO {

    public List<Object[]> getAllUsers() throws SQLException {
        List<Object[]> users = new ArrayList<>();

        String sql = "SELECT * FROM `user`";

        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String first = rs.getString("Fname");
                String last = rs.getString("Lname");

                String name = ((first == null ? "" : first) + " " +
                        (last == null ? "" : last)).trim();

                users.add(new Object[]{
                        rs.getInt("User_ID"),
                        name,
                        rs.getString("Username"),
                        rs.getString("Role"),
                        rs.getString("Email"),
                        rs.getString("Phone")
                });
            }
        }
        return users;
    }



    public String getDepartment(int userId, String role) throws SQLException {
        String table = departmentTable(role);
        if (table == null) return "";

        String idColumn = role.equalsIgnoreCase("STUDENT") ? "Student_ID"
                : role.equalsIgnoreCase("LECTURER") ? "Lecturer_ID" : "TO_ID";

        String sql = "SELECT Department FROM " + table +
                " WHERE " + idColumn + " = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("Department") : "";
            }
        }
    }

    private String departmentTable(String role) {
        switch (role.toUpperCase()) {
            case "STUDENT": return "student";
            case "LECTURER": return "lecturer";
            case "TECHNICAL_OFFICER": return "technical_officer";
            default: return null;
        }
    }

    public void addUser(String username, String password,
                        String fname, String lname, String role,
                        String email, String phone,
                        String department) throws SQLException {

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                String sql = "INSERT INTO `user` " +
                        "(Username, Password, Fname, Lname, Role, Email, Phone) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

                int userId;

                try (PreparedStatement ps = con.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, fname);
                    ps.setString(4, lname);
                    ps.setString(5, role);
                    ps.setString(6, email.isEmpty() ? null : email);
                    ps.setString(7, phone);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("User ID not generated");
                        }
                        userId = keys.getInt(1);
                    }
                }

                String table = departmentTable(role);

                if (table != null) {
                    String idColumn = role.equalsIgnoreCase("STUDENT")
                            ? "Student_ID"
                            : role.equalsIgnoreCase("LECTURER")
                            ? "Lecturer_ID" : "TO_ID";

                    String insert = "INSERT INTO " + table +
                            " (" + idColumn + ", Department) VALUES (?, ?)";

                    try (PreparedStatement ps = con.prepareStatement(insert)) {
                        ps.setInt(1, userId);
                        ps.setString(2, department);
                        ps.executeUpdate();
                    }

                } else if (role.equalsIgnoreCase("ADMIN")) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO admin (Admin_ID) VALUES (?)")) {
                        ps.setInt(1, userId);
                        ps.executeUpdate();
                    }
                } else {
                    throw new SQLException("Invalid user role: " + role);
                }

                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void updateUser(int id, String username, String fname,
                           String lname, String role, String email,
                           String phone, String password,
                           String department) throws SQLException {

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                String existingRole;

                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT Role FROM `user` WHERE User_ID=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("User not found");
                        }
                        existingRole = rs.getString("Role");
                    }
                }

                if (!existingRole.equalsIgnoreCase(role)) {
                    throw new SQLException(
                            "Changing an existing user's role is not supported");
                }

                String sql = "UPDATE `user` SET Username=?, Fname=?, " +
                        "Lname=?, Role=?, Email=?, Phone=?" +
                        (password.isEmpty() ? "" : ", Password=?") +
                        " WHERE User_ID=?";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, username);
                    ps.setString(2, fname);
                    ps.setString(3, lname);
                    ps.setString(4, role);
                    ps.setString(5, email.isEmpty() ? null : email);
                    ps.setString(6, phone);

                    if (password.isEmpty()) {
                        ps.setInt(7, id);
                    } else {
                        ps.setString(7, password);
                        ps.setInt(8, id);
                    }

                    ps.executeUpdate();
                }

                String table = departmentTable(role);

                if (table != null) {
                    String idColumn = role.equalsIgnoreCase("STUDENT")
                            ? "Student_ID"
                            : role.equalsIgnoreCase("LECTURER")
                            ? "Lecturer_ID" : "TO_ID";

                    String update = "UPDATE " + table +
                            " SET Department=? WHERE " + idColumn + "=?";

                    try (PreparedStatement ps = con.prepareStatement(update)) {
                        ps.setString(1, department);
                        ps.setInt(2, id);

                        if (ps.executeUpdate() == 0) {
                            throw new SQLException(
                                    "Department profile not found for user " + id);
                        }
                    }
                }

                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }


    public void deleteUser(int id) throws SQLException {

        String sql = "DELETE FROM `user` WHERE User_ID=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
