package com.ftms.view.student;

import com.ftms.dao.CourseDAO;
import com.ftms.util.SessionManager;
import javax.swing.*;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CoursesPanel extends JPanel {
    private final DefaultTableModel coursesTableModel;
    private final JPanel cardsGridPanel;

    public CoursesPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(25, 30, 25, 30));
        setBackground(new Color(240, 242, 245));

        // Title section
        JLabel lblTitle = new JLabel("My Enrolled Courses");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 60, 75));
        add(lblTitle, BorderLayout.NORTH);

        // Main Card Container Panel with Grid Layout (2 columns)
        cardsGridPanel = new JPanel(new GridLayout(0, 2, 25, 25));
        cardsGridPanel.setBackground(new Color(240, 242, 245));

        JScrollPane scrollPane = new JScrollPane(cardsGridPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);

        // Initialize table model with original column names
        String[] columns = {"Course Code", "Course Name"};
        coursesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Listen for table model changes to dynamically refresh the card view
        coursesTableModel.addTableModelListener(e -> refreshCourseCards());

        // Initial render
        refreshCourseCards();
        loadCourses();
    }

    public void loadCourses() {
        int studentId = SessionManager.getInstance().getLoggedInUserId();
        if (studentId < 0) {
            coursesTableModel.setRowCount(0);
            showMessage("Please log in to view your courses.");
            return;
        }

        showMessage("Loading courses...");
        new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() throws Exception {
                return new CourseDAO().getCoursesByStudentId(studentId);
            }

            @Override
            protected void done() {
                try {
                    List<String[]> courses = get();
                    coursesTableModel.setRowCount(0);
                    for (String[] course : courses) {
                        coursesTableModel.addRow(course);
                    }
                    refreshCourseCards();
                } catch (Exception ex) {
                    coursesTableModel.setRowCount(0);
                    showMessage("Could not load courses. Check the database connection.");
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void showMessage(String message) {
        cardsGridPanel.removeAll();
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        cardsGridPanel.add(label);
        cardsGridPanel.revalidate();
        cardsGridPanel.repaint();
    }

    private void refreshCourseCards() {
        cardsGridPanel.removeAll();

        int rowCount = coursesTableModel.getRowCount();
        if (rowCount == 0) {
            JLabel emptyLabel = new JLabel("No enrolled courses found.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(100, 110, 125));
            cardsGridPanel.add(emptyLabel);
        } else {
            ImageIcon courseIcon = null;
            java.net.URL imgUrl = getClass().getResource("/images/Icon.png");
            if (imgUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                Image scaledImg = originalIcon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                courseIcon = new ImageIcon(scaledImg);
            }

            for (int i = 0; i < rowCount; i++) {
                String code = (String) coursesTableModel.getValueAt(i, 0);
                String name = (String) coursesTableModel.getValueAt(i, 1);

                JPanel singleCard = createCourseCard(code, name, courseIcon);
                cardsGridPanel.add(singleCard);
            }
        }

        cardsGridPanel.revalidate();
        cardsGridPanel.repaint();
    }

    private JPanel createCourseCard(String code, String name, ImageIcon icon) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 235), 1, true),
                new EmptyBorder(25, 20, 25, 20)
        ));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Course Icon Label
        JLabel lblIcon = new JLabel();
        if (icon != null) {
            lblIcon.setIcon(icon);
        } else {
            lblIcon.setText("[Icon]");
        }
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Course Code Label
        JLabel lblCode = new JLabel(code != null ? code : "-");
        lblCode.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCode.setForeground(new Color(40, 45, 55));
        lblCode.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Course Name Label
        JLabel lblName = new JLabel(name != null ? name : "-");
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblName.setForeground(new Color(100, 110, 125));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblIcon);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(lblCode);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblName);

        return card;
    }

    public DefaultTableModel getCoursesTableModel() {
        return coursesTableModel;
    }
}