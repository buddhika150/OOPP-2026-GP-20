package com.ftms.view.technicalofficer;

import com.ftms.dao.AttendanceDAO;
import com.ftms.model.TechnicalOfficer;
import com.ftms.dao.TimetableDAO;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;





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
    private static final String CARD_PROFILE = "PROFILE";
    private static final String CARD_ATTENDANCE = "ATTENDANCE";
    private static final String CARD_SUMMARY = "SUMMARY";
    private static final String CARD_MEDICAL = "MEDICAL";
    private static final String CARD_APPROVAL = "APPROVAL";
    private static final String CARD_NOTICES = "NOTICES";
    private static final String CARD_TIMETABLE = "TIMETABLE";

    private TechnicalOfficer currentOfficer;
    private final AttendanceDAO profileDAO;

    private CardLayout cardLayout;
    private JPanel contentCardsPanel;
    private JLabel lblPageTitle;
    private JLabel lblRoleBadge;
    private JLabel lblWelcomeTitle;

    // My Profile fields
    private JLabel lblProfileHeaderName;
    private JLabel lblProfileHeaderSub;
    private JLabel lblProfileAvatar;
    private JTextField txtProfileToId;
    private JTextField txtProfileUsername;
    private JTextField txtProfileRole;
    private JTextField txtProfileDepartment;
    private JTextField txtProfileFname;
    private JTextField txtProfileLname;
    private JTextField txtProfileEmail;
    private JTextField txtProfilePhone;
    private JTextField txtProfilePicPath;

    private AddAttendancePanel addAttendancePanel;
    private AttendanceSummaryPanel attendanceSummaryPanel;
    private AddMedicalPanel addMedicalPanel;
    private MedicalApprovalPanel medicalApprovalPanel;

    private final Map<String, SidebarNavButton> navButtons = new LinkedHashMap<>();

    public TechnicalOfficerDashboard() {
        this(null);
    }

    public TechnicalOfficerDashboard(TechnicalOfficer officer) {
        this.profileDAO = new AttendanceDAO();
        this.currentOfficer = resolveFullOfficerProfile(officer);
        initFrame();
        initComponents();
        navigateTo(CARD_DASHBOARD, "Technical Officer Dashboard");
    }


    private TechnicalOfficer resolveFullOfficerProfile(TechnicalOfficer passedOfficer) {
        TechnicalOfficer candidate = passedOfficer;

        // If null, check if Member 1's SessionManager holds a logged-in User
        if (candidate == null) {
            try {
                Class<?> smClass = Class.forName("com.ftms.util.SessionManager");
                Object currentUser = smClass.getMethod("getCurrentUser").invoke(null);
                if (currentUser instanceof TechnicalOfficer toUser) {
                    candidate = toUser;
                }
            } catch (Exception ignored) {
            }
        }

        if (candidate != null) {
            TechnicalOfficer dbProfile = profileDAO.getTechnicalOfficerProfile(
                    candidate.getToId(), candidate.getUsername()
            );
            if (dbProfile != null) {
                return dbProfile;
            }
            return candidate;
        }

        // Default fallback to TO_ID = 7 (to01) if launched standalone without LoginFrame
        throw new IllegalStateException(
                "Technical Officer login session not found. Please login again."
        );
    }

    private void initFrame() {
        setTitle("FTMS - Technical Officer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        lblRoleBadge = new JLabel(getRoleBadgeString());
        lblRoleBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRoleBadge.setForeground(TEAL_ACCENT);
        lblRoleBadge.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRoleBadge.setToolTipText("Click to view Technical Officer Profile");
        lblRoleBadge.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateTo(CARD_PROFILE, "Technical Officer Profile");
            }
        });

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
        contentCardsPanel.add(createProfilePanel(), CARD_PROFILE);
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

    private String getRoleBadgeString() {
        if (currentOfficer != null && currentOfficer.getDepartment() != null && !currentOfficer.getDepartment().isEmpty()) {
            return "TECHNICAL OFFICER (" + currentOfficer.getDepartment() + ")";
        }
        return "TECHNICAL OFFICER";
    }

    private String getOfficerDisplayName() {
        if (currentOfficer != null) {
            if (currentOfficer.getFname() != null && !currentOfficer.getFname().isEmpty()) {
                return currentOfficer.getFname();
            }
            if (currentOfficer.getUsername() != null && !currentOfficer.getUsername().isEmpty()) {
                return currentOfficer.getUsername();
            }
        }
        return "Technical Officer";
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
        lblSubBrand.setBorder(new EmptyBorder(0, 8, 26, 0));

        topSection.add(lblBrand);
        topSection.add(lblSubBrand);

        addNavButton(topSection, CARD_DASHBOARD, "Dashboard", "Technical Officer Dashboard", IconType.DASHBOARD);
        addNavButton(topSection, CARD_PROFILE, "My Profile", "Technical Officer Profile", IconType.USERS);
        addNavButton(topSection, CARD_ATTENDANCE, "Manage Attendance", "Attendance Management", IconType.USERS);
        addNavButton(topSection, CARD_SUMMARY, "Attendance Summary", "Attendance Summary", IconType.COURSES);
        addNavButton(topSection, CARD_MEDICAL, "Medical Records", "Medical Records Management", IconType.MEDICAL);
        addNavButton(topSection, CARD_APPROVAL,
                "Medical Approval", "Medical Approval", IconType.APPROVAL);
        addNavButton(topSection, CARD_NOTICES, " Notices", "Faculty Notices", IconType.NOTICES);
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
        btnLogout.setBorder(new EmptyBorder(12, 42, 8, 14));
        btnLogout.addActionListener(e -> handleLogout());
        bottomSection.add(btnLogout, BorderLayout.CENTER);

        sidebar.add(bottomSection, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addNavButton(JPanel parent, String cardKey, String label, String pageTitle, IconType iconType) {
        SidebarNavButton btn = new SidebarNavButton(label, iconType);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.addActionListener(e -> navigateTo(cardKey, pageTitle));

        navButtons.put(cardKey, btn);
        parent.add(btn);
        parent.add(Box.createVerticalStrut(5));
    }

    public void navigateTo(String cardKey, String pageTitle) {
        lblPageTitle.setText(pageTitle);
        for (Map.Entry<String, SidebarNavButton> entry : navButtons.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(cardKey));
        }

        if (CARD_PROFILE.equals(cardKey)) {
            populateProfileFields();
        } else if (CARD_ATTENDANCE.equals(cardKey) && addAttendancePanel != null) {
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
        welcomeCard.setLayout(new BorderLayout(20, 0));
        welcomeCard.setBorder(new EmptyBorder(20, 24, 20, 24));
        welcomeCard.setPreferredSize(new Dimension(0, 128));

        JPanel leftBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        leftBanner.setOpaque(false);

        CircleBadgeIcon bannerIcon = new CircleBadgeIcon(80, ICON_CIRCLE_BG, NAVY_DARK, IconType.INSTITUTE);
        leftBanner.add(bannerIcon);

        JPanel welcomeTextPanel = new JPanel();
        welcomeTextPanel.setOpaque(false);
        welcomeTextPanel.setLayout(new BoxLayout(welcomeTextPanel, BoxLayout.Y_AXIS));

        lblWelcomeTitle = new JLabel("Welcome Back, " + getOfficerDisplayName() + "!");
        lblWelcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        lblWelcomeTitle.setForeground(NAVY_DARK);

        JLabel lblWelcomeSub = new JLabel("Manage your faculty activities in one place.");
        lblWelcomeSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcomeSub.setForeground(TEXT_MUTED);

        welcomeTextPanel.add(lblWelcomeTitle);
        welcomeTextPanel.add(Box.createVerticalStrut(6));
        welcomeTextPanel.add(lblWelcomeSub);

        leftBanner.add(welcomeTextPanel);
        welcomeCard.add(leftBanner, BorderLayout.WEST);

        JPanel rightBanner = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 24));
        rightBanner.setOpaque(false);
        JButton btnViewProfile = createPrimaryButton("My Profile", TEAL_ACCENT, 125);
        btnViewProfile.addActionListener(e -> navigateTo(CARD_PROFILE, "Technical Officer Profile"));
        rightBanner.add(btnViewProfile);
        welcomeCard.add(rightBanner, BorderLayout.EAST);

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
                "Add & manage student medical records",
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

    /**
     * Creates the Technical Officer Profile panel ("My Profile" tab).
     */
    private JPanel createProfilePanel() {
        JPanel profileRoot = new JPanel(new BorderLayout(0, 18));
        profileRoot.setOpaque(false);

        // 1. Top Officer Summary Banner Card
        RoundedCardPanel topProfileCard = new RoundedCardPanel(14, CARD_BG, CARD_BORDER);
        topProfileCard.setLayout(new BorderLayout(20, 0));
        topProfileCard.setBorder(new EmptyBorder(20, 26, 20, 26));

        JPanel avatarAndInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        avatarAndInfo.setOpaque(false);

        lblProfileAvatar = new JLabel();
        lblProfileAvatar.setPreferredSize(new Dimension(84, 84));
        lblProfileAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblProfileAvatar.setVerticalAlignment(SwingConstants.CENTER);
        lblProfileAvatar.setOpaque(true);
        lblProfileAvatar.setBackground(ICON_CIRCLE_BG);
        lblProfileAvatar.setForeground(NAVY_DARK);
        lblProfileAvatar.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblProfileAvatar.setBorder(new LineBorder(CARD_BORDER, 2, true));

        JPanel infoBox = new JPanel();
        infoBox.setOpaque(false);
        infoBox.setLayout(new BoxLayout(infoBox, BoxLayout.Y_AXIS));

        lblProfileHeaderName = new JLabel();
        lblProfileHeaderName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblProfileHeaderName.setForeground(NAVY_DARK);

        lblProfileHeaderSub = new JLabel();
        lblProfileHeaderSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblProfileHeaderSub.setForeground(TEXT_MUTED);

        infoBox.add(lblProfileHeaderName);
        infoBox.add(Box.createVerticalStrut(6));
        infoBox.add(lblProfileHeaderSub);

        avatarAndInfo.add(lblProfileAvatar);
        avatarAndInfo.add(infoBox);
        topProfileCard.add(avatarAndInfo, BorderLayout.WEST);

        profileRoot.add(topProfileCard, BorderLayout.NORTH);

        // 2. Profile Details & Edit Form Card
        JPanel detailsCard = new JPanel(new BorderLayout(0, 16));
        detailsCard.setBackground(CARD_BG);
        detailsCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(22, 26, 22, 26)
        ));

        JLabel lblFormHeading = new JLabel("Technical Officer Account & Personal Details");
        lblFormHeading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormHeading.setForeground(NAVY_DARK);
        detailsCard.add(lblFormHeading, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtProfileToId = createProfileTextField(false);
        txtProfileUsername = createProfileTextField(false);
        txtProfileRole = createProfileTextField(false);
        txtProfileDepartment = createProfileTextField(true);
        txtProfileFname = createProfileTextField(true);
        txtProfileLname = createProfileTextField(true);
        txtProfileEmail = createProfileTextField(true);
        txtProfilePhone = createProfileTextField(true);
        txtProfilePicPath = createProfileTextField(false);

        // Row 1: TO ID, Username, Role
        addProfileField(formGrid, gbc, 0, 0, 1, "Technical Officer ID (TO_ID)", txtProfileToId);
        addProfileField(formGrid, gbc, 1, 0, 1, "Username", txtProfileUsername);
        addProfileField(formGrid, gbc, 2, 0, 1, "System Role", txtProfileRole);

        // Row 2: First Name, Last Name, Department
        addProfileField(formGrid, gbc, 0, 1, 1, "First Name (Fname)", txtProfileFname);
        addProfileField(formGrid, gbc, 1, 1, 1, "Last Name (Lname)", txtProfileLname);
        addProfileField(formGrid, gbc, 2, 1, 1, "Department", txtProfileDepartment);

        // Row 3: Email, Phone, Profile Picture
        addProfileField(formGrid, gbc, 0, 2, 1, "Email Address", txtProfileEmail);
        addProfileField(formGrid, gbc, 1, 2, 1, "Phone Number", txtProfilePhone);

        JPanel picPickerWrap = new JPanel(new BorderLayout(6, 0));
        picPickerWrap.setOpaque(false);
        JButton btnBrowsePic = createPrimaryButton("Browse...", TEAL_ACCENT, 100);
        btnBrowsePic.addActionListener(e -> chooseProfilePicture());
        picPickerWrap.add(txtProfilePicPath, BorderLayout.CENTER);
        picPickerWrap.add(btnBrowsePic, BorderLayout.EAST);
        addProfileField(formGrid, gbc, 2, 2, 1, "Profile Picture", picPickerWrap);

        JPanel formNorthWrap = new JPanel(new BorderLayout());
        formNorthWrap.setOpaque(false);
        formNorthWrap.add(formGrid, BorderLayout.NORTH);
        detailsCard.add(formNorthWrap, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        JButton btnReload = createPrimaryButton("Reload from DB", new Color(108, 122, 137), 145);
        JButton btnSaveProfile = createPrimaryButton("Update Profile", TEAL_ACCENT, 145);

        btnReload.addActionListener(e -> reloadProfileFromDatabase());
        btnSaveProfile.addActionListener(e -> saveProfileChanges());

        actionsPanel.add(btnReload);
        actionsPanel.add(btnSaveProfile);

        detailsCard.add(actionsPanel, BorderLayout.SOUTH);
        profileRoot.add(detailsCard, BorderLayout.CENTER);

        populateProfileFields();
        return profileRoot;
    }

    private void addProfileField(JPanel panel, GridBagConstraints gbc, int col, int row, int width,
                                 String labelText, JComponent comp) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(NAVY_DARK);

        comp.setPreferredSize(new Dimension(180, 36));
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(comp, BorderLayout.CENTER);

        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = width;
        panel.add(wrapper, gbc);
        gbc.gridwidth = 1;
    }

    private JTextField createProfileTextField(boolean editable) {
        JTextField tf = new JTextField();
        tf.setEditable(editable);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(NAVY_DARK);
        tf.setBackground(editable ? Color.WHITE : new Color(240, 244, 248));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(206, 216, 226), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        return tf;
    }

    private void chooseProfilePicture() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Profile Picture");
        chooser.setFileFilter(new FileNameExtensionFilter("Images (PNG, JPG, JPEG)", "png", "jpg", "jpeg"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            txtProfilePicPath.setText(file.getAbsolutePath());
            updateAvatarPreview(file.getAbsolutePath());
        }
    }

    private void updateAvatarPreview(String imagePath) {
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                ImageIcon rawIcon = new ImageIcon(imgFile.getAbsolutePath());
                Image scaled = rawIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                lblProfileAvatar.setText("");
                lblProfileAvatar.setIcon(new ImageIcon(scaled));
                return;
            }
        }
        lblProfileAvatar.setIcon(null);
        String initials = "TO";
        if (currentOfficer != null && currentOfficer.getFname() != null && !currentOfficer.getFname().isEmpty()) {
            initials = currentOfficer.getFname().substring(0, 1).toUpperCase();
            if (currentOfficer.getLname() != null && !currentOfficer.getLname().isEmpty()) {
                initials += currentOfficer.getLname().substring(0, 1).toUpperCase();
            }
        }
        lblProfileAvatar.setText(initials);
    }

    private void populateProfileFields() {
        if (currentOfficer == null) {
            return;
        }
        String fullName = ((currentOfficer.getFname() != null ? currentOfficer.getFname() : "") + " "
                + (currentOfficer.getLname() != null ? currentOfficer.getLname() : "")).trim();
        if (fullName.isEmpty()) {
            fullName = currentOfficer.getUsername();
        }

        lblProfileHeaderName.setText(fullName);
        lblProfileHeaderSub.setText("TO ID: #" + currentOfficer.getToId()
                + "  |  Username: " + currentOfficer.getUsername()
                + "  |  Department: " + (currentOfficer.getDepartment() != null ? currentOfficer.getDepartment() : "N/A"));

        txtProfileToId.setText(String.valueOf(currentOfficer.getToId()));
        txtProfileUsername.setText(currentOfficer.getUsername() != null ? currentOfficer.getUsername() : "");
        txtProfileRole.setText(currentOfficer.getRole() != null ? currentOfficer.getRole() : "TECHNICAL_OFFICER");
        txtProfileDepartment.setText(currentOfficer.getDepartment() != null ? currentOfficer.getDepartment() : "");
        txtProfileFname.setText(currentOfficer.getFname() != null ? currentOfficer.getFname() : "");
        txtProfileLname.setText(currentOfficer.getLname() != null ? currentOfficer.getLname() : "");
        txtProfileEmail.setText(currentOfficer.getEmail() != null ? currentOfficer.getEmail() : "");
        txtProfilePhone.setText(currentOfficer.getPhone() != null ? currentOfficer.getPhone() : "");
        txtProfilePicPath.setText(currentOfficer.getProfilePic() != null ? currentOfficer.getProfilePic() : "");

        updateAvatarPreview(currentOfficer.getProfilePic());
    }

    private void reloadProfileFromDatabase() {
        if (currentOfficer == null) {
            return;
        }
        TechnicalOfficer refreshed = profileDAO.getTechnicalOfficerProfile(
                currentOfficer.getToId(), currentOfficer.getUsername()
        );
        if (refreshed != null) {
            this.currentOfficer = refreshed;
            populateProfileFields();
            lblWelcomeTitle.setText("Welcome Back, " + getOfficerDisplayName() + "!");
            lblRoleBadge.setText(getRoleBadgeString());
        }
    }

    private void saveProfileChanges() {
        if (currentOfficer == null) {
            return;
        }
        try {
            currentOfficer.setFname(txtProfileFname.getText().trim());
            currentOfficer.setLname(txtProfileLname.getText().trim());
            currentOfficer.setDepartment(txtProfileDepartment.getText().trim());
            currentOfficer.setEmail(txtProfileEmail.getText().trim());
            currentOfficer.setPhone(txtProfilePhone.getText().trim());
            currentOfficer.setProfilePic(txtProfilePicPath.getText().trim());

            if (profileDAO.updateTechnicalOfficerProfile(currentOfficer)) {
                populateProfileFields();
                lblWelcomeTitle.setText("Welcome Back, " + getOfficerDisplayName() + "!");
                lblRoleBadge.setText(getRoleBadgeString());
                JOptionPane.showMessageDialog(this,
                        "Technical Officer profile updated successfully!",
                        "Profile Updated", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update profile in database.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JButton createPrimaryButton(String text, Color bg, int width) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 36));
        return btn;
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

        try {
            String sql = "SELECT * FROM Notice";

            try (java.sql.Connection con =
                         com.ftms.database.DatabaseConnection.getConnection();
                 java.sql.PreparedStatement ps = con.prepareStatement(sql);
                 java.sql.ResultSet rs = ps.executeQuery()) {

                model.setRowCount(0);

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4)
                    });
                }
            }

        } catch (java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading notices: " + ex.getMessage()
            );
        }

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

        JLabel lblHeading = new JLabel(
                "Department Course Timetable (" + currentOfficer.getDepartment() + ")"
        );
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

        try {
            TimetableDAO dao = new TimetableDAO();

            for (Object[] row : dao.getByDepartment(currentOfficer.getDepartment())) {
                model.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading timetable: " + ex.getMessage()
            );
        }

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
            try {
                Class<?> loginClass = Class.forName("com.ftms.view.LoginFrame");
                Object frame = loginClass.getDeclaredConstructor().newInstance();
                if (frame instanceof JFrame) {
                    ((JFrame) frame).setVisible(true);
                }
            } catch (Exception ignored) {
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
        SwingUtilities.invokeLater(() -> new com.ftms.view.LoginFrame().setVisible(true));
    }
}
