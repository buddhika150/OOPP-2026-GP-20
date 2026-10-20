package com.ftms.view.technicalofficer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * TechnicalOfficerDashboard - Primary application window for Member 3 (Technical Officer module).
 * Strictly mirrors the visual theme and layout of the reference screenshot:
 * - Dark navy sidebar (#1A2B4C) with bright cyan/teal highlight (#00A8A8) and logout button.
 * - Clean off-white main content area (#F4F7FB).
 * - Top header displaying dynamic screen title and user role badge ("TECHNICAL OFFICER").
 * - Welcome banner card with custom vector illustration of medical clipboard & kit.
 * - 2x2 grid of interactive overview feature cards with vector icons, chevrons, and status pills.
 * - Modular CardLayout navigation between Dashboard, Add Attendance, Attendance Summary,
 *   Add Medical Record, and Medical Approvals screens.
 */
public class TechnicalOfficerDashboard extends JFrame {

    // Card Identifiers
    public static final String CARD_DASHBOARD = "CARD_DASHBOARD";
    public static final String CARD_ADD_ATTENDANCE = "CARD_ADD_ATTENDANCE";
    public static final String CARD_ATTENDANCE_SUMMARY = "CARD_ATTENDANCE_SUMMARY";
    public static final String CARD_ADD_MEDICAL = "CARD_ADD_MEDICAL";
    public static final String CARD_MEDICAL_APPROVAL = "CARD_MEDICAL_APPROVAL";

    // Color Palette matching reference screenshot
    private static final Color SIDEBAR_BG = new Color(0x1A, 0x2B, 0x4C);
    private static final Color SIDEBAR_ITEM_HOVER = new Color(0x23, 0x36, 0x5D);
    private static final Color TEAL_ACCENT = new Color(0x00, 0xA8, 0xA8);
    private static final Color MAIN_BG = new Color(0xF4, 0xF7, 0xFB);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xE2, 0xE8, 0xF0);
    private static final Color TEXT_PRIMARY = new Color(0x1E, 0x29, 0x3B);
    private static final Color TEXT_MUTED = new Color(0x64, 0x74, 0x8B);

    // Status Pill Colors
    private static final Color PILL_YELLOW_BG = new Color(0xFE, 0xF3, 0xC7);
    private static final Color PILL_YELLOW_TEXT = new Color(0xB4, 0x53, 0x09);
    private static final Color PILL_GREEN_BG = new Color(0xDC, 0xFC, 0xE7);
    private static final Color PILL_GREEN_TEXT = new Color(0x15, 0x80, 0x3D);
    private static final Color PILL_BLUE_BG = new Color(0xDB, 0xEA, 0xFE);
    private static final Color PILL_BLUE_TEXT = new Color(0x1D, 0x4E, 0xD8);

    // Navigation and layout components
    private CardLayout cardLayout;
    private JPanel cardsContainer;
    private JLabel headerTitleLabel;
    private JLabel officerInfoLabel;
    private final List<SidebarNavButton> navButtons = new ArrayList<>();

    // Logged in officer state
    private String officerName = "Chamod Perera";
    private String department = "ICT";
    private int officerId = 172;

    // Sub-panels
    private AddAttendancePanel addAttendancePanel;
    private AttendanceSummaryPanel attendanceSummaryPanel;
    private AddMedicalPanel addMedicalPanel;
    private MedicalApprovalPanel medicalApprovalPanel;

    public TechnicalOfficerDashboard() {
        this(null);
    }

    public TechnicalOfficerDashboard(Object userSession) {
        extractSessionDetails(userSession);
        initializeUI();
    }

    public TechnicalOfficerDashboard(String officerName, String department) {
        if (officerName != null && !officerName.trim().isEmpty()) {
            this.officerName = officerName;
        }
        if (department != null && !department.trim().isEmpty()) {
            this.department = department;
        }
        initializeUI();
    }

    /**
     * Inspects passed session object or searches for SessionManager via reflection
     * to safely decouple from other members' classes while remaining fully compatible.
     */
    private void extractSessionDetails(Object sessionObj) {
        if (sessionObj != null) {
            try {
                Method getNameMethod = sessionObj.getClass().getMethod("getUsername");
                Object nameVal = getNameMethod.invoke(sessionObj);
                if (nameVal != null) this.officerName = nameVal.toString();

                Method getDeptMethod = sessionObj.getClass().getMethod("getDepartment");
                Object deptVal = getDeptMethod.invoke(sessionObj);
                if (deptVal != null) this.department = deptVal.toString();
                return;
            } catch (Exception ignored) {
            }
        }

        // Try discovering SessionManager if already implemented in the project
        try {
            Class<?> sessionMgrClass = Class.forName("com.ftms.util.SessionManager");
            Method getInstanceMethod = sessionMgrClass.getMethod("getInstance");
            Object instance = getInstanceMethod.invoke(null);
            Method getUserMethod = sessionMgrClass.getMethod("getCurrentUser");
            Object currentUser = getUserMethod.invoke(instance);
            if (currentUser != null) {
                Method getUnameMethod = currentUser.getClass().getMethod("getUsername");
                this.officerName = String.valueOf(getUnameMethod.invoke(currentUser));
            }
        } catch (Exception ignored) {
            // Default to Technical Officer 01 from db.sql: Chamod Perera (to01, Department: ICT)
        }
    }

    private void initializeUI() {
        setTitle("FTMS - Technical Officer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1160, 740);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(MAIN_BG);

        // 1. Sidebar on the West
        rootPanel.add(createSidebarPanel(), BorderLayout.WEST);

        // 2. Main Content on the Center (Header + CardsContainer)
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(MAIN_BG);
        mainArea.add(createHeaderPanel(), BorderLayout.NORTH);

        // Sub-panels initialization
        cardLayout = new CardLayout();
        cardsContainer = new JPanel(cardLayout);
        cardsContainer.setBackground(MAIN_BG);

        addAttendancePanel = new AddAttendancePanel();
        attendanceSummaryPanel = new AttendanceSummaryPanel();
        addMedicalPanel = new AddMedicalPanel();
        medicalApprovalPanel = new MedicalApprovalPanel();

        cardsContainer.add(createDashboardOverviewPanel(), CARD_DASHBOARD);
        cardsContainer.add(addAttendancePanel, CARD_ADD_ATTENDANCE);
        cardsContainer.add(attendanceSummaryPanel, CARD_ATTENDANCE_SUMMARY);
        cardsContainer.add(addMedicalPanel, CARD_ADD_MEDICAL);
        cardsContainer.add(medicalApprovalPanel, CARD_MEDICAL_APPROVAL);

        mainArea.add(cardsContainer, BorderLayout.CENTER);
        rootPanel.add(mainArea, BorderLayout.CENTER);

        setContentPane(rootPanel);
    }

    // =========================================================================
    // SIDEBAR COMPONENT (MATCHING SCREENSHOT)
    // =========================================================================

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(new EmptyBorder(28, 16, 24, 16));

        // Top Brand Section
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(0, 10, 24, 10));

        JLabel logoLabel = new JLabel("FTMS");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subBrandLabel1 = new JLabel("FACULTY OF");
        subBrandLabel1.setFont(new Font("Segoe UI", Font.BOLD, 10));
        subBrandLabel1.setForeground(new Color(0xD0, 0xDC, 0xF0));
        subBrandLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subBrandLabel2 = new JLabel("TECHNOLOGY");
        subBrandLabel2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        subBrandLabel2.setForeground(new Color(0xD0, 0xDC, 0xF0));
        subBrandLabel2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subBrandLabel3 = new JLabel("MANAGEMENT SYSTEM");
        subBrandLabel3.setFont(new Font("Segoe UI", Font.BOLD, 10));
        subBrandLabel3.setForeground(new Color(0xD0, 0xDC, 0xF0));
        subBrandLabel3.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandPanel.add(logoLabel);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(subBrandLabel1);
        brandPanel.add(subBrandLabel2);
        brandPanel.add(subBrandLabel3);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Center Navigation Menu Items
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        SidebarNavButton btnDashboard = new SidebarNavButton("Dashboard", "DASHBOARD", CARD_DASHBOARD);
        SidebarNavButton btnAddAttendance = new SidebarNavButton("Add Attendance", "ADD_ATTENDANCE", CARD_ADD_ATTENDANCE);
        SidebarNavButton btnAttSummary = new SidebarNavButton("Attendance Summary", "ATTENDANCE_SUMMARY", CARD_ATTENDANCE_SUMMARY);
        SidebarNavButton btnAddMedical = new SidebarNavButton("Add Medical Record", "ADD_MEDICAL", CARD_ADD_MEDICAL);
        SidebarNavButton btnApprovals = new SidebarNavButton("Medical Approvals", "MEDICAL_APPROVAL", CARD_MEDICAL_APPROVAL);

        navButtons.add(btnDashboard);
        navButtons.add(btnAddAttendance);
        navButtons.add(btnAttSummary);
        navButtons.add(btnAddMedical);
        navButtons.add(btnApprovals);

        menuPanel.add(btnDashboard);
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnAddAttendance);
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnAttSummary);
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnAddMedical);
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(btnApprovals);

        btnDashboard.setActive(true);

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Bottom Logout Button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JButton logoutBtn = new JButton("logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Draw door with exit arrow icon
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.8f));
                // Door frame
                g2.drawRect(14, 10, 10, 18);
                // Arrow pointing out
                g2.drawLine(20, 19, 28, 19);
                g2.drawLine(25, 16, 28, 19);
                g2.drawLine(25, 22, 28, 19);

                // Label
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                g2.drawString(getText(), 36, 23);
                g2.dispose();
            }
        };
        logoutBtn.setPreferredSize(new Dimension(200, 40));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder());
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> handleLogout());

        bottomPanel.add(logoutBtn, BorderLayout.SOUTH);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    // =========================================================================
    // TOP HEADER COMPONENT (MATCHING SCREENSHOT)
    // =========================================================================

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MAIN_BG);
        header.setBorder(new EmptyBorder(22, 35, 12, 35));

        headerTitleLabel = new JLabel("Technical Officer Dashboard");
        headerTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerTitleLabel.setForeground(TEXT_PRIMARY);
        header.add(headerTitleLabel, BorderLayout.WEST);

        // Role Badge on Right: "TECHNICAL OFFICER"
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rolePanel.setOpaque(false);

        JLabel roleLabel = new JLabel("TECHNICAL OFFICER");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleLabel.setForeground(TEAL_ACCENT);

        officerInfoLabel = new JLabel("(" + officerName + " - " + department + ")");
        officerInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        officerInfoLabel.setForeground(TEXT_MUTED);

        rolePanel.add(roleLabel);
        rolePanel.add(officerInfoLabel);

        header.add(rolePanel, BorderLayout.EAST);

        return header;
    }

    // =========================================================================
    // DASHBOARD OVERVIEW VIEW (MATCHING REFERENCE SCREENSHOT)
    // =========================================================================

    private JPanel createDashboardOverviewPanel() {
        JPanel overviewPanel = new JPanel(new BorderLayout(0, 20));
        overviewPanel.setBackground(MAIN_BG);
        overviewPanel.setBorder(new EmptyBorder(10, 35, 30, 35));

        // 1. Top Welcome Banner Card
        overviewPanel.add(createWelcomeCard(), BorderLayout.NORTH);

        // 2. 2x2 Feature Overview Cards Grid
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        gridPanel.setOpaque(false);

        // Card 1: Add Attendance
        FeatureCard card1 = new FeatureCard(
                "Add Attendance",
                "Record attendance for classes and sessions.",
                "ADD_ATTENDANCE",
                null, null, null,
                () -> navigateTo(CARD_ADD_ATTENDANCE)
        );

        // Card 2: Attendance Summary (With "Pending" status pill)
        FeatureCard card2 = new FeatureCard(
                "Attendance Summary",
                "View attendance reports and summaries.",
                "ATTENDANCE_SUMMARY",
                "Pending", PILL_YELLOW_BG, PILL_YELLOW_TEXT,
                () -> navigateTo(CARD_ATTENDANCE_SUMMARY)
        );

        // Card 3: Medical Records (With "Approved" status pill)
        FeatureCard card3 = new FeatureCard(
                "Medical Records",
                "Add and manage student medical records.",
                "ADD_MEDICAL",
                "Approved", PILL_GREEN_BG, PILL_GREEN_TEXT,
                () -> navigateTo(CARD_ADD_MEDICAL)
        );

        // Card 4: Pending Approvals (With "Reviewed" status pill)
        FeatureCard card4 = new FeatureCard(
                "Pending Approvals",
                "Review and approve medical records.",
                "MEDICAL_APPROVAL",
                "Reviewed", PILL_BLUE_BG, PILL_BLUE_TEXT,
                () -> navigateTo(CARD_MEDICAL_APPROVAL)
        );

        gridPanel.add(card1);
        gridPanel.add(card2);
        gridPanel.add(card3);
        gridPanel.add(card4);

        overviewPanel.add(gridPanel, BorderLayout.CENTER);

        return overviewPanel;
    }

    /**
     * Top Welcome Card with vector medical illustration matching screenshot.
     */
    private JPanel createWelcomeCard() {
        JPanel welcomeCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        welcomeCard.setOpaque(false);
        welcomeCard.setPreferredSize(new Dimension(0, 160));
        welcomeCard.setBorder(new EmptyBorder(25, 32, 25, 32));

        // Left side text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel welcomeTitle = new JLabel("Welcome Back, Technical Officer!");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeTitle.setForeground(TEXT_PRIMARY);

        JLabel welcomeSub = new JLabel("Manage attendance and medical records efficiently.");
        welcomeSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeSub.setForeground(TEXT_MUTED);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(welcomeTitle);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(welcomeSub);
        textPanel.add(Box.createVerticalGlue());

        welcomeCard.add(textPanel, BorderLayout.WEST);

        // Right side: Medical Illustration
        WelcomeIllustrationPanel illustration = new WelcomeIllustrationPanel();
        illustration.setPreferredSize(new Dimension(200, 120));
        welcomeCard.add(illustration, BorderLayout.EAST);

        return welcomeCard;
    }

    // =========================================================================
    // NAVIGATION LOGIC
    // =========================================================================

    public void navigateTo(String cardName) {
        cardLayout.show(cardsContainer, cardName);

        // Update Header Title
        switch (cardName) {
            case CARD_ADD_ATTENDANCE:
                headerTitleLabel.setText("Add Attendance");
                break;
            case CARD_ATTENDANCE_SUMMARY:
                headerTitleLabel.setText("Attendance Summary");
                break;
            case CARD_ADD_MEDICAL:
                headerTitleLabel.setText("Add Medical Record");
                break;
            case CARD_MEDICAL_APPROVAL:
                headerTitleLabel.setText("Medical Approvals");
                if (medicalApprovalPanel != null) {
                    medicalApprovalPanel.loadMedicalRecords();
                }
                break;
            case CARD_DASHBOARD:
            default:
                headerTitleLabel.setText("Technical Officer Dashboard");
                break;
        }

        // Update active state in sidebar buttons
        for (SidebarNavButton btn : navButtons) {
            btn.setActive(btn.getTargetCard().equals(cardName));
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of FTMS?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    // =========================================================================
    // INNER HELPER COMPONENTS (SIDEBAR BUTTONS & FEATURE CARDS)
    // =========================================================================

    /**
     * SidebarNavButton - Clean navigation button with teal active highlight and vector icons.
     */
    private class SidebarNavButton extends JButton {
        private final String targetCard;
        private final String iconType;
        private boolean active = false;

        public SidebarNavButton(String text, String iconType, String targetCard) {
            super(text);
            this.iconType = iconType;
            this.targetCard = targetCard;

            setPreferredSize(new Dimension(218, 44));
            setMaximumSize(new Dimension(218, 44));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder());
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 13));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!active) repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!active) repaint();
                }
            });

            addActionListener(e -> navigateTo(targetCard));
        }

        public String getTargetCard() {
            return targetCard;
        }

        public void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (active) {
                // Teal rounded pill highlight
                g2.setColor(TEAL_ACCENT);
                g2.fill(new RoundRectangle2D.Float(0, 2, w, h - 4, 12, 12));
                g2.setColor(Color.WHITE);
            } else if (getModel().isRollover()) {
                g2.setColor(SIDEBAR_ITEM_HOVER);
                g2.fill(new RoundRectangle2D.Float(0, 2, w, h - 4, 12, 12));
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(SIDEBAR_BG);
                g2.fillRect(0, 0, w, h);
                g2.setColor(new Color(0xD8, 0xE2, 0xEF));
            }

            // Draw Vector Icon on the left
            drawSidebarIcon(g2, iconType, 16, (h - 20) / 2, active);

            // Draw Text
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), 48, textY);

            g2.dispose();
        }

        private void drawSidebarIcon(Graphics2D g2, String type, int x, int y, boolean isActive) {
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.8f));

            if ("DASHBOARD".equals(type)) {
                // Speedometer / Gauge
                g2.draw(new Arc2D.Float(x, y + 2, 18, 18, 0, 180, Arc2D.OPEN));
                g2.drawLine(x + 9, y + 11, x + 14, y + 6);
                g2.fillOval(x + 7, y + 9, 4, 4);
            } else if ("ADD_ATTENDANCE".equals(type)) {
                // Clipboard with checkmark
                g2.drawRoundRect(x + 2, y + 2, 14, 17, 3, 3);
                g2.fillRect(x + 6, y, 6, 3);
                g2.drawLine(x + 6, y + 11, x + 8, y + 13);
                g2.drawLine(x + 8, y + 13, x + 12, y + 8);
            } else if ("ATTENDANCE_SUMMARY".equals(type)) {
                // 3 Bar charts
                g2.drawLine(x + 2, y + 18, x + 18, y + 18);
                g2.fillRect(x + 4, y + 10, 3, 8);
                g2.fillRect(x + 9, y + 6, 3, 12);
                g2.fillRect(x + 14, y + 2, 3, 16);
            } else if ("ADD_MEDICAL".equals(type)) {
                // Document with medical cross
                g2.drawRoundRect(x + 2, y + 1, 14, 18, 3, 3);
                g2.drawLine(x + 9, y + 6, x + 9, y + 14);
                g2.drawLine(x + 5, y + 10, x + 13, y + 10);
            } else if ("MEDICAL_APPROVAL".equals(type)) {
                // Shield
                Polygon shield = new Polygon();
                shield.addPoint(x + 9, y + 2);
                shield.addPoint(x + 16, y + 5);
                shield.addPoint(x + 16, y + 12);
                shield.addPoint(x + 9, y + 18);
                shield.addPoint(x + 2, y + 12);
                shield.addPoint(x + 2, y + 5);
                g2.drawPolygon(shield);
            }
        }
    }

    /**
     * FeatureCard - Interactive dashboard card strictly matching reference screenshot.
     */
    private class FeatureCard extends JPanel {
        private boolean hovered = false;

        public FeatureCard(String title, String subtitle, String iconType,
                           String pillText, Color pillBg, Color pillFg, Runnable onClick) {
            setLayout(new BorderLayout(15, 10));
            setBackground(CARD_BG);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(22, 22, 20, 22));

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

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (onClick != null) onClick.run();
                }
            });

            // Left circular icon
            JPanel iconCircle = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Soft light teal circle background
                    g2.setColor(new Color(0xE0, 0xF4, 0xF4));
                    g2.fillOval(0, 0, getWidth(), getHeight());

                    // Vector Icon inside
                    g2.setColor(TEAL_ACCENT);
                    g2.setStroke(new BasicStroke(2.0f));

                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;

                    if ("ADD_ATTENDANCE".equals(iconType)) {
                        g2.drawRoundRect(cx - 8, cy - 10, 16, 20, 3, 3);
                        g2.fillRect(cx - 4, cy - 12, 8, 3);
                        g2.drawLine(cx - 4, cy + 1, cx - 1, cy + 4);
                        g2.drawLine(cx - 1, cy + 4, cx + 4, cy - 2);
                    } else if ("ATTENDANCE_SUMMARY".equals(iconType)) {
                        g2.fillRect(cx - 9, cy + 2, 4, 8);
                        g2.fillRect(cx - 3, cy - 3, 4, 13);
                        g2.fillRect(cx + 3, cy - 9, 4, 19);
                    } else if ("ADD_MEDICAL".equals(iconType)) {
                        g2.drawRoundRect(cx - 8, cy - 11, 16, 22, 3, 3);
                        g2.drawLine(cx, cy - 4, cx, cy + 4);
                        g2.drawLine(cx - 4, cy, cx + 4, cy);
                    } else if ("MEDICAL_APPROVAL".equals(iconType)) {
                        Polygon s = new Polygon();
                        s.addPoint(cx, cy - 11);
                        s.addPoint(cx + 9, cy - 7);
                        s.addPoint(cx + 9, cy + 3);
                        s.addPoint(cx, cy + 11);
                        s.addPoint(cx - 9, cy + 3);
                        s.addPoint(cx - 9, cy - 7);
                        g2.drawPolygon(s);
                    }
                    g2.dispose();
                }
            };
            iconCircle.setOpaque(false);
            iconCircle.setPreferredSize(new Dimension(54, 54));
            add(iconCircle, BorderLayout.WEST);

            // Center: Title + Subtitle
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblTitle.setForeground(TEXT_PRIMARY);

            JLabel lblSub = new JLabel("<html>" + subtitle + "</html>");
            lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblSub.setForeground(TEXT_MUTED);

            textPanel.add(lblTitle);
            textPanel.add(Box.createVerticalStrut(4));
            textPanel.add(lblSub);
            add(textPanel, BorderLayout.CENTER);

            // Right: Chevron right arrow
            JLabel chevron = new JLabel(">") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(hovered ? TEAL_ACCENT : new Color(0x94, 0xA3, 0xB8));
                    g2.setStroke(new BasicStroke(2.2f));
                    int x = getWidth() / 2 - 2;
                    int y = getHeight() / 2;
                    g2.drawLine(x - 3, y - 6, x + 3, y);
                    g2.drawLine(x + 3, y, x - 3, y + 6);
                    g2.dispose();
                }
            };
            chevron.setPreferredSize(new Dimension(24, 24));
            add(chevron, BorderLayout.EAST);

            // Bottom Pill Badge if present
            if (pillText != null) {
                JPanel pillContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
                pillContainer.setOpaque(false);

                JLabel pillLabel = new JLabel(pillText) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(pillBg);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));

                        // Icon in pill
                        g2.setColor(pillFg);
                        g2.setStroke(new BasicStroke(1.5f));
                        int cy = getHeight() / 2;

                        if ("Pending".equals(getText())) {
                            // Clock icon
                            g2.drawOval(10, cy - 5, 10, 10);
                            g2.drawLine(15, cy - 2, 15, cy);
                            g2.drawLine(15, cy, 17, cy);
                        } else if ("Approved".equals(getText())) {
                            // Check icon
                            g2.drawLine(9, cy, 12, cy + 3);
                            g2.drawLine(12, cy + 3, 17, cy - 3);
                        } else if ("Reviewed".equals(getText())) {
                            // Eye icon
                            g2.drawOval(9, cy - 4, 10, 8);
                            g2.fillOval(12, cy - 2, 4, 4);
                        }

                        // Text
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        g2.drawString(getText(), 25, cy + 4);
                        g2.dispose();
                    }
                };
                pillLabel.setPreferredSize(new Dimension(92, 26));
                pillContainer.add(pillLabel);
                add(pillContainer, BorderLayout.SOUTH);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(CARD_BG);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));

            // Hover border glow
            if (hovered) {
                g2.setColor(TEAL_ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
            } else {
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
            g2.dispose();
        }
    }

    /**
     * Vector illustration matching the clipboard and first-aid medical kit in the reference screenshot.
     */
    private static class WelcomeIllustrationPanel extends JPanel {
        public WelcomeIllustrationPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // 1. Soft pastel cyan circle background
            g2.setColor(new Color(0xE8, 0xF7, 0xF7));
            g2.fillOval(w - 170, 5, 140, 110);

            // 2. Clipboard (Back document)
            int cbX = w - 145;
            int cbY = 15;
            int cbW = 65;
            int cbH = 85;

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(cbX, cbY, cbW, cbH, 8, 8);
            g2.setColor(new Color(0x00, 0x7A, 0x87));
            g2.setStroke(new BasicStroke(2.2f));
            g2.drawRoundRect(cbX, cbY, cbW, cbH, 8, 8);

            // Clipboard top clip
            g2.setColor(new Color(0x00, 0x9B, 0x9E));
            g2.drawRoundRect(cbX + 20, cbY - 5, 25, 10, 4, 4);

            // Checklist lines on clipboard
            g2.setColor(new Color(0x94, 0xDE, 0xDE));
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawLine(cbX + 14, cbY + 22, cbX + 50, cbY + 22);
            g2.drawLine(cbX + 14, cbY + 32, cbX + 50, cbY + 32);
            g2.drawLine(cbX + 14, cbY + 42, cbX + 50, cbY + 42);

            // 3. Medical First Aid Kit (Front box)
            int kitX = w - 95;
            int kitY = 48;
            int kitW = 60;
            int kitH = 48;

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(kitX, kitY, kitW, kitH, 10, 10);
            g2.setColor(new Color(0x00, 0x7A, 0x87));
            g2.setStroke(new BasicStroke(2.2f));
            g2.drawRoundRect(kitX, kitY, kitW, kitH, 10, 10);

            // Kit handle
            g2.drawRoundRect(kitX + 18, kitY - 6, 24, 8, 4, 4);

            // Medical Cross inside kit
            int crossCx = kitX + kitW / 2;
            int crossCy = kitY + kitH / 2;
            g2.setColor(new Color(0x00, 0x9B, 0x9E));
            g2.fillRect(crossCx - 3, crossCy - 9, 6, 18);
            g2.fillRect(crossCx - 9, crossCy - 3, 18, 6);

            g2.dispose();
        }
    }

    // =========================================================================
    // MAIN METHOD TO LAUNCH AND TEST
    // =========================================================================

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            TechnicalOfficerDashboard dashboard = new TechnicalOfficerDashboard();
            dashboard.setVisible(true);
        });
    }
}
