package ftms.gui;

import ftms.model.TechnicalOfficer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Java Swing GUI Panel for Technical Officer Profile management.
 * Complies with SRS 2.3 & Section 3:
 * "Update their profile except username and password."
 */
public class ProfilePanel extends JPanel {
    private final TechnicalOfficer technicalOfficer;

    private JTextField txtUserId;
    private JTextField txtUsername;
    private JTextField txtRole;
    private JTextField txtDepartment;

    private JTextField txtFname;
    private JTextField txtLname;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtProfilePic;

    public ProfilePanel(TechnicalOfficer technicalOfficer) {
        this.technicalOfficer = technicalOfficer;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadProfileData();
    }

    private void initComponents() {
        // TOP Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("My Profile");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel lblSub = new JLabel("View and update personal details. (Username and role cannot be modified)");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSub.setForeground(Color.GRAY);
        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        pnlHeader.add(lblSub, BorderLayout.SOUTH);
        add(pnlHeader, BorderLayout.NORTH);

        // Form in Center
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Profile Information",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // User ID (Disabled)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        pnlForm.add(new JLabel("User ID:"), gbc);
        txtUserId = new JTextField();
        txtUserId.setEditable(false);
        txtUserId.setBackground(new Color(245, 245, 245));
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.8;
        pnlForm.add(txtUserId, gbc);

        // Username (Disabled - SRS Constraint)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        pnlForm.add(new JLabel("Username (Read-only):"), gbc);
        txtUsername = new JTextField();
        txtUsername.setEditable(false);
        txtUsername.setBackground(new Color(245, 245, 245));
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.8;
        pnlForm.add(txtUsername, gbc);

        // Role & Department (Disabled)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        pnlForm.add(new JLabel("Role:"), gbc);
        txtRole = new JTextField();
        txtRole.setEditable(false);
        txtRole.setBackground(new Color(245, 245, 245));
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.8;
        pnlForm.add(txtRole, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        pnlForm.add(new JLabel("Department:"), gbc);
        txtDepartment = new JTextField();
        txtDepartment.setEditable(false);
        txtDepartment.setBackground(new Color(245, 245, 245));
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.8;
        pnlForm.add(txtDepartment, gbc);

        // First Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        pnlForm.add(new JLabel("First Name: *"), gbc);
        txtFname = new JTextField();
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.8;
        pnlForm.add(txtFname, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        pnlForm.add(new JLabel("Last Name: *"), gbc);
        txtLname = new JTextField();
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.8;
        pnlForm.add(txtLname, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        pnlForm.add(new JLabel("Email Address: *"), gbc);
        txtEmail = new JTextField();
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.8;
        pnlForm.add(txtEmail, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        pnlForm.add(new JLabel("Phone Number:"), gbc);
        txtPhone = new JTextField();
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.8;
        pnlForm.add(txtPhone, gbc);

        // Profile Picture
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        pnlForm.add(new JLabel("Profile Picture Path:"), gbc);
        txtProfilePic = new JTextField();
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.8;
        pnlForm.add(txtProfilePic, gbc);

        // Buttons
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnSave = new JButton("Save Profile Changes");
        btnSave.setBackground(new Color(0, 123, 255));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);

        JButton btnReset = new JButton("Reset");
        btnReset.setFocusPainted(false);

        pnlBtn.add(btnSave);
        pnlBtn.add(btnReset);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 12, 10, 12);
        pnlForm.add(pnlBtn, gbc);

        JPanel pnlWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlWrapper.add(pnlForm);
        add(pnlWrapper, BorderLayout.CENTER);

        // Listeners
        btnSave.addActionListener(e -> onSaveProfile());
        btnReset.addActionListener(e -> loadProfileData());
    }

    private void loadProfileData() {
        if (technicalOfficer != null) {
            txtUserId.setText(String.valueOf(technicalOfficer.getUserId()));
            txtUsername.setText(technicalOfficer.getUsername());
            txtRole.setText(technicalOfficer.getRoleName());
            txtDepartment.setText(technicalOfficer.getDepartment());
            txtFname.setText(technicalOfficer.getFname());
            txtLname.setText(technicalOfficer.getLname());
            txtEmail.setText(technicalOfficer.getEmail());
            txtPhone.setText(technicalOfficer.getPhone());
            txtProfilePic.setText(technicalOfficer.getProfilePic());
        }
    }

    private void onSaveProfile() {
        String fname = txtFname.getText().trim();
        String lname = txtLname.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String pic = txtProfilePic.getText().trim();

        if (fname.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name and Email are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = technicalOfficer.updateProfile(fname, lname, email, phone, pic);
            if (success) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
