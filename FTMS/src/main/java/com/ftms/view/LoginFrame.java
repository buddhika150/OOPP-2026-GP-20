
package com.ftms.view;

import com.ftms.dao.LoginDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton;

    Color navy = new Color(16, 43, 78);
    Color teal = new Color(22, 124, 145);

    public LoginFrame() {

        setTitle("FTMS - Faculty Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(850, 550));

        // Main background
        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(235, 241, 247));
        add(background);

        // Main login container
        JPanel container = new JPanel(new GridLayout(1, 2));
        container.setPreferredSize(new Dimension(900, 520));
        background.add(container);

        // LEFT SIDE
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(navy);
        leftPanel.setBorder(new EmptyBorder(110, 45, 50, 45));

        JLabel logo = new JLabel("FTMS");
        logo.setFont(new Font("Arial", Font.BOLD, 48));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel systemName = new JLabel(
                "<html>FACULTY OF TECHNOLOGY<br>MANAGEMENT SYSTEM</html>"
        );
        systemName.setFont(new Font("Arial", Font.BOLD, 19));
        systemName.setForeground(Color.WHITE);
        systemName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description = new JLabel(
                "<html>Manage your academic activities<br>" +
                        "easily and efficiently in one place.</html>"
        );
        description.setFont(new Font("Arial", Font.PLAIN, 15));
        description.setForeground(new Color(190, 215, 230));
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(logo);
        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(systemName);
        leftPanel.add(Box.createVerticalStrut(25));
        leftPanel.add(description);

        // RIGHT SIDE
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setPreferredSize(new Dimension(310, 350));

        JLabel welcome = new JLabel("Welcome Back!");
        welcome.setFont(new Font("Arial", Font.BOLD, 28));
        welcome.setForeground(navy);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to your FTMS account");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(310, 40));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 15));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(310, 40));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 15));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        char originalEcho = passwordField.getEchoChar();

        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setBackground(Color.WHITE);
        showPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(originalEcho);
            }
        });

        loginButton = new JButton("SIGN IN");
        loginButton.setBackground(teal);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setMaximumSize(new Dimension(310, 45));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        form.add(welcome);
        form.add(Box.createVerticalStrut(8));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(35));

        form.add(usernameLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(usernameField);
        form.add(Box.createVerticalStrut(20));

        form.add(passwordLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(10));
        form.add(showPassword);
        form.add(Box.createVerticalStrut(25));
        form.add(loginButton);

        rightPanel.add(form);

        container.add(leftPanel);
        container.add(rightPanel);

        // LOGIN FUNCTION
        loginButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter username and password.");
                return;
            }

            LoginDAO dao = new LoginDAO();
            String role = dao.login(username, password);

            if (role != null) {
                JOptionPane.showMessageDialog(this,
                        "Login Successful! Role: " + role);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid Username or Password");
            }
        });

        getRootPane().setDefaultButton(loginButton);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
