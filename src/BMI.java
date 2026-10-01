public class BMI {
    private double weight;
    private double height;
    private boolean isMetric;

    public BMI(double weight, double height, boolean isMetric) {
        this.weight = weight;
        this.height = height;
        this.isMetric = isMetric;
    }

    public double calculate() {
        if (isMetric) {
            return weight / (height * height);
        } else {
            return (weight * 703) / (height * height);
        }
    }

    public String getStatus() {
        double bmi = calculate();
        if (bmi < 18.5) return "Underweight";
        if (bmi <= 24.9) return "Normal Weight";
        if (bmi <= 29.9) return "Overweight";
        return "Obese";
    }
}
