
package com.ftms.dao;

import com.ftms.database.DatabaseConnection;
import java.sql.*;

public class NoticeDAO {

    public ResultSet getNotices() throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT Notice_ID, Title, Content, Published_date " +
                        "FROM notice ORDER BY Notice_ID DESC"
        );
        return ps.executeQuery();
    }

    public boolean addNotice(String title, String content)
            throws SQLException {

        String sql = "INSERT INTO notice " +
                "(Admin_ID, Title, Content, Published_date) " +
                "VALUES (?, ?, ?, CURDATE())";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, 1);
            ps.setString(2, title);
            ps.setString(3, content);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateNotice(int id, String title, String content)
            throws SQLException {

        String sql = "UPDATE notice SET Title=?, Content=? " +
                "WHERE Notice_ID=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, content);
            ps.setInt(3, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteNotice(int id) throws SQLException {

        String sql = "DELETE FROM notice WHERE Notice_ID=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}
