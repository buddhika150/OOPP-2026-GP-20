package com.ftms.model;

import com.ftms.database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;



public class Add_course_material extends JFrame {

    private int lecturerId;

    public Add_course_material(int lecturerId) {

        this.lecturerId = lecturerId;



        add_mateial();
        create_material_upload();

        setVisible(true);

    }


    private void add_mateial() {

        setTitle("Upload Material");
        setSize(700, 800);
        setLocationRelativeTo(null);
       setResizable(false);
       setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().setBackground(Color.white);



    }

    private void create_material_upload(){

        //1st panel

        JPanel form_bc_panel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
        form_bc_panel.setBackground(Color.WHITE);

        //form main label
        JLabel add_new_matatereal_label = new JLabel("Add New Material");
        add_new_matatereal_label.setFont(new Font("arial",Font.BOLD,30));
        form_bc_panel.add(add_new_matatereal_label);


        // 2nd panel
        JPanel material_upload_panel = new JPanel(new GridLayout(0,1,10,10));
        material_upload_panel.setPreferredSize(new Dimension(700,600));
        material_upload_panel.setBackground(Color.WHITE);

        material_upload_panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        form_bc_panel.add(material_upload_panel);
        this.add(form_bc_panel,BorderLayout.CENTER);


        //cours_coad  add

        JLabel course_coad_label = new JLabel("Course Code:");
        course_coad_label.setFont(new Font("arial",Font.BOLD,18));

        JTextField course_coad_Field  = new JTextField(15);
        course_coad_Field.setFont(new Font("Arial", Font.PLAIN, 20));


        material_upload_panel.add(course_coad_label);
        material_upload_panel.add(course_coad_Field);


        //Lecture id

        JLabel lecturer_id_label = new JLabel("Lecturer ID:");
        JTextField lecturer_id_field = new JTextField(15);

        lecturer_id_label.setFont(new Font("arial",Font.BOLD,18));
        lecturer_id_field.setFont(new Font("Arial", Font.PLAIN, 20));

        material_upload_panel.add(lecturer_id_label);
        material_upload_panel.add(lecturer_id_field);


        //material title add

        JLabel material_title_label = new JLabel("Material Title");
        JTextField material_title_filed = new JTextField(15);
        material_title_label.setFont(new Font("arial",Font.BOLD,18));
        material_title_filed.setFont(new Font("Arial", Font.PLAIN, 20));

        material_upload_panel.add(material_title_label);
        material_upload_panel.add(material_title_filed);


        //material type

        JLabel material_type_label = new JLabel("Material Type");
        material_type_label.setFont(new Font("arial",Font.BOLD,18));

        String[] material_type =  {"PDF", "PPT", "DOCX", "Video", "Other"};
        JComboBox<String> type_of_materil_dropdown = new JComboBox<>(material_type);


        material_upload_panel.add(material_type_label);
        material_upload_panel.add(type_of_materil_dropdown);

        //choose file

        JTextField file_path_field = new  JTextField(20);
        file_path_field.setEditable(false);
        material_upload_panel.add(file_path_field);

        JButton choose_file_button = new JButton("Choose File");
        material_upload_panel.add(choose_file_button);

        choose_file_button.addActionListener(e -> {

            JFileChooser choose_file = new JFileChooser();
            int result = choose_file.showOpenDialog(this);

            if(result == JFileChooser.APPROVE_OPTION){

                String filepath =  choose_file.getSelectedFile().getAbsolutePath();


                file_path_field.setText(filepath);

            }

        });

        //upload button
        JButton upload_button = new JButton("Upload Material");
        upload_button.setPreferredSize(new Dimension(200,60));
        upload_button.setBackground(new Color(0,143,156));
        upload_button.setForeground(Color.WHITE);
        upload_button.setFont(new Font("arial",Font.BOLD,18));
        form_bc_panel.add(upload_button);

        upload_button.addActionListener(e -> {


            int lecturer_id;

            try {
                lecturer_id = Integer.parseInt(lecturer_id_field.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid Lecturer ID");
                return;
            }

            String course_coad = course_coad_Field.getText();

            String title = material_title_filed.getText();

            String materialtype = type_of_materil_dropdown.getSelectedItem().toString();

            String file_path = file_path_field.getText();


            if (course_coad.trim().isEmpty() ||
                    title.trim().isEmpty() ||                     //all box fill and space remove
                    file_path.trim().isEmpty()) {

                JOptionPane.showMessageDialog(this, "Please fill all fields");

                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Course Code: " + course_coad +
                            "\nTitle: " + title +
                            "\nMaterial Type: " + materialtype +
                            "\nFile Path: " + file_path);


            try{

                Connection con = DatabaseConnection.getConnection();
                JOptionPane.showMessageDialog(this,"Database Connected");

                String sql = "INSERT INTO course_material " + "(Course_code, Lecturer_ID, Title, Material_type, File_path, Uploaded_date) " + "VALUES (?, ?, ?, ?, ?, ?)";

                PreparedStatement pst = con.prepareStatement(sql);

                pst.setString(1,course_coad);
                pst.setInt(2,lecturer_id);
                pst.setString(3,title);
                pst.setString(4,materialtype);
                pst.setString(5,file_path);
                pst.setDate(6,new java.sql.Date(System.currentTimeMillis()));


                int rows = pst.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Material Saved Successfully!");
                }


            }
            catch (Exception ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }




        });





    }

}
