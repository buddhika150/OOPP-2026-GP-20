package com.ftms.view.technicalofficer;

import com.ftms.model.TechnicalOfficer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Main Swing Dashboard for Member 3 (Technical Officer).
 * Matches the uploaded FTMS UI theme and provides navigation across
 * Dashboard, Attendance, Attendance Summary, Medical Records, Medical Approval,
 * Notices, and Timetable.
 */
public class TechnicalOfficerDashboard extends JFrame {

    // Theme palette matching the reference screenshot
    private static final Color SIDEBAR_BG = new Color(7, 43, 71);
    private static final Color SIDEBAR_ACTIVE = new Color(0, 131, 155);
    private static final Color SIDEBAR_HOVER = new Color(14, 61, 96);
    private static final Color SIDEBAR_DIVIDER = new Color(28, 69, 102);
    private static final Color SIDEBAR_SUBTITLE = new Color(175, 198, 218);

    private static final Color MAIN_BG = new Color(245, 248, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(224, 232, 240);
    private static final Color CARD_HOVER_BORDER = new Color(0, 131, 155);
    private static final Color ICON_CIRCLE_BG = new Color(214, 236, 243);
    private static final Color NAVY_DARK = new Color(11, 46, 74);
    private static final Color TEAL_ACCENT = new Color(0, 131, 155);
    private static final Color TEXT_MUTED = new Color(115, 128, 142);

    private static final String CARD_DASHBOARD = "DASHBOARD";
    private static final String CARD_ATTENDANCE = "ATTENDANCE";
    private static final String CARD_SUMMARY = "SUMMARY";
    private static final String CARD_MEDICAL = "MEDICAL";
    private static final String CARD_APPROVAL = "APPROVAL";
    private static final String CARD_NOTICES = "NOTICES";
    private static final String CARD_TIMETABLE = "TIMETABLE";

    private final TechnicalOfficer currentOfficer;

    private CardLayout cardLayout;
    private JPanel contentCardsPanel;
    private JLabel lblPageTitle;

    private AddAttendancePanel addAttendancePanel;
    private AttendanceSummaryPanel attendanceSummaryPanel;
    private AddMedicalPanel addMedicalPanel;
    private MedicalApprovalPanel medicalApprovalPanel;

    private final Map<String, SidebarNavButton> navButtons = new LinkedHashMap<>();

    public TechnicalOfficerDashboard() {
        this(null);
    }

    public TechnicalOfficerDashboard(TechnicalOfficer officer) {
        this.currentOfficer = officer;
        initFrame();
        initComponents();
        navigateTo(CARD_DASHBOARD, "Technical Officer Dashboard");
    }

    private void initFrame() {
        setTitle("FTMS - Technical Officer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(1020, 660));
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(MAIN_BG);

        // 1. Left Sidebar
        root.add(createSidebar(), BorderLayout.WEST);

        // 2. Right Main Content Area
        JPanel mainArea = new JPanel(new BorderLayout(0, 18));
        mainArea.setBackground(MAIN_BG);
        mainArea.setBorder(new EmptyBorder(26, 32, 26, 32));

        // Header Bar
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setOpaque(false);

        lblPageTitle = new JLabel("Technical Officer Dashboard");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lblPageTitle.setForeground(NAVY_DARK);

        String roleBadgeText = (currentOfficer != null && currentOfficer.getDepartment() != null)
                ? "TECHNICAL OFFICER (" + currentOfficer.getDepartment() + ")"
                : "TECHNICAL OFFICER";
        JLabel lblRoleBadge = new JLabel(roleBadgeText);
        lblRoleBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRoleBadge.setForeground(TEAL_ACCENT);

        headerBar.add(lblPageTitle, BorderLayout.WEST);
        headerBar.add(lblRoleBadge, BorderLayout.EAST);
        mainArea.add(headerBar, BorderLayout.NORTH);

        // CardLayout Container
        cardLayout = new CardLayout();
        contentCardsPanel = new JPanel(cardLayout);
        contentCardsPanel.setOpaque(false);

        addAttendancePanel = new AddAttendancePanel();
        attendanceSummaryPanel = new AttendanceSummaryPanel();
        addMedicalPanel = new AddMedicalPanel();
        medicalApprovalPanel = new MedicalApprovalPanel();

        contentCardsPanel.add(createDashboardHomePanel(), CARD_DASHBOARD);
        contentCardsPanel.add(addAttendancePanel, CARD_ATTENDANCE);
        contentCardsPanel.add(attendanceSummaryPanel, CARD_SUMMARY);
        contentCardsPanel.add(addMedicalPanel, CARD_MEDICAL);
        contentCardsPanel.add(medicalApprovalPanel, CARD_APPROVAL);
        contentCardsPanel.add(createNoticesPanel(), CARD_NOTICES);
        contentCardsPanel.add(createTimetablePanel(), CARD_TIMETABLE);

        mainArea.add(contentCardsPanel, BorderLayout.CENTER);
        root.add(mainArea, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(235, 0));

        // Top Branding + Navigation List
        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBorder(new EmptyBorder(28, 18, 18, 18));

        JLabel lblBrand = new JLabel("FTMS");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblBrand.setBorder(new EmptyBorder(0, 8, 2, 0));

        JLabel lblSubBrand = new JLabel("FACULTY MANAGEMENT");
        lblSubBrand.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblSubBrand.setForeground(SIDEBAR_SUBTITLE);
        lblSubBrand.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubBrand.setBorder(new EmptyBorder(0, 8, 28, 0));

        topSection.add(lblBrand);
        topSection.add(lblSubBrand);

        addNavButton(topSection, CARD_DASHBOARD, "Dashboard", "Technical Officer Dashboard", IconType.DASHBOARD);
        addNavButton(topSection, CARD_ATTENDANCE, "Manage Attendance", "Attendance Management", IconType.USERS);
        addNavButton(topSection, CARD_SUMMARY, "Attendance Summary", "Attendance Summary", IconType.COURSES);
        addNavButton(topSection, CARD_MEDICAL, "Medical Records", "Medical Records Management", IconType.MEDICAL);
        addNavButton(topSection, CARD_APPROVAL, "Medical Approval", "Medical Approval", IconType.APPROVAL);
        addNavButton(topSection, CARD_NOTICES, "Manage Notices", "Faculty Notices", IconType.NOTICES);
        addNavButton(topSection, CARD_TIMETABLE, "Timetable", "Department Timetable", IconType.TIMETABLE);

        sidebar.add(topSection, BorderLayout.NORTH);

        // Bottom Logout Area
        JPanel bottomSection = new JPanel(new BorderLayout());
        bottomSection.setOpaque(false);
        bottomSection.setBorder(new EmptyBorder(10, 18, 22, 18));

        JSeparator separator = new JSeparator();
        separator.setForeground(SIDEBAR_DIVIDER);
        separator.setBackground(SIDEBAR_DIVIDER);
        bottomSection.add(separator, BorderLayout.NORTH);

        SidebarNavButton btnLogout = new SidebarNavButton("Logout", IconType.LOGOUT);
        btnLogout.setBorder(new EmptyBorder(12, 12, 8, 12));
        btnLogout.addActionListener(e -> handleLogout());
        bottomSection.add(btnLogout, BorderLayout.CENTER);

        sidebar.add(bottomSection, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addNavButton(JPanel parent, String cardKey, String label, String pageTitle, IconType iconType) {
        SidebarNavButton btn = new SidebarNavButton(label, iconType);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.addActionListener(e -> navigateTo(cardKey, pageTitle));

        navButtons.put(cardKey, btn);
        parent.add(btn);
        parent.add(Box.createVerticalStrut(6));
    }

    public void navigateTo(String cardKey, String pageTitle) {
        lblPageTitle.setText(pageTitle);
        for (Map.Entry<String, SidebarNavButton> entry : navButtons.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(cardKey));
        }

        // Refresh data when switching into data-driven panels
        if (CARD_ATTENDANCE.equals(cardKey) && addAttendancePanel != null) {
            addAttendancePanel.loadAttendanceRecords();
        } else if (CARD_SUMMARY.equals(cardKey) && attendanceSummaryPanel != null) {
            attendanceSummaryPanel.loadSummaryData();
        } else if (CARD_MEDICAL.equals(cardKey) && addMedicalPanel != null) {
            addMedicalPanel.loadMedicalRecords();
        } else if (CARD_APPROVAL.equals(cardKey) && medicalApprovalPanel != null) {
            medicalApprovalPanel.loadRecords();
        }

        cardLayout.show(contentCardsPanel, cardKey);
    }

    private JPanel createDashboardHomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout(0, 22));
        homePanel.setOpaque(false);

        // 1. Welcome Banner Card matching the screenshot
        RoundedCardPanel welcomeCard = new RoundedCardPanel(14, CARD_BG, CARD_BORDER);
        welcomeCard.setLayout(new FlowLayout(FlowLayout.LEFT, 24, 22));
        welcomeCard.setPreferredSize(new Dimension(0, 130));

        CircleBadgeIcon bannerIcon = new CircleBadgeIcon(80, ICON_CIRCLE_BG, NAVY_DARK, IconType.INSTITUTE);
        welcomeCard.add(bannerIcon);

        JPanel welcomeTextPanel = new JPanel();
        welcomeTextPanel.setOpaque(false);
        welcomeTextPanel.setLayout(new BoxLayout(welcomeTextPanel, BoxLayout.Y_AXIS));

        String officerDisplayName = "Technical Officer";
        if (currentOfficer != null) {
            if (currentOfficer.getFname() != null && !currentOfficer.getFname().isEmpty()) {
                officerDisplayName = currentOfficer.getFname();
            } else if (currentOfficer.getUsername() != null && !currentOfficer.getUsername().isEmpty()) {
                officerDisplayName = currentOfficer.getUsername();
            }
        }

        JLabel lblWelcomeTitle = new JLabel("Welcome Back, " + officerDisplayName + "!");
        lblWelcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        lblWelcomeTitle.setForeground(NAVY_DARK);

        JLabel lblWelcomeSub = new JLabel("Manage your faculty activities in one place.");
        lblWelcomeSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcomeSub.setForeground(TEXT_MUTED);

        welcomeTextPanel.add(lblWelcomeTitle);
        welcomeTextPanel.add(Box.createVerticalStrut(6));
        welcomeTextPanel.add(lblWelcomeSub);

        welcomeCard.add(welcomeTextPanel);
        homePanel.add(welcomeCard, BorderLayout.NORTH);

        // 2. Action Cards Grid matching screenshot
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);

        JPanel cardsGrid = new JPanel(new GridLayout(3, 2, 22, 20));
        cardsGrid.setOpaque(false);

        cardsGrid.add(createFeatureCard(
                "Manage Attendance",
                "Record & update session attendance",
                IconType.USERS,
                CARD_ATTENDANCE,
                "Attendance Management"
        ));
        cardsGrid.add(createFeatureCard(
                "Attendance Summary",
                "Individual & batch attendance %",
                IconType.COURSES,
                CARD_SUMMARY,
                "Attendance Summary"
        ));
        cardsGrid.add(createFeatureCard(
                "Medical Records",
                "Submit & manage student medicals",
                IconType.MEDICAL,
                CARD_MEDICAL,
                "Medical Records Management"
        ));
        cardsGrid.add(createFeatureCard(
                "Medical Approval",
                "Approve or reject medical requests",
                IconType.APPROVAL,
                CARD_APPROVAL,
                "Medical Approval"
        ));
        cardsGrid.add(createFeatureCard(
                "Manage Notices",
                "Faculty updates",
                IconType.NOTICES,
                CARD_NOTICES,
                "Faculty Notices"
        ));
        cardsGrid.add(createFeatureCard(
                "Timetable",
                "Class schedules",
                IconType.TIMETABLE,
                CARD_TIMETABLE,
                "Department Timetable"
        ));

        gridWrapper.add(cardsGrid, BorderLayout.NORTH);
        homePanel.add(gridWrapper, BorderLayout.CENTER);

        return homePanel;
    }

    private JPanel createFeatureCard(String title, String subtitle, IconType iconType,
                                     String targetCard, String targetPageTitle) {
        RoundedCardPanel card = new RoundedCardPanel(14, CARD_BG, CARD_BORDER);
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 22));
        card.setPreferredSize(new Dimension(320, 112));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        CircleBadgeIcon badge = new CircleBadgeIcon(62, ICON_CIRCLE_BG, TEAL_ACCENT, iconType);

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(NAVY_DARK);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_MUTED);

        textBox.add(lblTitle);
        textBox.add(Box.createVerticalStrut(4));
        textBox.add(lblSub);

        card.add(badge);
        card.add(textBox);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorderColor(CARD_HOVER_BORDER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorderColor(CARD_BORDER);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                navigateTo(targetCard, targetPageTitle);
            }
        });

        return card;
    }

    private JPanel createNoticesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(18, 22, 18, 22)
        ));

        JLabel lblHeading = new JLabel("Published Faculty Notices");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeading.setForeground(NAVY_DARK);
        card.add(lblHeading, BorderLayout.NORTH);

        String[] cols = {"Notice ID", "Title", "Content", "Published Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addRow(new Object[]{1, "Welcome to FTMS", "Check your timetable and courses.", "2026-09-01"});
        model.addRow(new Object[]{2, "CA assessment", "CA marks and eligibility can be checked in FTMS.", "2026-09-15"});

        JTable table = new JTable(model);
        styleBasicTable(table);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(CARD_BORDER, 1));
        sp.getViewport().setBackground(Color.WHITE);
        card.add(sp, BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(18, 22, 18, 22)
        ));

        JLabel lblHeading = new JLabel("Department Course Timetable (ICT)");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeading.setForeground(NAVY_DARK);
        card.add(lblHeading, BorderLayout.NORTH);

        String[] cols = {"Timetable ID", "Course Code", "Department", "Day", "Time Slot"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addRow(new Object[]{1, "ICT2132", "ICT", "Monday", "08:00-10:00"});
        model.addRow(new Object[]{2, "ICT2142", "ICT", "Tuesday", "10:00-12:00"});
        model.addRow(new Object[]{3, "ICT2152", "ICT", "Wednesday", "13:00-15:00"});
        model.addRow(new Object[]{4, "ICT2162", "ICT", "Thursday", "09:00-11:00"});

        JTable table = new JTable(model);
        styleBasicTable(table);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(CARD_BORDER, 1));
        sp.getViewport().setBackground(Color.WHITE);
        card.add(sp, BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private void styleBasicTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(34);
        table.setSelectionBackground(new Color(216, 238, 245));
        table.setSelectionForeground(NAVY_DARK);
        table.setGridColor(new Color(235, 240, 245));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(NAVY_DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            // Open LoginFrame dynamically if present in the project
            try {
                Class<?> loginClass = Class.forName("com.ftms.view.LoginFrame");
                Object frame = loginClass.getDeclaredConstructor().newInstance();
                if (frame instanceof JFrame) {
                    ((JFrame) frame).setVisible(true);
                }
            } catch (Exception ignored) {
                // LoginFrame not yet added by Member 1
            }
        }
    }

    // --- Custom Styled UI Components matching the Screenshot ---

    private enum IconType {
        DASHBOARD, USERS, COURSES, MEDICAL, APPROVAL, NOTICES, TIMETABLE, LOGOUT, INSTITUTE
    }

    private static class SidebarNavButton extends JButton {
        private final IconType iconType;
        private boolean active = false;
        private boolean hovered = false;

        SidebarNavButton(String text, IconType iconType) {
            super(text);
            this.iconType = iconType;
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 42, 10, 14));

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

        void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (active) {
                g2.setColor(SIDEBAR_ACTIVE);
                g2.fill(new RoundRectangle2D.Double(0, 2, getWidth(), getHeight() - 4, 10, 10));
            } else if (hovered) {
                g2.setColor(SIDEBAR_HOVER);
                g2.fill(new RoundRectangle2D.Double(0, 2, getWidth(), getHeight() - 4, 10, 10));
            }

            // Draw left vector icon
            drawVectorIcon(g2, iconType, 14, (getHeight() - 16) / 2, 16, Color.WHITE);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class RoundedCardPanel extends JPanel {
        private final int radius;
        private final Color bgColor;
        private Color borderColor;

        RoundedCardPanel(int radius, Color bgColor, Color borderColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        void setBorderColor(Color borderColor) {
            this.borderColor = borderColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class CircleBadgeIcon extends JComponent {
        private final int diameter;
        private final Color circleColor;
        private final Color iconColor;
        private final IconType iconType;

        CircleBadgeIcon(int diameter, Color circleColor, Color iconColor, IconType iconType) {
            this.diameter = diameter;
            this.circleColor = circleColor;
            this.iconColor = iconColor;
            this.iconType = iconType;
            setPreferredSize(new Dimension(diameter, diameter));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(circleColor);
            g2.fillOval(0, 0, diameter - 1, diameter - 1);

            int iconSize = diameter / 2;
            int offset = (diameter - iconSize) / 2;
            drawVectorIcon(g2, iconType, offset, offset, iconSize, iconColor);

            g2.dispose();
        }
    }

    private static void drawVectorIcon(Graphics2D g2, IconType type, int x, int y, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(Math.max(1.8f, size / 11f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (type) {
            case DASHBOARD -> {
                int half = size / 2 - 2;
                g2.drawRect(x, y, half, half);
                g2.drawRect(x + half + 3, y, half, half);
                g2.drawRect(x, y + half + 3, half, half);
                g2.drawRect(x + half + 3, y + half + 3, half, half);
            }
            case USERS -> {
                int r = size / 3;
                g2.drawOval(x + size / 4, y + 1, r, r);
                g2.drawArc(x + 2, y + r + 3, size - 4, size / 2, 0, 180);
            }
            case COURSES -> {
                g2.drawRect(x + 2, y + 2, size - 4, size - 4);
                g2.drawLine(x + size / 2, y + 2, x + size / 2, y + size - 2);
            }
            case MEDICAL -> {
                g2.drawRoundRect(x + 2, y + 2, size - 4, size - 4, 4, 4);
                g2.drawLine(x + size / 2, y + 5, x + size / 2, y + size - 5);
                g2.drawLine(x + 5, y + size / 2, x + size - 5, y + size / 2);
            }
            case APPROVAL -> {
                g2.drawRoundRect(x + 2, y + 2, size - 4, size - 4, 4, 4);
                g2.drawLine(x + 5, y + size / 2, x + size / 2 - 1, y + size - 5);
                g2.drawLine(x + size / 2 - 1, y + size - 5, x + size - 4, y + 5);
            }
            case NOTICES -> {
                int[] px = {x + 3, x + size - 3, x + size - 3, x + 3};
                int[] py = {y + size / 2 - 2, y + 3, y + size - 3, y + size / 2 + 2};
                g2.drawPolygon(px, py, 4);
            }
            case TIMETABLE -> {
                g2.drawRoundRect(x + 2, y + 3, size - 4, size - 4, 4, 4);
                g2.drawLine(x + 2, y + 8, x + size - 2, y + 8);
                g2.drawLine(x + 6, y + 1, x + 6, y + 5);
                g2.drawLine(x + size - 6, y + 1, x + size - 6, y + 5);
            }
            case LOGOUT -> {
                g2.drawRect(x + 2, y + 2, size / 2, size - 4);
                g2.drawLine(x + size / 2, y + size / 2, x + size - 2, y + size / 2);
                g2.drawLine(x + size - 5, y + size / 2 - 3, x + size - 2, y + size / 2);
                g2.drawLine(x + size - 5, y + size / 2 + 3, x + size - 2, y + size / 2);
            }
            case INSTITUTE -> {
                int[] rx = {x + 2, x + size / 2, x + size - 2};
                int[] ry = {y + size / 3, y + 2, y + size / 3};
                g2.drawPolygon(rx, ry, 3);
                g2.drawLine(x + 4, y + size - 3, x + size - 4, y + size - 3);
                g2.drawLine(x + 7, y + size / 3, x + 7, y + size - 3);
                g2.drawLine(x + size / 2, y + size / 3, x + size / 2, y + size - 3);
                g2.drawLine(x + size - 7, y + size / 3, x + size - 7, y + size - 3);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TechnicalOfficer sampleOfficer = new TechnicalOfficer(
                    7, "to01", "to123", "to01@gmail.com", "TECHNICAL_OFFICER",
                    "Ruwan", "Rathnayake", "0704121966", null, "ICT"
            );
            new TechnicalOfficerDashboard(sampleOfficer).setVisible(true);
        });
    }
}
