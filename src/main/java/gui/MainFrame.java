package gui;

import gui.cave.CaveController;
import gui.cave.CavePanel;
import gui.maze.MazeController;
import gui.maze.MazePanel;
import java.awt.*;
import javax.swing.*;

/** Главное окно приложения с вкладками для лабиринтов и пещер. */
public class MainFrame extends JFrame {
  public MainFrame() {
    super("Maze & Cave Generator");
    initUI();
  }

  private void initUI() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(880, 580));
    setResizable(false);

    JTabbedPane tabbedPane = new JTabbedPane();

    // 1. Вкладка "Лабиринт"
    MazeController mazeController = new MazeController();
    MazePanel mazePanel = new MazePanel(mazeController);
    tabbedPane.addTab("Лабиринт", mazePanel);

    // 2. Вкладка "Пещера"
    CaveController caveController = new CaveController();
    CavePanel cavePanel = new CavePanel(caveController);
    tabbedPane.addTab("Пещера", cavePanel);

    add(tabbedPane, BorderLayout.CENTER);

    pack();
    setLocationRelativeTo(null);
  }
}
