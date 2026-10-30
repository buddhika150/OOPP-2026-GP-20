package com.ftms.view.lecturer;
import  javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.awt.Desktop;

import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;

import com.ftms.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.ftms.model.Add_course_material;


public class CourseMaterials extends JFrame {


    JPanel main_panel = new JPanel(new BorderLayout(10,10));

    JPanel top_panel = new JPanel(new BorderLayout(10,10));

    private int lectureId;


  public   CourseMaterials(int lecturerId) {

      this.lectureId = lecturerId;

      setupwindow();
      create_heading();
      CreaterSidebar();
      //create_material_upload();
      create_upload_button();
     material_tabel();


      this.add(main_panel,BorderLayout.CENTER);
      main_panel.add(top_panel,BorderLayout.NORTH);

      setVisible(true);

    }

    private void setupwindow(){

        setTitle("courses Materials");
        setSize(800 ,500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10 , 10));
        getContentPane().setBackground(new Color(245, 249, 252));


    }


    private void create_heading (){

        JPanel heading_panel = new JPanel(new BorderLayout());
        Dimension heading_size = new Dimension(500,200);
        heading_panel.setPreferredSize(heading_size);
        heading_panel.setBackground(Color.WHITE);



        JLabel heading_label = new JLabel(" course Materials");
        heading_label.setFont(new Font("arial",Font.BOLD,50));
        heading_panel.add(heading_label,BorderLayout.WEST);
        top_panel.add(heading_panel, BorderLayout.NORTH);



        URL course_material_imge_path = getClass().getResource("/images/corse_upload_image.png");
        ImageIcon heading_image = new ImageIcon(course_material_imge_path);
        JLabel image_label = new JLabel(heading_image);
        heading_panel.add(image_label,BorderLayout.EAST);


    }



    private void create_upload_button(){

      JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT,40,40));
      button_panel.setPreferredSize(new Dimension(0,110));
      button_panel.setBackground(Color.WHITE);

      JButton material_upload_button = new JButton("Upload Material");

        URL pluse =  getClass().getResource("/images/plus.png");
        ImageIcon pluse_icon = new ImageIcon(pluse);
        material_upload_button.setIcon(pluse_icon);

      button_panel.add(material_upload_button);
      material_upload_button.setForeground(Color.WHITE);
      material_upload_button.setBackground(new Color(0,143,156));
      material_upload_button.setFont(new Font("arial",Font.BOLD,18));
      top_panel.add(button_panel,BorderLayout.SOUTH);

      material_upload_button.setPreferredSize(new Dimension(250,80));

      material_upload_button.addActionListener(e -> {

          new Add_course_material(lectureId);

      });

    }







//delet option

    class DeleteButton extends JButton
            implements javax.swing.table.TableCellRenderer {

        public DeleteButton() {
            setText("Delete");
            setBackground(Color.RED);
            setForeground(Color.WHITE);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            return this;


        }
    }



private  void material_tabel(){

        String[] colums = {
                "Material ID",
                "Course Code",
                "Lecturer ID",
                "Title",
                "Type",
                "Uploaded Date",
                "Delete"
        };



        DefaultTableModel model = new DefaultTableModel(colums , 0);

        JTable material_tabel = new JTable(model);

    material_tabel.getColumnModel().getColumn(6).setCellRenderer(new DeleteButton());

    material_tabel.addMouseListener(new java.awt.event.MouseAdapter() {

        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {

            int row = material_tabel.rowAtPoint(e.getPoint());
            int column = material_tabel.columnAtPoint(e.getPoint());

            if (row == -1 || column != 6) {
                return;
            }

            int modelRow = material_tabel.convertRowIndexToModel(row);

            int id = Integer.parseInt(
                    model.getValueAt(modelRow, 0).toString()
            );

            deleteMaterial(id, model, modelRow);
        }
    });




        material_tabel.setRowHeight(35);
        material_tabel.setFont(new Font("Arial", Font.PLAIN, 18));
        material_tabel.getTableHeader().setFont(new Font("Arial",Font.BOLD,20));

        JScrollPane scrollPane = new JScrollPane(material_tabel);
        main_panel.add(scrollPane);


        try{

            Connection con = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM course_material WHERE Lecturer_ID = ?";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, lectureId);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){

                Object[] row = {
                        rs.getInt("Material_ID"),
                        rs.getString("Course_code"),
                        rs.getInt("Lecturer_ID"),
                        rs.getString("Title"),
                        rs.getString("Material_type"),
                        rs.getDate("Uploaded_date"),
                        "Delete"

                };

                model.addRow(row);
//lil
            }

            rs.close();
            pst.close();
            con.close();



        }catch (Exception ex){

            JOptionPane.showMessageDialog(this, ex.getMessage());

        };




}


    private void deleteMaterial(int id, DefaultTableModel model, int row) {

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            Connection con = DatabaseConnection.getConnection();

            String sql = "DELETE FROM course_material WHERE Material_ID = ? AND Lecturer_ID = ?";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, id);
            pst.setInt(2, lectureId);

            int result = pst.executeUpdate();

            pst.close();
            con.close();

            if (result > 0) {
                model.removeRow(row);
                JOptionPane.showMessageDialog(this, "Material Deleted Successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Material not found");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }


/*
    private void create_material_upload(){

      JPanel material_upload_panel = new JPanel(new GridLayout(0,2,15,15));
      material_upload_panel.setPreferredSize(new Dimension(650,200));

      main_panel.add(material_upload_panel,BorderLayout.CENTER);



        //cours_coad  add

      JLabel course_coad_label = new JLabel("Course Code:");
      JTextField course_coad_Field  = new JTextField(15);

      material_upload_panel.add(course_coad_label);
      material_upload_panel.add(course_coad_Field);

      //Lecture id

      JLabel lecturer_id_label = new JLabel("Lecturer ID:");
        JTextField lecturer_id_field = new JTextField(10);

        material_upload_panel.add(lecturer_id_label);
        material_upload_panel.add(lecturer_id_field);

      //material title add

        JLabel material_title_label = new JLabel("Material Title");
        JTextField material_title_filed = new JTextField(15);
        material_upload_panel.add(material_title_label);
        material_upload_panel.add(material_title_filed);


        //material type

        JLabel material_type_label = new JLabel("Material Type");
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

      JButton upload_button = new JButton("Upload Material");
      material_upload_panel.add(upload_button);

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


*/


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


    });



    //Student Eligibility

    JButton eligibility_button = new JButton("Attendance Summary");
    sidebar.add(attendance_button);

    eligibility_button.setPreferredSize(sidebar_button_size);
    eligibility_button.setBackground(sidebar_button_color);
    eligibility_button.setForeground(Color.WHITE);

    URL eligibility_image_path = getClass().getResource("/images/student_eligibility.png");
    ImageIcon eligibility_icon = new ImageIcon(eligibility_image_path);
    eligibility_button.setIcon(eligibility_icon);

    eligibility_button.setHorizontalAlignment(SwingConstants.LEFT);
    eligibility_button.setIconTextGap(15);

    eligibility_button.addActionListener(e -> {



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


}



}

