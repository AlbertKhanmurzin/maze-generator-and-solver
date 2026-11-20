package main;

import com.formdev.flatlaf.FlatLightLaf;
import gui.MainFrame;
import javax.swing.*;

public class Main {
  public static void main(String[] args) {
    try {
      FlatLightLaf.setup();
    } catch (Exception ignored) {
    }

    SwingUtilities.invokeLater(() -> {
      MainFrame frame = new MainFrame();
      frame.setVisible(true);
    });
  }
}
