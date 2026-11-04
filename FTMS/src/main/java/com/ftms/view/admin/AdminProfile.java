
package com.ftms.view.admin;

import com.ftms.database.DatabaseConnection;
import com.ftms.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.*;
import java.sql.*;

public class AdminProfile extends JFrame {

    private final Color NAVY = new Color(18, 44, 78);
    private final Color TEAL = new Color(21, 130, 153);
    private final Color BG = new Color(238, 243, 248);

    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtUsername;

    private JLabel photoLabel;
    private String photoPath;

    private final int adminId;

    public AdminProfile() {
        adminId = SessionManager.getInstance().getLoggedInUserId();

        setTitle("FTMS - Admin Profile");
        setSize(1100, 700);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if (adminId <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Please login as Admin first.");
            SwingUtilities.invokeLater(this::dispose);
            return;
        }

        buildUI();
        loadProfile();
    }

    private void buildUI() {

        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(25, 35, 25, 35));
        setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(NAVY);

        JButton back = createButton("Dashboard", NAVY);
        back.addActionListener(e -> {
            new AdminDashboard();
            dispose();
        });

        header.add(title, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout(25, 20));
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(30, 35, 30, 35));
        main.setPreferredSize(new Dimension(950, 480));

        JPanel photoPanel = new JPanel();
        photoPanel.setOpaque(false);
        photoPanel.setLayout(
                new BoxLayout(photoPanel, BoxLayout.Y_AXIS));

        photoLabel = new JLabel("No Photo", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(160, 160));
        photoLabel.setMinimumSize(new Dimension(160, 160));
        photoLabel.setMaximumSize(new Dimension(160, 160));
        photoLabel.setBorder(
                BorderFactory.createLineBorder(TEAL, 2));
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton upload = createButton("Change Photo", TEAL);
        upload.setAlignmentX(Component.CENTER_ALIGNMENT);
        upload.addActionListener(e -> uploadPhoto());

        JButton remove = createButton("Remove Photo", NAVY);
        remove.setAlignmentX(Component.CENTER_ALIGNMENT);
        remove.addActionListener(e -> removePhoto());

        photoPanel.add(photoLabel);
        photoPanel.add(Box.createVerticalStrut(18));
        photoPanel.add(upload);
        photoPanel.add(Box.createVerticalStrut(10));
        photoPanel.add(remove);

        main.add(photoPanel, BorderLayout.WEST);

        JPanel form = new JPanel(new GridLayout(0, 2, 18, 20));
        form.setOpaque(false);

        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtUsername = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();

        txtUsername.setEditable(false);
        txtUsername.setBackground(new Color(240, 243, 247));

        form.add(field("First Name", txtFirstName));
        form.add(field("Last Name", txtLastName));
        form.add(field("Username (Read Only)", txtUsername));
        form.add(field("Email", txtEmail));
        form.add(field("Phone Number", txtPhone));

        JLabel role = new JLabel("ADMIN");
        role.setForeground(TEAL);
        role.setFont(new Font("Arial", Font.BOLD, 16));
        form.add(field("Role (Read Only)", role));

        main.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        actions.setOpaque(false);

        JButton password = createButton("Change Password", NAVY);
        JButton save = createButton("Save Changes", TEAL);

        password.addActionListener(e -> changePassword());
        save.addActionListener(e -> saveProfile());

        actions.add(password);
        actions.add(save);

        main.add(actions, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(main);

        root.add(wrapper, BorderLayout.CENTER);
    }

    private JPanel field(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(NAVY);

        component.setFont(new Font("Arial", Font.PLAIN, 15));
        component.setPreferredSize(new Dimension(200, 42));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(155, 42));
        return button;
    }

    private void loadProfile() {
        String sql = "SELECT Username, Fname, Lname, Email, " +
                "Phone, Profile_pic FROM `user` " +
                "WHERE User_ID=? AND LOWER(Role)='admin'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, adminId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtUsername.setText(rs.getString("Username"));
                    txtFirstName.setText(rs.getString("Fname"));
                    txtLastName.setText(rs.getString("Lname"));
                    txtEmail.setText(rs.getString("Email"));
                    txtPhone.setText(rs.getString("Phone"));
                    photoPath = rs.getString("Profile_pic");
                    showPhoto();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Admin account not found.");
                }
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void saveProfile() {
        String first = txtFirstName.getText().trim();
        String last = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (first.isEmpty() || last.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "First name and last name are required.");
            return;
        }

        if (!email.isEmpty() &&
                !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid email address.");
            return;
        }

        String sql = "UPDATE `user` SET Fname=?, Lname=?, " +
                "Email=?, Phone=? WHERE User_ID=? " +
                "AND LOWER(Role)='admin'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, first);
            ps.setString(2, last);
            ps.setString(3, email.isEmpty() ? null : email);
            ps.setString(4, phone);
            ps.setInt(5, adminId);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this,
                        "Profile updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Profile was not updated.");
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void uploadPhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Profile Photo");

        if (chooser.showOpenDialog(this) !=
                JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        String name = file.getName().toLowerCase();

        if (!(name.endsWith(".jpg") ||
                name.endsWith(".jpeg") ||
                name.endsWith(".png"))) {
            JOptionPane.showMessageDialog(this,
                    "Only JPG and PNG photos are allowed.");
            return;
        }

        if (file.length() > 2 * 1024 * 1024) {
            JOptionPane.showMessageDialog(this,
                    "Photo must be smaller than 2MB.");
            return;
        }

        try {
            java.awt.image.BufferedImage image =
                    javax.imageio.ImageIO.read(file);

            if (image == null) {
                JOptionPane.showMessageDialog(this,
                        "Invalid image file.");
                return;
            }

            Path folder = Paths.get("uploads", "admin_profiles");
            Files.createDirectories(folder);

            String extension = name.endsWith(".png")
                    ? ".png" : ".jpg";

            Path target = folder.resolve(
                    "admin_" + adminId + "_" +
                            System.currentTimeMillis() + extension);

            String format = extension.equals(".png") ? "png" : "jpg";

            if (!javax.imageio.ImageIO.write(
                    image, format, target.toFile())) {
                throw new java.io.IOException("Cannot save image.");
            }

            String sql = "UPDATE `user` SET Profile_pic=? " +
                    "WHERE User_ID=? AND LOWER(Role)='admin'";

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, target.toString());
                ps.setInt(2, adminId);

                if (ps.executeUpdate() == 0) {
                    Files.deleteIfExists(target);
                    throw new SQLException("Admin account not found.");
                }
            }

            photoPath = target.toString();
            showPhoto();

            JOptionPane.showMessageDialog(this,
                    "Photo updated successfully!");

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void removePhoto() {
        String sql = "UPDATE `user` SET Profile_pic=NULL " +
                "WHERE User_ID=? AND LOWER(Role)='admin'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, adminId);

            if (ps.executeUpdate() > 0) {
                photoPath = null;
                showPhoto();
                JOptionPane.showMessageDialog(this,
                        "Photo removed successfully!");
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showPhoto() {
        photoLabel.setIcon(null);
        photoLabel.setText("No Photo");

        if (photoPath == null || photoPath.isBlank()) {
            return;
        }

        File file = new File(photoPath);

        if (!file.isFile()) {
            return;
        }

        ImageIcon original = new ImageIcon(photoPath);

        if (original.getIconWidth() <= 0) {
            return;
        }

        Image scaled = original.getImage().getScaledInstance(
                150, 150, Image.SCALE_SMOOTH);

        photoLabel.setIcon(new ImageIcon(scaled));
        photoLabel.setText("");
    }

    private void changePassword() {
        JPasswordField current = new JPasswordField();
        JPasswordField next = new JPasswordField();
        JPasswordField confirm = new JPasswordField();

        Object[] fields = {
                "Current Password:", current,
                "New Password:", next,
                "Confirm Password:", confirm
        };

        int result = JOptionPane.showConfirmDialog(
                this, fields, "Change Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        char[] oldPassword = current.getPassword();
        char[] newPassword = next.getPassword();
        char[] confirmPassword = confirm.getPassword();

        try {
            if (newPassword.length < 8 ||
                    !java.util.Arrays.equals(
                            newPassword, confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Password must have at least 8 characters " +
                                "and both new passwords must match.");
                return;
            }

            String sql = "SELECT Password FROM `user` " +
                    "WHERE User_ID=? AND LOWER(Role)='admin'";

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, adminId);

                String stored;

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Admin not found.");
                    }
                    stored = rs.getString("Password");
                }

                if (!com.ftms.util.PasswordUtil.verify(
                        oldPassword, stored)) {
                    JOptionPane.showMessageDialog(this,
                            "Current password is incorrect.");
                    return;
                }

                String hashed =
                        com.ftms.util.PasswordUtil.hash(newPassword);

                try (PreparedStatement update = con.prepareStatement(
                        "UPDATE `user` SET Password=? " +
                                "WHERE User_ID=? AND LOWER(Role)='admin'")) {

                    update.setString(1, hashed);
                    update.setInt(2, adminId);
                    update.executeUpdate();
                }

                JOptionPane.showMessageDialog(this,
                        "Password changed successfully!");
            }

        } catch (Exception ex) {
            showError(ex);
        } finally {
            java.util.Arrays.fill(oldPassword, '\0');
            java.util.Arrays.fill(newPassword, '\0');
            java.util.Arrays.fill(confirmPassword, '\0');
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(
                this, ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
