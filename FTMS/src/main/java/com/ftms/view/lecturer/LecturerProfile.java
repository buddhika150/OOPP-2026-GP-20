package com.ftms.view.lecturer;

import com.ftms.database.DatabaseConnection;
import com.ftms.view.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.File;


public class LecturerProfile extends JFrame {


    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField departmentField;
    private JLabel photoLabel;

    JPanel main_panel = new JPanel(new BorderLayout());    //slide bar and dear balence main panel

    private int lectureId;

    public LecturerProfile(int lecturerId) {

        this.lectureId = lecturerId;

        setupWindow();
        CreateHeading();
        CreaterSidebar();
        CreateProfilePanel();
        load_profile_ditails();

        this.add(main_panel, BorderLayout.CENTER);  //slide bar and hear balence main panel;

        setVisible(true);

    }


    //window
    private void setupWindow() {

        setTitle("Lecturer Profile");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);            //open windo dispay center
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color bgcolor = new Color(245, 249, 252);
        this.getContentPane().setBackground(bgcolor);

        this.setLayout(new BorderLayout());


    }

private void CreateHeading(){

    JPanel heading_panel = new JPanel(new BorderLayout());
    heading_panel.setPreferredSize(new Dimension(500,200));
    heading_panel.setBackground(new Color(245, 249, 252));
    heading_panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 30));


    JLabel Heading =  new JLabel();
    Heading.setText(" Lecturer Profile");
    Heading.setVerticalAlignment(JLabel.CENTER);

    Color h_color = new Color(16,45,79);
    Heading.setForeground(h_color);
    Heading.setFont(new Font("Arial",Font.BOLD,50));

    heading_panel.add(Heading);

    URL headin_image_path = getClass().getResource("/images/header_image.png");
    ImageIcon heading_icon = new ImageIcon(headin_image_path);
    JLabel image_label = new JLabel(heading_icon);
    heading_panel.add(image_label, BorderLayout.EAST);



    main_panel.add(heading_panel,BorderLayout.NORTH);

}



    private void CreateProfilePanel() {

        // Main Profile Panel
        JPanel profile_panel = new JPanel(new BorderLayout(35, 20));
        profile_panel.setBackground(Color.WHITE);
        profile_panel.setPreferredSize(new Dimension(1100, 480));
        profile_panel.setBorder(
                BorderFactory.createEmptyBorder(40, 45, 40, 45)
        );


        // Details Panel
        JPanel details = new JPanel(new GridLayout(5, 2, 25, 18));
        details.setBackground(Color.WHITE);

        Font labelFont = new Font("Arial", Font.BOLD, 20);
        Font fieldFont = new Font("Arial", Font.PLAIN, 19);
        Color textColor = new Color(16, 45, 79);

        // Name
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(textColor);
        details.add(nameLabel);

        nameField = new JTextField();
        nameField.setFont(fieldFont);
        details.add(nameField);

        // Lecturer ID
        JLabel idLabel = new JLabel("Lecturer ID");
        idLabel.setFont(labelFont);
        idLabel.setForeground(textColor);
        details.add(idLabel);

        JTextField idField = new JTextField(String.valueOf(lectureId));
        idField.setFont(fieldFont);
        idField.setEditable(false);
        details.add(idField);

        // Email
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(textColor);
        details.add(emailLabel);

        emailField = new JTextField();
        emailField.setFont(fieldFont);
        details.add(emailField);

        // Phone
        JLabel phoneLabel = new JLabel("Phone");
        phoneLabel.setFont(labelFont);
        phoneLabel.setForeground(textColor);
        details.add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setFont(fieldFont);
        details.add(phoneField);

        // Department
        JLabel departmentLabel = new JLabel("Department");
        departmentLabel.setFont(labelFont);
        departmentLabel.setForeground(textColor);
        details.add(departmentLabel);

        departmentField = new JTextField();
        departmentField.setFont(fieldFont);
        details.add(departmentField);


        JButton updateButton = new JButton("Update Profile");

        updateButton.setBackground(new Color(0, 143, 156));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFont(new Font("Arial", Font.BOLD, 16));

        updateButton.addActionListener(e -> {
            updateProfile();

        });

        profile_panel.add(updateButton, BorderLayout.SOUTH);

        profile_panel.add(details, BorderLayout.CENTER);

        // Center Panel
        JPanel center_panel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 20, 35)
        );

        center_panel.setBackground(new Color(245, 249, 252));
        center_panel.add(profile_panel);

        main_panel.add(center_panel, BorderLayout.CENTER);



        // Profile photo
        JPanel photo_panel = new JPanel(new BorderLayout(10, 10));
        photo_panel.setBackground(Color.WHITE);
        photo_panel.setPreferredSize(new Dimension(240, 300));

        photoLabel = new JLabel("Profile Photo", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(220, 220));
        photoLabel.setOpaque(true);
        photoLabel.setBackground(new Color(220, 235, 245));

        JButton upload_button = new JButton("Upload Photo");
        upload_button.setBackground(new Color(0, 143, 156));
        upload_button.setForeground(Color.WHITE);

        upload_button.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();

            chooser.setFileFilter(
                    new javax.swing.filechooser.FileNameExtensionFilter(
                            "Image Files", "jpg", "jpeg", "png"
                    )
            );

            int result = chooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {

                File file = chooser.getSelectedFile();

                savePhoto(file);

                load_profile_ditails();
            }
        });

        // Add photo and button to panel
        photo_panel.add(photoLabel, BorderLayout.CENTER);
        photo_panel.add(upload_button, BorderLayout.SOUTH);

        // Add photo panel to profile
        profile_panel.add(photo_panel, BorderLayout.WEST);




    }



    private void savePhoto(File file) {

        try {
            // Create folder
            File folder = new File("profile_photos");
            folder.mkdir();

            // Photo name
            String fileName = file.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

            String photoName = "lecturer_" + lectureId + extension;
            // Photo path
            File newFile = new File(folder, photoName);

            // Copy photo
            Files.copy(file.toPath(), newFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            // Database update
            Connection con = DatabaseConnection.getConnection();

            String sql = "UPDATE `user` SET Profile_pic=? WHERE User_ID=?";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, newFile.getPath());
            pst.setInt(2, lectureId);

            pst.executeUpdate();

            pst.close();
            con.close();

            JOptionPane.showMessageDialog(this, "Photo Saved");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }


    private void updateProfile() {

        try {
            Connection con = DatabaseConnection.getConnection();

            String sql = "UPDATE `user` SET Fname=?, Lname=?, Email=?, Phone=? WHERE User_ID=?";

            PreparedStatement pst = con.prepareStatement(sql);

            String fullName = nameField.getText().trim();
            String[] names = fullName.split("\\s+", 2);

            String firstName = names[0];
            String lastName = names.length > 1 ? names[1] : "";

            pst.setString(1, firstName);
            pst.setString(2, lastName);
            pst.setString(3, emailField.getText());
            pst.setString(4, phoneField.getText());
            pst.setInt(5, lectureId);

            int rows = pst.executeUpdate();

            pst.close();

            String sql2 = "UPDATE lecturer SET Department=? WHERE Lecturer_ID=?";

            PreparedStatement pst2 = con.prepareStatement(sql2);

            pst2.setString(1, departmentField.getText());
            pst2.setInt(2, lectureId);

            pst2.executeUpdate();

            pst2.close();
            con.close();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Profile Updated Successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Lecturer not found");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
        private void load_profile_ditails() {



            try {
                Connection con = DatabaseConnection.getConnection();

                String sql = "SELECT * FROM user u " +
                        "JOIN lecturer l ON u.User_ID = l.Lecturer_ID " +
                        "WHERE u.User_ID = ?";

                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, lectureId);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String fname = rs.getString("Fname");
                    String lname = rs.getString("Lname");

                    nameField.setText(fname + " " + lname);
                    emailField.setText(rs.getString("Email"));
                    phoneField.setText(rs.getString("Phone"));
                    departmentField.setText(rs.getString("Department"));

                    String photoPath = rs.getString("Profile_pic");

                    if (photoPath != null && !photoPath.isEmpty()) {

                        File photoFile = new File(photoPath);

                        if (photoFile.exists()) {

                            ImageIcon image = new ImageIcon(photoPath);

                            int width = image.getIconWidth();
                            int height = image.getIconHeight();

                            if (width > 0 && height > 0) {

                                double scale = Math.min(220.0 / width, 250.0 / height);

                                Image img = image.getImage().getScaledInstance(
                                        (int) (width * scale),
                                        (int) (height * scale),
                                        Image.SCALE_SMOOTH
                                );

                                photoLabel.setText("");
                                photoLabel.setIcon(new ImageIcon(img));
                            }
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Lecturer not found");
                }

                rs.close();
                pst.close();
                con.close();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //sidebar
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private  void CreaterSidebar(){

        JPanel sidebar = new JPanel();
        Color sidebar_color = new Color(16,45,79);

        sidebar.setBackground(sidebar_color);

        this.add(sidebar , BorderLayout.WEST);


        // FTMS text

        JLabel sidebar_headr_text = new JLabel("FTMS");
        sidebar_headr_text.setForeground(Color.WHITE);
        sidebar_headr_text.setFont(new Font("Arial",Font.BOLD,40));
        sidebar.add(sidebar_headr_text);


        //FACULTY OF TECHNOLOGY
        //MANAGEMENT SYSTEM   text

        JLabel sidebar_dear_text_2 = new JLabel("FACULTY OF TECHNOLOGY");
        sidebar_dear_text_2.setForeground(Color.WHITE);
        sidebar_dear_text_2.setFont(new Font("Arial",Font.BOLD,15));
        sidebar.add(sidebar_dear_text_2);


        JLabel sidebar_dear_text_3 = new JLabel("MANAGEMENT SYSTEM");
        sidebar_dear_text_3.setForeground(Color.WHITE);
        sidebar_dear_text_3.setFont(new Font("Arial",Font.BOLD,15));
        sidebar.add(sidebar_dear_text_3);



        //side bar button common variable

        Dimension sidebar_size = new Dimension(250,500);
        sidebar.setPreferredSize(sidebar_size);


        Color sidebar_button_color = new Color(0,143,156);
        Dimension sidebar_button_size = new Dimension(215,40);

        //Dashbord
        JButton dashbord_button = new JButton("Dashbord");
        dashbord_button.setFont(new Font("Arial",Font.BOLD,15));

        sidebar.add(dashbord_button);

        dashbord_button.setBackground(sidebar_button_color);
        dashbord_button.setForeground(Color.WHITE);

        dashbord_button.setPreferredSize(sidebar_button_size);

        dashbord_button.addActionListener( e -> {
            new LecturerDashboard(lectureId);
            this.setVisible(false);
        });


        URL Dash_path = getClass().getResource("/images/house.png");
        ImageIcon dashbord_icon = new ImageIcon(Dash_path);
        dashbord_button.setIcon(dashbord_icon);


        dashbord_button.setHorizontalAlignment(SwingConstants.LEFT);
        dashbord_button.setIconTextGap(15);





        //my course

        JButton my_course_button = new JButton("My Course");
        sidebar.add(my_course_button);

        my_course_button.setPreferredSize(sidebar_button_size);
        my_course_button.setBackground(sidebar_button_color);
        my_course_button.setForeground(Color.WHITE);

        URL manage_path = getClass().getResource("/images/book.png");
        ImageIcon manage_icon = new ImageIcon(manage_path);
        my_course_button.setIcon(manage_icon);

        my_course_button.setHorizontalAlignment(SwingConstants.LEFT);
        my_course_button.setIconTextGap(15);

        my_course_button.addActionListener(e -> {
            new MyCourses(lectureId);
            this.setVisible(false);
        });


        //course material

        JButton course_material_button = new JButton("Upload Material");
        sidebar.add(course_material_button);

        course_material_button.setPreferredSize(sidebar_button_size);
        course_material_button.setBackground(sidebar_button_color);
        course_material_button.setForeground(Color.WHITE);

        URL course_path = getClass().getResource("/images/file.png");
        ImageIcon course_icon = new ImageIcon(course_path);
        course_material_button.setIcon(course_icon);

        course_material_button.setHorizontalAlignment(SwingConstants.LEFT);
        course_material_button.setIconTextGap(15);

        course_material_button.addActionListener(e -> {
            new CourseMaterials(lectureId);
            this.setVisible(false);
        });


        //Upload marks

        JButton upload_marks_button = new JButton("Upload Marks");
        sidebar.add(upload_marks_button);

        upload_marks_button.setPreferredSize(sidebar_button_size);
        upload_marks_button.setBackground(sidebar_button_color);
        upload_marks_button.setForeground(Color.WHITE);

        URL upload_mark_path = getClass().getResource("/images/upload_marks.png");
        ImageIcon uploadmark_icon = new ImageIcon(upload_mark_path);
        upload_marks_button.setIcon(uploadmark_icon);

        upload_marks_button.setHorizontalAlignment(SwingConstants.LEFT);
        upload_marks_button.setIconTextGap(15);

        upload_marks_button.addActionListener(e -> {
            new UploadMarks(lectureId);
            this.setVisible(false);


        });


        //Attendance  summary

        JButton attendance_button = new JButton("Attendance Summary");
        sidebar.add(attendance_button);

        attendance_button.setPreferredSize(sidebar_button_size);
        attendance_button.setBackground(sidebar_button_color);
        attendance_button.setForeground(Color.WHITE);

        URL attendance_image_path = getClass().getResource("/images/attendance_summary.png");
        ImageIcon attendance_icon = new ImageIcon(attendance_image_path);
        attendance_button.setIcon(attendance_icon);

        attendance_button.setHorizontalAlignment(SwingConstants.LEFT);
        attendance_button.setIconTextGap(15);

        attendance_button.addActionListener(e -> {
            new LecturerAttendanceSummary(lectureId);
            this.setVisible(false);


        });



        //Student Eligibility

        JButton eligibility_button = new JButton("Student Eligibility");
        sidebar.add(eligibility_button);

        eligibility_button.setPreferredSize(sidebar_button_size);
        eligibility_button.setBackground(sidebar_button_color);
        eligibility_button.setForeground(Color.WHITE);

        URL eligibility_image_path = getClass().getResource("/images/student_eligibility.png");
        ImageIcon eligibility_icon = new ImageIcon(eligibility_image_path);
        eligibility_button.setIcon(eligibility_icon);

        eligibility_button.setHorizontalAlignment(SwingConstants.LEFT);
        eligibility_button.setIconTextGap(15);

        eligibility_button.addActionListener(e -> {

            new LecturerStudentEligibility(lectureId);
            this.setVisible(false);


        });


        //Profile button

        JButton  profile_button= new JButton("Profile");
        sidebar.add(profile_button);

        profile_button.setPreferredSize(sidebar_button_size);
        profile_button.setBackground(sidebar_button_color);
        profile_button.setForeground(Color.WHITE);

        URL profile_image_path = getClass().getResource("/images/profile.png");
        ImageIcon profile_icon = new ImageIcon(profile_image_path);
        profile_button.setIcon(profile_icon);

        profile_button.setHorizontalAlignment(SwingConstants.LEFT);
        profile_button.setIconTextGap(15);

        profile_button.addActionListener(e -> {

            new LecturerProfile(lectureId);
            this.setVisible(false);


        });


        //Student gpa button

        JButton  student_gpa_button= new JButton("Student Marks and GPA");
        sidebar.add(student_gpa_button);

        student_gpa_button.setPreferredSize(sidebar_button_size);
        student_gpa_button.setBackground(sidebar_button_color);
        student_gpa_button.setForeground(Color.WHITE);

        URL student_gpa_path = getClass().getResource("/images/gpa.png");
        ImageIcon student_gpa_icon = new ImageIcon(student_gpa_path);
        student_gpa_button.setIcon(student_gpa_icon);

        student_gpa_button.setHorizontalAlignment(SwingConstants.LEFT);
        student_gpa_button.setIconTextGap(15);

        student_gpa_button.addActionListener(e -> {

            new Lecturer_Student_Marks_GPA(lectureId);
            this.setVisible(false);


        });


        //student medicle button

        JButton  student_medicle_button= new JButton("Student Medicle");
        sidebar.add(student_medicle_button);

        student_medicle_button.setPreferredSize(sidebar_button_size);
        student_medicle_button.setBackground(sidebar_button_color);
        student_medicle_button.setForeground(Color.WHITE);

        URL student_madicle_path = getClass().getResource("/images/medicle.png");
        ImageIcon student_medicle_icon = new ImageIcon(student_madicle_path);
        student_medicle_button.setIcon(student_medicle_icon);

        student_medicle_button.setHorizontalAlignment(SwingConstants.LEFT);
        student_medicle_button.setIconTextGap(15);

        student_medicle_button.addActionListener(e -> {

            new Lecturer_student_medicle(lectureId);
            this.setVisible(false);


        });


        //student ditais button

        JButton  student_ditails_button= new JButton("Student Details");
        sidebar.add(student_ditails_button);

        student_ditails_button.setPreferredSize(sidebar_button_size);
        student_ditails_button.setBackground(sidebar_button_color);
        student_ditails_button.setForeground(Color.WHITE);

        URL student_ditails_path = getClass().getResource("/images/ditals.png");
        ImageIcon student_ditails_icon = new ImageIcon(student_ditails_path);
        student_ditails_button.setIcon(student_ditails_icon);

        student_ditails_button.setHorizontalAlignment(SwingConstants.LEFT);
        student_ditails_button.setIconTextGap(15);

        student_ditails_button.addActionListener(e -> {

            new Lecturer_Student_Details(lectureId);
            this.setVisible(false);


        });


        //student notice button

        JButton notice_button= new JButton("Notice");
        sidebar.add(notice_button);

        notice_button.setPreferredSize(sidebar_button_size);
        notice_button.setBackground(sidebar_button_color);
        notice_button.setForeground(Color.WHITE);

        URL notice_path = getClass().getResource("/images/notice.png");
        ImageIcon notice_icon = new ImageIcon(notice_path);
        notice_button.setIcon(notice_icon);

        notice_button.setHorizontalAlignment(SwingConstants.LEFT);
        notice_button.setIconTextGap(15);

        notice_button.addActionListener(e -> {

            new Lecturer_Notice_View(lectureId);
            this.setVisible(false);


        });



        //log out

        JButton logout_button = new JButton("Logout");
        sidebar.add(logout_button);

        logout_button.setPreferredSize(sidebar_button_size);
        logout_button.setBackground(sidebar_button_color);
        logout_button.setForeground(Color.WHITE);


        URL log_path = getClass().getResource("/images/logout.png");
        ImageIcon log_icon = new ImageIcon(log_path);
        logout_button.setIcon(log_icon);


        logout_button.setHorizontalAlignment(SwingConstants.LEFT);
        logout_button.setIconTextGap(15);

        logout_button.addActionListener(e -> {

            // Open Login Frame
            new LoginFrame().setVisible(true);

            // Close Dashboard
            this.dispose();

        });




    }


}





