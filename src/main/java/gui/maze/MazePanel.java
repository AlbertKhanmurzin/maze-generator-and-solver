package gui.maze;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Вкладка для работы с лабиринтом: объединяет холст и панель управления. */
public class MazePanel extends JPanel {
  private final MazeCanvas canvas;
  private final MazeController controller;

  private JSpinner rowsSpinner;
  private JSpinner colsSpinner;

  private JSpinner startXSpinner;
  private JSpinner startYSpinner;
  private JSpinner finishXSpinner;
  private JSpinner finishYSpinner;

  private JLabel statusLabel;

  public MazePanel(MazeController controller) {
    this.controller = controller;
    this.canvas = new MazeCanvas();
    this.controller.setView(this);

    setLayout(new BorderLayout(15, 15));
    setBorder(new EmptyBorder(15, 15, 15, 15));

    // Левая часть: холст отрисовки
    JPanel canvasWrapper = new JPanel(new GridBagLayout());
    canvasWrapper.setBackground(new Color(245, 245, 245));
    canvasWrapper.setBorder(BorderFactory.createEtchedBorder());
    canvasWrapper.add(canvas);
    add(canvasWrapper, BorderLayout.CENTER);

    // Правая часть: панель управления
    JPanel controlPanel = createControlPanel();
    add(controlPanel, BorderLayout.EAST);

    // Слушатель клика на холсте
    canvas.setCellClickListener((x, y) -> controller.handleCanvasClick(x, y));
  }

  private JPanel createControlPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setPreferredSize(new Dimension(320, 500));

    // 1. Секция файлов
    JPanel fileSection = new JPanel(new GridLayout(1, 2, 8, 8));
    fileSection.setBorder(new TitledBorder("Файл лабиринта"));

    JButton loadBtn = new JButton("Загрузить...");
    JButton saveBtn = new JButton("Сохранить...");

    loadBtn.addActionListener(e -> onOpenFile());
    saveBtn.addActionListener(e -> onSaveFile());

    fileSection.add(loadBtn);
    fileSection.add(saveBtn);
    panel.add(fileSection);
    panel.add(Box.createVerticalStrut(10));

    // 2. Секция генерации
    JPanel genSection = new JPanel(new GridBagLayout());
    genSection.setBorder(new TitledBorder("Генерация (алгоритм Эллера)"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(4, 4, 4, 4);

    rowsSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
    colsSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0.5;
    genSection.add(new JLabel("Строк (Y):"), gbc);
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 0.5;
    genSection.add(rowsSpinner, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0.5;
    genSection.add(new JLabel("Столбцов (X):"), gbc);
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 0.5;
    genSection.add(colsSpinner, gbc);

    JButton generateBtn = new JButton("Сгенерировать");
    generateBtn.addActionListener(e -> {
      int rows = (int) rowsSpinner.getValue();
      int cols = (int) colsSpinner.getValue();
      controller.generate(rows, cols);
    });

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    genSection.add(generateBtn, gbc);

    panel.add(genSection);
    panel.add(Box.createVerticalStrut(10));

    // 3. Секция поиска решения
    JPanel solveSection = new JPanel(new GridBagLayout());
    solveSection.setBorder(new TitledBorder("Решение лабиринта"));
    GridBagConstraints sgbc = new GridBagConstraints();
    sgbc.fill = GridBagConstraints.HORIZONTAL;
    sgbc.insets = new Insets(4, 4, 4, 4);

    JLabel hintLabel =
        new JLabel(
            "<html><i>Кликните по полю для выбора<br>старта (S) и " + "финиша (F)</i></html>");
    sgbc.gridx = 0;
    sgbc.gridy = 0;
    sgbc.gridwidth = 4;
    solveSection.add(hintLabel, sgbc);

    startXSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 49, 1));
    startYSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 49, 1));
    finishXSpinner = new JSpinner(new SpinnerNumberModel(9, 0, 49, 1));
    finishYSpinner = new JSpinner(new SpinnerNumberModel(9, 0, 49, 1));

    sgbc.gridwidth = 1;
    sgbc.gridx = 0;
    sgbc.gridy = 1;
    solveSection.add(new JLabel("Старт X:"), sgbc);
    sgbc.gridx = 1;
    sgbc.gridy = 1;
    solveSection.add(startXSpinner, sgbc);
    sgbc.gridx = 2;
    sgbc.gridy = 1;
    solveSection.add(new JLabel("Y:"), sgbc);
    sgbc.gridx = 3;
    sgbc.gridy = 1;
    solveSection.add(startYSpinner, sgbc);

    sgbc.gridx = 0;
    sgbc.gridy = 2;
    solveSection.add(new JLabel("Финиш X:"), sgbc);
    sgbc.gridx = 1;
    sgbc.gridy = 2;
    solveSection.add(finishXSpinner, sgbc);
    sgbc.gridx = 2;
    sgbc.gridy = 2;
    solveSection.add(new JLabel("Y:"), sgbc);
    sgbc.gridx = 3;
    sgbc.gridy = 2;
    solveSection.add(finishYSpinner, sgbc);

    JPanel btnRow = new JPanel(new GridLayout(1, 2, 6, 6));
    JButton solveBtn = new JButton("Найти путь");
    JButton clearBtn = new JButton("Очистить");

    solveBtn.addActionListener(e -> {
      int sx = (int) startXSpinner.getValue();
      int sy = (int) startYSpinner.getValue();
      int fx = (int) finishXSpinner.getValue();
      int fy = (int) finishYSpinner.getValue();
      controller.solve(sx, sy, fx, fy);
    });

    clearBtn.addActionListener(e -> controller.clearSolution());

    btnRow.add(solveBtn);
    btnRow.add(clearBtn);

    sgbc.gridx = 0;
    sgbc.gridy = 3;
    sgbc.gridwidth = 4;
    solveSection.add(btnRow, sgbc);

    panel.add(solveSection);
    panel.add(Box.createVerticalGlue());

    // 4. Секция статуса
    statusLabel = new JLabel("Готов к работе");
    statusLabel.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), new EmptyBorder(8, 8, 8, 8)));
    statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(statusLabel);

    return panel;
  }

  private void onOpenFile() {
    JFileChooser fileChooser = new JFileChooser(".");
    fileChooser.setDialogTitle("Выберите файл лабиринта");
    fileChooser.setFileFilter(new FileNameExtensionFilter("Текстовые файлы (*.txt)", "txt"));
    int res = fileChooser.showOpenDialog(this);
    if (res == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      controller.loadFromFile(selectedFile);
    }
  }

  private void onSaveFile() {
    JFileChooser fileChooser = new JFileChooser(".");
    fileChooser.setDialogTitle("Сохранить лабиринт в файл");
    fileChooser.setFileFilter(new FileNameExtensionFilter("Текстовые файлы (*.txt)", "txt"));
    int res = fileChooser.showSaveDialog(this);
    if (res == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      if (!selectedFile.getName().endsWith(".txt")) {
        selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
      }
      controller.saveToFile(selectedFile);
    }
  }

  public void updateDimensions(int rows, int cols) {
    rowsSpinner.setValue(rows);
    colsSpinner.setValue(cols);
    ((SpinnerNumberModel) startXSpinner.getModel()).setMaximum(cols - 1);
    ((SpinnerNumberModel) startYSpinner.getModel()).setMaximum(rows - 1);
    ((SpinnerNumberModel) finishXSpinner.getModel()).setMaximum(cols - 1);
    ((SpinnerNumberModel) finishYSpinner.getModel()).setMaximum(rows - 1);
  }

  public void setStartCoordinates(int x, int y) {
    startXSpinner.setValue(x);
    startYSpinner.setValue(y);
  }

  public void setFinishCoordinates(int x, int y) {
    finishXSpinner.setValue(x);
    finishYSpinner.setValue(y);
  }

  public void setStatus(String message) {
    statusLabel.setText("<html>" + message + "</html>");
  }

  public MazeCanvas getCanvas() {
    return canvas;
  }
}
