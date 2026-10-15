package ftms.gui;

import javax.swing.*;
import java.awt.*;

public class UiUtils {
    public static void showError(Component parent, String message, String title) {
        System.err.println("[" + title + "] " + message);
        if (parent != null && parent.isShowing() && !GraphicsEnvironment.isHeadless()) {
            JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void showInfo(Component parent, String message, String title) {
        System.out.println("[" + title + "] " + message);
        if (parent != null && parent.isShowing() && !GraphicsEnvironment.isHeadless()) {
            JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
