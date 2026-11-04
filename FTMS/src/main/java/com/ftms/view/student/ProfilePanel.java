
package com.ftms.view.student;

import com.ftms.dao.StudentProfileDAO;
import com.ftms.model.Undergraduate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

public class ProfilePanel extends JPanel {

    private Undergraduate currentStudent;
    private JTextField txtEmail, txtPhone;
    private JLabel photoLabel;
    private File selectedPhoto;

    public ProfilePanel(Undergraduate currentStudent) {

        this.currentStudent = currentStudent;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("My Profile Details");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // Main profile card
        JPanel profileContent = new JPanel(new BorderLayout(10, 15));
        profileContent.setBackground(Color.WHITE);
        profileContent.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Profile Photo Section
        JPanel photoSection = new JPanel();
        photoSection.setLayout(new BoxLayout(photoSection, BoxLayout.Y_AXIS));
        photoSection.setBackground(Color.WHITE);

        photoLabel = new JLabel("No Photo", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(130, 130));
        photoLabel.setMinimumSize(new Dimension(130, 130));
        photoLabel.setMaximumSize(new Dimension(130, 130));
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        photoLabel.setBorder(BorderFactory.createLineBorder(
                new Color(220, 225, 230)));

        JButton btnPhoto = new JButton("Choose Photo");
        btnPhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPhoto.setFocusPainted(false);
        btnPhoto.addActionListener(e -> choosePhoto());

        photoSection.add(photoLabel);
        photoSection.add(Box.createVerticalStrut(10));
        photoSection.add(btnPhoto);

        profileContent.add(photoSection, BorderLayout.NORTH);

        // Student details
        JPanel card = new JPanel(new GridLayout(0, 2, 15, 10));
        card.setBackground(Color.WHITE);

        addRow(card, "Full Name:",
                currentStudent.getFname() + " " + currentStudent.getLname());

        addRow(card, "TG Number:", currentStudent.getTgNum());
        addRow(card, "Department:", currentStudent.getDepartment());
        addRow(card, "Batch:", currentStudent.getBatch());
        addRow(card, "NIC:", currentStudent.getNic());
        addRow(card, "Status:", currentStudent.getStatus());
        addRow(card, "Year:", String.valueOf(currentStudent.getYear()));
        addRow(card, "Semester:", String.valueOf(currentStudent.getSemester()));

        // Editable Email
        card.add(new JLabel("Email:"));
        txtEmail = new JTextField(currentStudent.getEmail());
        card.add(txtEmail);

        // Editable Phone
        card.add(new JLabel("Phone:"));
        txtPhone = new JTextField(currentStudent.getPhone());
        card.add(txtPhone);

        // Save button
        JButton btnSave = new JButton("Save Changes");
        btnSave.setBackground(new Color(35, 110, 190));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> saveProfile());

        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        savePanel.setBackground(Color.WHITE);
        savePanel.add(btnSave);

        card.add(new JLabel(""));
        card.add(savePanel);

        profileContent.add(card, BorderLayout.CENTER);

        // Keep profile compact
        profileContent.setPreferredSize(new Dimension(750, 720));

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setBackground(new Color(240, 242, 245));
        wrapper.add(profileContent);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        showPhoto(currentStudent.getProfilePic());
    }

    private void addRow(JPanel panel, String label, String value) {
        panel.add(new JLabel(label));
        panel.add(new JLabel(value == null ? "-" : value));
    }

    // Choose Photo
    private void choosePhoto() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Profile Photo");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            File file = chooser.getSelectedFile();
            String name = file.getName().toLowerCase();

            if (!name.endsWith(".jpg") &&
                    !name.endsWith(".jpeg") &&
                    !name.endsWith(".png")) {

                JOptionPane.showMessageDialog(this,
                        "Please select JPG or PNG image.");
                return;
            }

            if (file.length() > 2 * 1024 * 1024) {
                JOptionPane.showMessageDialog(this,
                        "Photo must be less than 2MB.");
                return;
            }

            selectedPhoto = file;
            showPhoto(file.getAbsolutePath());
        }
    }

    // Display full photo without cropping
    private void showPhoto(String path) {

        if (path == null || !new File(path).isFile()) {
            photoLabel.setIcon(null);
            photoLabel.setText("No Photo");
            return;
        }

        ImageIcon icon = new ImageIcon(path);

        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        if (width <= 0 || height <= 0) {
            photoLabel.setIcon(null);
            photoLabel.setText("Invalid Photo");
            return;
        }

        double scale = Math.min(125.0 / width, 125.0 / height);

        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);

        Image image = icon.getImage().getScaledInstance(
                newWidth, newHeight, Image.SCALE_SMOOTH);

        photoLabel.setIcon(new ImageIcon(image));
        photoLabel.setText("");
    }

    // Save Profile
    private void saveProfile() {

        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            JOptionPane.showMessageDialog(this, "Invalid Email!");
            return;
        }

        String photoPath = currentStudent.getProfilePic();

        try {

            if (selectedPhoto != null) {

                File folder = new File("uploads/profile");

                if (!folder.exists() && !folder.mkdirs()) {
                    throw new IOException("Cannot create photo folder");
                }

                String name = selectedPhoto.getName().toLowerCase();
                String extension = name.substring(name.lastIndexOf("."));

                File destination = new File(folder,
                        "student_" + currentStudent.getStudentId()
                                + "_" + System.currentTimeMillis()
                                + extension);

                Files.copy(selectedPhoto.toPath(), destination.toPath());

                photoPath = destination.getAbsolutePath();
            }

            StudentProfileDAO dao = new StudentProfileDAO();

            boolean saved = dao.updateProfile(
                    currentStudent.getStudentId(),
                    email,
                    phone,
                    photoPath
            );

            if (saved) {

                currentStudent.setEmail(email);
                currentStudent.setPhone(phone);
                currentStudent.setProfilePic(photoPath);

                selectedPhoto = null;
                showPhoto(photoPath);

                JOptionPane.showMessageDialog(this,
                        "Profile Updated Successfully!");

            } else {
                JOptionPane.showMessageDialog(this, "Update Failed!");
            }

        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage());
        }
    }

    // Refresh Profile
    public void updateProfileData() {

        txtEmail.setText(currentStudent.getEmail());
        txtPhone.setText(currentStudent.getPhone());

        if (selectedPhoto == null) {
            showPhoto(currentStudent.getProfilePic());
        }
    }
}
