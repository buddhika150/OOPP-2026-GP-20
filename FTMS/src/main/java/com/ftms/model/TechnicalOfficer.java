package com.ftms.model;

import com.ftms.view.technicalofficer.TechnicalOfficerDashboard;
import javax.swing.SwingUtilities;

/**
 * Model class representing a Technical Officer in the FTMS system.
 * Extends the abstract User class and maps to the technical_officer table (TO_ID, Department).
 */
public class TechnicalOfficer extends User {

    // Technical Officer-specific attribute from the technical_officer table
    private String department;

    // Additional profile attributes from the user table (not declared in User.java)
    private String fname;
    private String lname;
    private String phone;
    private String profilePic;

    /**
     * Constructor calling the parent User constructor.
     */
    public TechnicalOfficer(int userId, String username, String password,
                            String email, String role) {
        super(userId, username, password, email, role);
    }

    /**
     * Constructor initializing User fields and Technical Officer department.
     */
    public TechnicalOfficer(int userId, String username, String password,
                            String email, String role, String department) {
        super(userId, username, password, email, role);
        setDepartment(department);
    }

    /**
     * Full constructor initializing User fields, profile details, and department.
     */
    public TechnicalOfficer(int userId, String username, String password,
                            String email, String role, String fname, String lname,
                            String phone, String profilePic, String department) {
        super(userId, username, password, email, role);
        setFname(fname);
        setLname(lname);
        setPhone(phone);
        setProfilePic(profilePic);
        setDepartment(department);
    }

    public int getToId() {
        return userId;
    }

    public void setToId(int toId) {
        if (toId <= 0) {
            throw new IllegalArgumentException("TO_ID must be a positive integer.");
        }
        this.userId = toId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (department != null && department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty.");
        }
        this.department = (department != null) ? department.trim() : null;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = (fname != null && !fname.trim().isEmpty()) ? fname.trim() : null;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = (lname != null && !lname.trim().isEmpty()) ? lname.trim() : null;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = (phone != null && !phone.trim().isEmpty()) ? phone.trim() : null;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = (profilePic != null && !profilePic.trim().isEmpty()) ? profilePic.trim() : null;
    }

    public void setEmail(String email) {
        if (email != null && email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        this.email = (email != null) ? email.trim() : null;
    }

    @Override
    public void viewDashboard() {
        System.out.println("Opening Technical Officer Dashboard for user: " + username);
        SwingUtilities.invokeLater(() -> new TechnicalOfficerDashboard(this).setVisible(true));
    }

    @Override
    public String toString() {
        return "TechnicalOfficer{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}

