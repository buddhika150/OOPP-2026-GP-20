package com.ftms.view.admin;

import com.ftms.view.LoginFrame;
import com.ftms.dao.AdminDAO;
import java.sql.SQLException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminDashboard extends JFrame {

    private final Color NAVY = new Color(18, 44, 78);
    private final Color TEAL = new Color(21, 130, 153);
    private final Color BG = new Color(238, 243, 248);
    private final Color MUTED = new Color(103, 119, 143);

    public AdminDashboard() {

        setTitle("FTMS - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // MAIN WINDOW
        JPanel root = new JPanel(new BorderLayout(18, 0));
        root.setBackground(BG);
        root.setBorder(
                BorderFactory.createEmptyBorder(12, 15, 12, 20)
        );
        setContentPane(root);

        //  SIDEBAR

        JPanel sidebar = new RoundedPanel(NAVY, 16);
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(270, 0));
        sidebar.setBorder(
                BorderFactory.createEmptyBorder(25, 16, 15, 16)
        );

        // FTMS TITLE
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel ftms = new JLabel("FTMS");
        ftms.setFont(new Font("Arial", Font.BOLD, 28));
        ftms.setForeground(Color.WHITE);
        ftms.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("FACULTY MANAGEMENT");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 11));
        subtitle.setForeground(new Color(193, 213, 226));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(ftms);
        brand.add(Box.createVerticalStrut(6));
        brand.add(subtitle);
        brand.add(Box.createVerticalStrut(25));

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(65, 95, 125));
        separator.setMaximumSize(new Dimension(500, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(separator);
        sidebar.add(brand, BorderLayout.NORTH);

        // SIDEBAR MENU
        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(
                BorderFactory.createEmptyBorder(25, 0, 0, 0)
        );

        JButton dashboardBtn = sideButton("Dashboard", true);
        JButton usersBtn = sideButton("Manage Users", false);
        JButton coursesBtn = sideButton("Manage Courses", false);
        JButton noticesBtn = sideButton("Manage Notices", false);
        JButton timetableBtn = sideButton("Timetable", false);

        JButton[] buttons = {
                dashboardBtn,
                usersBtn,
                coursesBtn,
                noticesBtn,
                timetableBtn
        };

        for (JButton btn : buttons) {
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(1000, 44));
            menu.add(btn);
            menu.add(Box.createVerticalStrut(12));
        }

        sidebar.add(menu, BorderLayout.CENTER);

        // LOGOUT BUTTON
        JButton logout = sideButton("Logout", false);
        logout.setBackground(new Color(37, 76, 110));
        logout.setPreferredSize(new Dimension(0, 43));

        sidebar.add(logout, BorderLayout.SOUTH);
        root.add(sidebar, BorderLayout.WEST);

        // MAIN CONTENT

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        content.setBorder(
                BorderFactory.createEmptyBorder(18, 0, 0, 0)
        );

        // HEADER
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setPreferredSize(new Dimension(0, 45));

        JLabel heading = new JLabel("Admin Dashboard");
        heading.setFont(new Font("Arial", Font.BOLD, 21));
        heading.setForeground(NAVY);

        JLabel adminLabel = new JLabel("ADMIN");
        adminLabel.setFont(new Font("Arial", Font.BOLD, 12));
        adminLabel.setForeground(TEAL);
        adminLabel.setOpaque(true);
        adminLabel.setBackground(Color.WHITE);
        adminLabel.setBorder(
                BorderFactory.createEmptyBorder(9, 15, 9, 15)
        );

        top.add(heading, BorderLayout.WEST);
        top.add(adminLabel, BorderLayout.EAST);

        content.add(top, BorderLayout.NORTH);

        // DASHBOARD BODY

        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setOpaque(false);

        // WELCOME BOX
        JPanel welcome = new RoundedPanel(Color.WHITE, 18);
        welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));
        welcome.setBorder(
                BorderFactory.createEmptyBorder(20, 22, 18, 22)
        );
        welcome.setPreferredSize(new Dimension(600, 110));

        JLabel welcomeTitle = new JLabel("Welcome Back, Admin!");
        welcomeTitle.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeTitle.setForeground(NAVY);
        welcomeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeText = new JLabel(
                "Manage your faculty activities in one place."
        );
        welcomeText.setFont(new Font("Arial", Font.PLAIN, 13));
        welcomeText.setForeground(MUTED);
        welcomeText.setAlignmentX(Component.LEFT_ALIGNMENT);

        welcome.add(welcomeTitle);
        welcome.add(Box.createVerticalStrut(9));
        welcome.add(welcomeText);

        body.add(welcome, BorderLayout.NORTH);
        AdminDAO adminDAO = new AdminDAO();

        int totalUsers = 0;
        int totalCourses = 0;
        int totalNotices = 0;
        int totalTimetables = 0;

        try {
            totalUsers = adminDAO.getTotalUsers();
            totalCourses = adminDAO.getTotalCourses();
            totalNotices = adminDAO.getTotalNotices();
            totalTimetables = adminDAO.getTotalTimetables();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load dashboard counts: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }


        // MEDIUM-SIZE CARDS

        JPanel cards = new JPanel
                (
                        new GridLayout(2, 2, 18, 18)
                );
        cards.setOpaque(false);

        JButton usersCard = createCard(

                        "users",
                        "Manage Users",
                        "Total Users: " + totalUsers
                );

        JButton coursesCard = createCard(
                "book",
                "Manage Courses",
                "Total Courses: " + totalCourses
        );

        JButton noticesCard = createCard(
                "megaphone",
                "Manage Notices",
                "Total Notices: " + totalNotices
        );

        JButton timetableCard = createCard(
                "calendar",
                "Timetable",
                "Total Timetables: " + totalTimetables
        );

        cards.add(usersCard);
        cards.add(coursesCard);
        cards.add(noticesCard);
        cards.add(timetableCard);

        // Medium cards with balanced spacing
        cards.setPreferredSize(new Dimension(760, 380));

        // Center cards without stretching
        JPanel cardsWrapper = new JPanel(new GridBagLayout());
        cardsWrapper.setOpaque(false);
        cardsWrapper.add(cards);

        body.add(cardsWrapper, BorderLayout.CENTER);

        content.add(body, BorderLayout.CENTER);
        root.add(content, BorderLayout.CENTER);


        // BUTTON ACTIONS - SIDEBAR
        usersBtn.addActionListener(e -> openPage(new UserManagement()));
        coursesBtn.addActionListener(e -> openPage(new CourseManagement()));
        noticesBtn.addActionListener(e -> openPage(new NoticeManagement()));
        timetableBtn.addActionListener(e -> openPage(new TimetableManagement()));

        // DASHBOARD CARD ACTIONS
        usersCard.addActionListener(e -> openPage(new UserManagement()));
        coursesCard.addActionListener(e -> openPage(new CourseManagement()));
        noticesCard.addActionListener(e -> openPage(new NoticeManagement()));
        timetableCard.addActionListener(e -> openPage(new TimetableManagement()));

        // LOGOUT
        logout.addActionListener(e -> openPage(new LoginFrame()));



        setVisible(true);
    }

    // Show the next window, then close the current dashboard.
    private void openPage(JFrame page) {
        page.setVisible(true);
        dispose();
    }

    // SIDEBAR BUTTON

    private JButton sideButton(String text, boolean active) {

        JButton btn = new JButton(text);

        btn.setFont(new Font(
                "Arial",
                active ? Font.BOLD : Font.PLAIN,
                14
        ));

        btn.setForeground(
                active
                        ? Color.WHITE
                        : new Color(218, 230, 240)
        );

        btn.setBackground(active ? TEAL : NAVY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(
                BorderFactory.createEmptyBorder(0, 15, 0, 0)
        );

        btn.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return btn;
    }

    //  MODERN CARD

    private JButton createCard(
            String iconType,
            String title,
            String description) {

        JButton button = new JButton() {

            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                int w = getWidth();
                int h = getHeight();

                // SHADOW
                g2.setColor(new Color(195, 210, 223, 100));
                g2.fillRoundRect(
                        4, 5, w - 8, h - 9, 24, 24
                );

                // CARD BACKGROUND
                g2.setColor(
                        hovered
                                ? new Color(240, 251, 253)
                                : Color.WHITE
                );
                g2.fillRoundRect(
                        1, 1, w - 6, h - 9, 24, 24
                );

                // HOVER ACCENT
                if (hovered) {
                    g2.setColor(TEAL);
                    g2.fillRoundRect(
                            2, 20, 5, h - 45, 5, 5
                    );
                }

                // CENTER POSITION
                int centerX = w / 2;
                int centerY = h / 2;

                // CENTER ICON
                Graphics2D iconG = (Graphics2D) g2.create();
                iconG.translate(centerX - 16, centerY - 68);
                iconG.scale(1.3, 1.3);
                drawIcon(iconG, iconType);
                iconG.dispose();

                // CENTER TITLE
                g2.setColor(NAVY);
                g2.setFont(new Font("Arial", Font.BOLD, 20));

                FontMetrics titleFM = g2.getFontMetrics();
                int titleX = centerX
                        - titleFM.stringWidth(title) / 2;

                g2.drawString(
                        title, titleX, centerY + 10
                );

                // CENTER DESCRIPTION
                g2.setColor(MUTED);
                g2.setFont(new Font("Arial", Font.PLAIN, 13));

                FontMetrics descFM = g2.getFontMetrics();
                int descX = centerX
                        - descFM.stringWidth(description) / 2;

                g2.drawString(
                        description, descX, centerY + 38
                );

                g2.dispose();
            }
        };

        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);

        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    //  DRAW ICONS

    private void drawIcon(Graphics2D g, String type) {

        g.setColor(TEAL);
        g.setStroke(new BasicStroke(
                2f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        switch (type) {

            case "users":
                g.drawOval(5, 2, 7, 7);
                g.drawOval(16, 4, 6, 6);
                g.drawArc(2, 12, 15, 12, 0, 180);
                g.drawArc(15, 13, 11, 10, 0, 180);
                break;

            case "book":
                g.drawLine(13, 5, 13, 24);
                g.drawLine(3, 5, 3, 21);
                g.drawLine(23, 5, 23, 21);
                g.drawLine(3, 5, 9, 4);
                g.drawLine(9, 4, 13, 7);
                g.drawLine(13, 7, 18, 4);
                g.drawLine(18, 4, 23, 5);
                g.drawLine(3, 21, 9, 20);
                g.drawLine(9, 20, 13, 23);
                g.drawLine(13, 23, 18, 20);
                g.drawLine(18, 20, 23, 21);
                break;

            case "megaphone":
                g.drawLine(3, 11, 18, 6);
                g.drawLine(3, 11, 3, 18);
                g.drawLine(3, 18, 18, 23);
                g.drawLine(18, 6, 18, 23);
                g.drawLine(7, 19, 10, 25);
                g.drawLine(10, 25, 13, 25);
                break;

            case "calendar":
                g.drawRoundRect(3, 5, 21, 20, 2, 2);
                g.drawLine(3, 11, 24, 11);
                g.drawLine(9, 2, 9, 8);
                g.drawLine(18, 2, 18, 8);

                g.fillRect(7, 15, 3, 3);
                g.fillRect(13, 15, 3, 3);
                g.fillRect(19, 15, 3, 3);
                g.fillRect(7, 20, 3, 3);
                g.fillRect(13, 20, 3, 3);
                break;
        }
    }

    // ROUNDED PANEL

    class RoundedPanel extends JPanel {

        private final Color color;
        private final int radius;

        public RoundedPanel(Color color, int radius) {
            this.color = color;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);
            g2.fillRoundRect(
                    0, 0,
                    getWidth(), getHeight(),
                    radius, radius
            );

            g2.dispose();
            super.paintComponent(g);
        }
    }

    //  MESSAGE

    private void showPage(String name) {

        JOptionPane.showMessageDialog(
                this,
                name + " page will be added soon."
        );
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}
