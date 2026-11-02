
package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import com.ftms.util.SessionManager;
import com.ftms.util.PasswordUtil;

import java.sql.*;
import java.util.Arrays;

public class LoginDAO {

    public String login(String username, String password) {

        String sql = "SELECT User_ID, Role, Password " +
                "FROM `user` WHERE Username=?";

        SessionManager.getInstance().logout();

        char[] input = password.toCharArray();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next() &&
                        PasswordUtil.verify(
                                input, rs.getString("Password"))) {

                    int userId = rs.getInt("User_ID");
                    String role = rs.getString("Role");

                    SessionManager.getInstance()
                            .setLoggedInUserId(userId);

                    return role;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Arrays.fill(input, '\0');
        }

        return null;
    }
}
