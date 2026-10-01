import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class BMICalculatorApp extends JFrame {
    private JRadioButton radioMetric, radioEnglish;
    private JTextField txtWeight, txtHeight;
    private JLabel lblBmi, lblStatus, lblWUnit, lblHUnit;
    private JPanel resultPanel, refPanel;

    public BMICalculatorApp() {
        setTitle("BMI Calculator Application");
        setSize(500, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // 1. Unit System Section
        JPanel unitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        unitPanel.add(new JLabel("Unit System:  "));
        radioMetric = new JRadioButton("Metric System", true);
        radioEnglish = new JRadioButton("English System");
        ButtonGroup group = new ButtonGroup();
        group.add(radioMetric); group.add(radioEnglish);
        unitPanel.add(radioMetric); unitPanel.add(radioEnglish);
        mainPanel.add(unitPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // 2. Input Fields Section
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtWeight = new JTextField(15);
        lblWUnit = new JLabel("(kg / lbs)");
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Weight:       "), gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(txtWeight, gbc);
        gbc.gridx = 2; gbc.gridy = 0; inputPanel.add(lblWUnit, gbc);

        txtHeight = new JTextField(15);
        lblHUnit = new JLabel("(m / inches)");
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Height:       "), gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(txtHeight, gbc);
        gbc.gridx = 2; gbc.gridy = 1; inputPanel.add(lblHUnit, gbc);
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // 3. Calculate Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnCalculate = new JButton("Calculate");
        btnCalculate.setPreferredSize(new Dimension(140, 30));
        buttonPanel.add(btnCalculate);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // 4. Output Results Display Panel
        resultPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        resultPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        resultPanel.setBackground(new Color(245, 245, 245));
        resultPanel.setVisible(false);

        lblBmi = new JLabel("Your Calculated BMI: --");
        lblStatus = new JLabel("Status Health Class: --");
        lblBmi.setFont(new Font("Arial", Font.BOLD, 13));
        lblStatus.setFont(new Font("Arial", Font.BOLD, 13));

        JPanel innerResult = new JPanel(new GridLayout(2, 1, 5, 5));
        innerResult.setOpaque(false);
        innerResult.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        innerResult.add(lblBmi);
        innerResult.add(lblStatus);
        resultPanel.add(innerResult);
        mainPanel.add(resultPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // 5. Reference Standards Panel ()
        refPanel = new JPanel(new BorderLayout());
        TitledBorder titleBorder = BorderFactory.createTitledBorder("Official Health Standards Reference Table:");
        titleBorder.setTitleFont(new Font("Arial", Font.PLAIN, 12));
        refPanel.setBorder(titleBorder);
        refPanel.setVisible(false);

        JPanel labelGrid = new JPanel(new GridLayout(2, 2, 10, 5));
        labelGrid.add(new JLabel("* Underweight: < 18.5"));
        labelGrid.add(new JLabel("* Overweight: 25.0 - 29.9"));
        labelGrid.add(new JLabel("* Normal: 18.5 - 24.9"));
        labelGrid.add(new JLabel("* Obese: >= 30.0"));
        refPanel.add(labelGrid, BorderLayout.CENTER);
        mainPanel.add(refPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Unit switcher
        ActionListener unitSwitcher = e -> {
            if (radioMetric.isSelected()) {
                lblWUnit.setText("(kg)"); lblHUnit.setText("(m)");
            } else {
                lblWUnit.setText("(lbs)"); lblHUnit.setText("(inches)");
            }
        };
        radioMetric.addActionListener(unitSwitcher);
        radioEnglish.addActionListener(unitSwitcher);

        // Calculate Action
        btnCalculate.addActionListener(e -> {
            try {
                double w = Double.parseDouble(txtWeight.getText());
                double h = Double.parseDouble(txtHeight.getText());
                boolean isMetric = radioMetric.isSelected();

                BMI bmiObj = new BMI(w, h, isMetric);

                lblBmi.setText(String.format("Your Calculated BMI: %.1f", bmiObj.calculate()));
                lblStatus.setText("Status Health Class: " + bmiObj.getStatus());


                resultPanel.setVisible(true);
                refPanel.setVisible(true);


                revalidate();
                repaint();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid values for Weight and Height.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }


}
