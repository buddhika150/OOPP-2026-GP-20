
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

    public void addUser(String username, String password,
                        String fname, String lname, String role,
                        String email, String phone) throws SQLException {

        String sql = "INSERT INTO `user` " +
                "(Username, Password, Fname, Lname, Role, Email, Phone) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fname);
            ps.setString(4, lname);
            ps.setString(5, role);
            ps.setString(6, email.isEmpty() ? null : email);
            ps.setString(7, phone);
            ps.executeUpdate();
        }
    }

    public void updateUser(int id, String username, String fname,
                           String lname, String role, String email,
                           String phone, String password) throws SQLException {

        String sql = "UPDATE `user` SET Username=?, Fname=?, " +
                "Lname=?, Role=?, Email=?, Phone=? " +
                (password.isEmpty() ? "" : ", Password=? ") +
                "WHERE User_ID=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

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
