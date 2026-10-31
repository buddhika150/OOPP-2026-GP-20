
package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import com.ftms.util.SessionManager;
import java.sql.*;

public class LoginDAO {

    public String login(String username, String password) {

        String sql = "SELECT User_ID, Role FROM `User` " +
                "WHERE Username = ? AND Password = ?";

        SessionManager.getInstance().logout();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, password);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    int userId = rs.getInt("User_ID");
                    String role = rs.getString("Role");

                    SessionManager.getInstance()
                            .setLoggedInUserId(userId);

                    return role;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
