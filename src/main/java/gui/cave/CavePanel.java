package gui.cave;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Вкладка для работы с пещерами: объединяет холст и панель управления. */
public class CavePanel extends JPanel {
  private final CaveCanvas canvas;
  private final CaveController controller;

  private JSpinner rowsSpinner;
  private JSpinner colsSpinner;
  private JSpinner probSpinner;

  private JSpinner deathLimitSpinner;
  private JSpinner lifeLimitSpinner;

  private JSpinner delaySpinner;
  private JButton stepBtn;
  private JButton autoBtn;
  private JButton initBtn;
  private JButton loadBtn;
  private JButton saveBtn;

  private JLabel statusLabel;

  public CavePanel(CaveController controller) {
    this.controller = controller;
    this.canvas = new CaveCanvas();
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
  }

  private JPanel createControlPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setPreferredSize(new Dimension(320, 500));

    // 1. Секция файлов
    JPanel fileSection = new JPanel(new GridLayout(1, 2, 8, 8));
    fileSection.setBorder(new TitledBorder("Файл пещеры"));

    loadBtn = new JButton("Загрузить...");
    saveBtn = new JButton("Сохранить...");

    loadBtn.addActionListener(e -> onOpenFile());
    saveBtn.addActionListener(e -> onSaveFile());

    fileSection.add(loadBtn);
    fileSection.add(saveBtn);
    panel.add(fileSection);
    panel.add(Box.createVerticalStrut(8));

    // 2. Секция инициализации
    JPanel initSection = new JPanel(new GridBagLayout());
    initSection.setBorder(new TitledBorder("Инициализация"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(3, 4, 3, 4);

    rowsSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 50, 1));
    colsSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 50, 1));
    probSpinner = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0.5;
    initSection.add(new JLabel("Строк (Y):"), gbc);
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 0.5;
    initSection.add(rowsSpinner, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0.5;
    initSection.add(new JLabel("Столбцов (X):"), gbc);
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 0.5;
    initSection.add(colsSpinner, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 0.5;
    initSection.add(new JLabel("Шанс жизни (%):"), gbc);
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.weightx = 0.5;
    initSection.add(probSpinner, gbc);

    initBtn = new JButton("Инициализировать");
    initBtn.addActionListener(e -> {
      int rows = (int) rowsSpinner.getValue();
      int cols = (int) colsSpinner.getValue();
      int death = (int) deathLimitSpinner.getValue();
      int life = (int) lifeLimitSpinner.getValue();
      double prob = ((int) probSpinner.getValue()) / 100.0;
      controller.initRandom(rows, cols, death, life, prob);
    });

    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    initSection.add(initBtn, gbc);

    panel.add(initSection);
    panel.add(Box.createVerticalStrut(8));

    // 3. Секция пределов клеточного автомата
    JPanel limitSection = new JPanel(new GridBagLayout());
    limitSection.setBorder(new TitledBorder("Пределы автомата"));
    GridBagConstraints lgbc = new GridBagConstraints();
    lgbc.fill = GridBagConstraints.HORIZONTAL;
    lgbc.insets = new Insets(3, 4, 3, 4);

    deathLimitSpinner = new JSpinner(new SpinnerNumberModel(3, 0, 7, 1));
    lifeLimitSpinner = new JSpinner(new SpinnerNumberModel(4, 0, 7, 1));

    lgbc.gridx = 0;
    lgbc.gridy = 0;
    lgbc.weightx = 0.5;
    limitSection.add(new JLabel("Предел «смерти»:"), lgbc);
    lgbc.gridx = 1;
    lgbc.gridy = 0;
    lgbc.weightx = 0.5;
    limitSection.add(deathLimitSpinner, lgbc);

    lgbc.gridx = 0;
    lgbc.gridy = 1;
    lgbc.weightx = 0.5;
    limitSection.add(new JLabel("Предел «рождения»:"), lgbc);
    lgbc.gridx = 1;
    lgbc.gridy = 1;
    lgbc.weightx = 0.5;
    limitSection.add(lifeLimitSpinner, lgbc);

    panel.add(limitSection);
    panel.add(Box.createVerticalStrut(8));

    // 4. Секция симуляции
    JPanel simSection = new JPanel(new GridBagLayout());
    simSection.setBorder(new TitledBorder("Симуляция"));
    GridBagConstraints sgbc = new GridBagConstraints();
    sgbc.fill = GridBagConstraints.HORIZONTAL;
    sgbc.insets = new Insets(3, 4, 3, 4);

    stepBtn = new JButton("Следующий шаг");
    stepBtn.addActionListener(e -> controller.step());

    sgbc.gridx = 0;
    sgbc.gridy = 0;
    sgbc.gridwidth = 2;
    simSection.add(stepBtn, sgbc);

    delaySpinner = new JSpinner(new SpinnerNumberModel(200, 10, 5000, 50));
    sgbc.gridwidth = 1;
    sgbc.gridx = 0;
    sgbc.gridy = 1;
    sgbc.weightx = 0.5;
    simSection.add(new JLabel("Задержка (мс):"), sgbc);
    sgbc.gridx = 1;
    sgbc.gridy = 1;
    sgbc.weightx = 0.5;
    simSection.add(delaySpinner, sgbc);

    autoBtn = new JButton("Автоматически");
    autoBtn.addActionListener(e -> {
      int delay = (int) delaySpinner.getValue();
      controller.toggleAuto(delay);
    });

    sgbc.gridx = 0;
    sgbc.gridy = 2;
    sgbc.gridwidth = 2;
    simSection.add(autoBtn, sgbc);

    panel.add(simSection);
    panel.add(Box.createVerticalGlue());

    // 5. Секция статуса
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
    fileChooser.setDialogTitle("Выберите файл пещеры");
    fileChooser.setFileFilter(new FileNameExtensionFilter("Текстовые файлы (*.txt)", "txt"));
    int res = fileChooser.showOpenDialog(this);
    if (res == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      controller.loadFromFile(selectedFile);
    }
  }

  private void onSaveFile() {
    JFileChooser fileChooser = new JFileChooser(".");
    fileChooser.setDialogTitle("Сохранить пещеру в файл");
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
  }

  public int getDeathLimit() {
    return (int) deathLimitSpinner.getValue();
  }

  public int getLifeLimit() {
    return (int) lifeLimitSpinner.getValue();
  }

  public void onAutoStarted() {
    autoBtn.setText("Остановить");
    stepBtn.setEnabled(false);
    initBtn.setEnabled(false);
    loadBtn.setEnabled(false);
    delaySpinner.setEnabled(false);
  }

  public void onAutoStopped() {
    autoBtn.setText("Автоматически");
    stepBtn.setEnabled(true);
    initBtn.setEnabled(true);
    loadBtn.setEnabled(true);
    delaySpinner.setEnabled(true);
  }

  public void setStatus(String message) {
    statusLabel.setText("<html>" + message + "</html>");
  }

  public CaveCanvas getCanvas() {
    return canvas;
  }
}
