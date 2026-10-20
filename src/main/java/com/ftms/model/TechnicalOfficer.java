package com.ftms.model;

import java.util.Objects;

/**
 * Model class representing a Technical Officer.
 * Maps directly to the Technical_Officer table in db.sql:
 * Technical_Officer(TO_ID, Department).
 * Extends the abstract User class.
 *
 * Demonstrates:
 * - Classes and Objects: Models the Technical Officer domain entity with specialized state.
 * - Inheritance: Subclass of abstract class User, reusing inherited properties.
 * - Abstraction: Fulfills abstract contracts defined in User.
 * - Polymorphism: Overrides getRoleName() and displayDashboard() for Technical Officer-specific behavior.
 * - Encapsulation: Private department field with validated getters and setters.
 * - Database Compatibility: Maps to Technical_Officer(TO_ID, Department).
 */
public class TechnicalOfficer extends User {

    public static final String ROLE_NAME = "TECHNICAL_OFFICER";

    private String department;

    /**
     * Default constructor.
     */
    public TechnicalOfficer() {
        super();
        setRole(ROLE_NAME);
    }

    /**
     * Constructor initializing Technical Officer with department and user ID.
     *
     * @param userId     the primary key User_ID / TO_ID
     * @param department the department assigned (e.g. ICT)
     */
    public TechnicalOfficer(int userId, String department) {
        super();
        setUserId(userId);
        setRole(ROLE_NAME);
        setDepartment(department);
    }

    /**
     * Full parameterized constructor initializing all inherited User attributes and department.
     *
     * @param userId     the primary key User_ID / TO_ID
     * @param username   the unique username
     * @param password   the user password
     * @param email      the user email
     * @param profilePic the profile picture path/filename
     * @param phone      the contact phone number
     * @param fname      the first name
     * @param lname      the last name
     * @param department the department of the Technical Officer
     */
    public TechnicalOfficer(int userId, String username, String password, String email,
                            String profilePic, String phone, String fname, String lname, String department) {
        super(userId, username, password, ROLE_NAME, email, profilePic, phone, fname, lname);
        setDepartment(department);
    }

    // ================= Getters and Setters (Encapsulation) =================

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be null or empty.");
        }
        this.department = department.trim();
    }

    // ================= Polymorphism (Overriding User methods) =================

    /**
     * Returns the human-readable display role name.
     */
    @Override
    public String getRoleName() {
        return "Technical Officer";
    }

    /**
     * Displays the Technical Officer Dashboard GUI.
     */
    @Override
    public void displayDashboard() {
        // Concrete dashboard view launch hook for Member 3 GUI
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TechnicalOfficer that = (TechnicalOfficer) o;
        return Objects.equals(department, that.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), department);
    }

    @Override
    public String toString() {
        return "TechnicalOfficer{" +
                "userId=" + getUserId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
