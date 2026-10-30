
package com.ftms.database;

import java.sql.Connection;

public class DatabaseTest {

    public static void main(String[] args) {

        try (Connection con = DatabaseConnection.getConnection()) {

            System.out.println(
                    "Database Connected Successfully!"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
