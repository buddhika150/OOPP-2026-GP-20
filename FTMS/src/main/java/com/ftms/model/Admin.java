package com.ftms.model;

public class Admin extends User {

    public Admin(int userId, String username,
                 String password, String email, String role) {

        super(userId, username, password, email, role);
    }

    @Override
    public void viewDashboard() {
        System.out.println("Admin Dashboard");
    }
}