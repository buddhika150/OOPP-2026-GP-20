import model.Course;
import model.CourseMaterial;
import gui.LecturerDashboard;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Demo {
    public static void main(String[] args){

        CourseMaterial m1 = new CourseMaterial(
                101,
                "ICT2132",
                "Introduction to OOP",
                "PDF",
                "materials/lesson01.pdf",
                new Date()
        );

            new LecturerDashboard();



    }

}

