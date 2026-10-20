package com.ftms.model;

import com.ftms.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Abstract Base Class representing a User in the Faculty of Technology Management System (FTMS).
 * Maps directly to the User table in db.sql:
 * User(User_ID, Username, Password, Role, Email, Profile_pic, Phone, Fname, Lname).
 *
 * Demonstrates OOP Principles:
 * - Abstraction: Abstract methods getRoleName() and displayDashboard() to be fulfilled by concrete user subclasses.
 * - Encapsulation: Private member variables with controlled getters and setters.
 * - Inheritance: Superclass for TechnicalOfficer, Lecturer, Admin, and Student.
 */
public abstract class User {

    private int userId;
    private String username;
    private String password;
    private String role;
    private String email;
    private String profilePic;
    private String phone;
    private String fname;
    private String lname;

    /**
     * Default constructor.
     */
    public User() {
    }

    /**
     * Full parameterized constructor initializing all User attributes.
     */
    public User(int userId, String username, String password, String role, String email,
                String profilePic, String phone, String fname, String lname) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.profilePic = profilePic;
        this.phone = phone;
        this.fname = fname;
        this.lname = lname;
    }

    // ================= Abstract Methods (Abstraction & Polymorphism) =================

    /**
     * Returns the human-readable display role name.
     */
    public abstract String getRoleName();

    /**
     * Displays the dashboard GUI specific to this role.
     */
    public abstract void displayDashboard();

    // ================= Business & Profile Logic =================

    /**
     * Updates user profile attributes (email, phone, fname, lname, profilePic) in the database.
     * Enforces the SRS constraint: Users cannot update their username and password via profile update.
     */
    public boolean updateProfile(String fname, String lname, String email, String phone, String profilePic) throws SQLException {
        String sql = "UPDATE User SET Fname = ?, Lname = ?, Email = ?, Phone = ?, Profile_pic = ? WHERE User_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fname);
            stmt.setString(2, lname);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, profilePic);
            stmt.setInt(6, this.userId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                this.fname = fname;
                this.lname = lname;
                this.email = email;
                this.phone = phone;
                this.profilePic = profilePic;
                return true;
            }
        }
        return false;
    }

    // ================= Getters and Setters (Encapsulation) =================

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getFullName() {
        return ((fname != null ? fname : "") + " " + (lname != null ? lname : "")).trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + getFullName() + '\'' +
                '}';
    }
}
