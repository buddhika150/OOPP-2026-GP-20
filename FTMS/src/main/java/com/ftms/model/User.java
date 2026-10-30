package com.ftms.model;

public abstract class User {

    protected int userId;
    protected String username;
    protected String password;
    protected String email;
    protected String role;

    public User(int userId, String username, String password,
            String email, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public abstract void viewDashboard();
}