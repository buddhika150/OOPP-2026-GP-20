
package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import java.sql.*;

public class StudentProfileDAO {

    public boolean updateProfile(int studentId, String email,
                                 String phone, String profilePic)
            throws SQLException {

        String sql = "UPDATE `User` SET Email = ?, Phone = ?, " +
                "Profile_pic = ? WHERE User_ID = ? " +
                "AND LOWER(Role) = 'student'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, email);
            pst.setString(2, phone);
            pst.setString(3, profilePic);
            pst.setInt(4, studentId);

            int rows = pst.executeUpdate();

            System.out.println("Profile updated rows: " + rows);

            return rows > 0;
        }
    }
}
