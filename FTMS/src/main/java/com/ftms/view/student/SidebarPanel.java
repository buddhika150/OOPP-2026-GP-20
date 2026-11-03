package com.ftms.view.student;

import com.ftms.util.SessionManager;
import com.ftms.view.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarPanel extends JPanel {
    private static final Color BACKGROUND = new Color(17, 30, 51);
    private static final Color TEXT = new Color(205, 218, 234);
    private static final Color MUTED = new Color(136, 156, 180);
    private static final Color ACTIVE = new Color(42, 72, 113);
    private static final Color HOVER = new Color(31, 53, 82);
    private static final Color ACCENT = new Color(78, 170, 241);

    private NavButton selectedButton;

    public SidebarPanel(CardLayout cardLayout, JPanel mainContentPanel,
                        JFrame parentFrame, ProfilePanel profilePanel,
                        Runnable refreshDataCallback) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BACKGROUND);
        setPreferredSize(new Dimension(242, 700));
        setBorder(BorderFactory.createEmptyBorder(26, 14, 18, 14));

        JLabel brand = new JLabel("FTMS");
        brand.setFont(new Font("SansSerif", Font.BOLD, 28));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        add(brand);

        JLabel subtitle = new JLabel("FACULTY MANAGEMENT");
        subtitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(3, 10, 0, 0));
        add(subtitle);
        add(Box.createVerticalStrut(38));

        JLabel menuLabel = new JLabel("MAIN MENU");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        menuLabel.setForeground(MUTED);
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 11, 0));
        add(menuLabel);

        NavButton dashboard = addNav("Dashboard", "Dashboard", cardLayout, mainContentPanel, null);
        addNav("My Profile", "Profile", cardLayout, mainContentPanel, profilePanel::updateProfileData);
        addNav("My Courses", "Courses", cardLayout, mainContentPanel, refreshDataCallback);
        addNav("My Attendance", "Attendance", cardLayout, mainContentPanel, refreshDataCallback);
        addNav("My Eligibility", "Eligibility", cardLayout, mainContentPanel, refreshDataCallback);
        addNav("My Grades & GPA", "Grades", cardLayout, mainContentPanel, refreshDataCallback);
        addNav("View Medical", "Medical", cardLayout, mainContentPanel, refreshDataCallback);
        addNav("My Timetable", "Timetable", cardLayout, mainContentPanel, null);
        addNav("Notices", "Notices", cardLayout, mainContentPanel, null);

        setSelected(dashboard);
        add(Box.createVerticalGlue());

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(51, 70, 95));
        separator.setBackground(new Color(51, 70, 95));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        add(separator);
        add(Box.createVerticalStrut(12));

        NavButton logout = new NavButton("Log Out", true);
        logout.addActionListener(e -> {
            SessionManager.getInstance().logout();
            parentFrame.dispose();
            new LoginFrame().setVisible(true);
        });
        add(logout);
        add(Box.createVerticalStrut(8));
    }

    private NavButton addNav(String text, String card, CardLayout layout,
                             JPanel content, Runnable afterNavigate) {
        NavButton button = new NavButton(text, false);
        button.addActionListener(e -> {
            setSelected(button);
            layout.show(content, card);
            if (afterNavigate != null) afterNavigate.run();
        });
        add(button);
        add(Box.createVerticalStrut(5));
        return button;
    }

    private void setSelected(NavButton button) {
        if (selectedButton != null) {
            selectedButton.selected = false;
            selectedButton.repaint();
        }
        selectedButton = button;
        button.selected = true;
        button.repaint();
    }

    private static class NavButton extends JButton {
        private boolean selected;
        private boolean hovered;
        private final boolean logout;

        NavButton(String text, boolean logout) {
            super(text);
            this.logout = logout;
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
            setPreferredSize(new Dimension(214, 43));
            setMinimumSize(new Dimension(180, 43));
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 17, 0, 8));
            setForeground(logout ? new Color(248, 164, 164) : TEXT);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (selected || hovered) {
                g.setColor(selected ? ACTIVE : HOVER);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 13, 13);
            }
            if (selected) {
                g.setColor(ACCENT);
                g.fillRoundRect(0, 9, 4, getHeight() - 18, 4, 4);
            }
            g.dispose();
            setForeground(logout ? new Color(248, 164, 164) : selected ? Color.WHITE : TEXT);
            super.paintComponent(graphics);
        }
    }
}
