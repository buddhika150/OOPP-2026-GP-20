package gui;
import javax.swing.*;
import java.awt.BorderLayout;


public class LecturerDashboard extends JFrame{

    public LecturerDashboard(){

        setupWindow();
        CreateHeading();


        setVisible(true);

}

//window
private void setupWindow(){

        setTitle("Lecturer Dashboard");
        setSize(800 , 500);
        setLocationRelativeTo(null);            //open windo dispay center
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void CreateHeading(){

        JLabel Heading =  new JLabel();
        Heading.setText("Welcome Lecturer");
        Heading.setHorizontalAlignment(JLabel.CENTER);  // Left -> center
        Heading.setVerticalAlignment(JLabel.TOP);


        add(Heading , BorderLayout.NORTH);

    }



}
