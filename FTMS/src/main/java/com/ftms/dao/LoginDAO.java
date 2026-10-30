
package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import java.sql.*;

public class LoginDAO {

    public String login(String username, String password) {

        try {
            Connection con = DatabaseConnection.getConnection();

            String sql = "SELECT * FROM User WHERE Username=? AND Password=?";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("Role");
                con.close();
                return role;
            }

            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }
}
