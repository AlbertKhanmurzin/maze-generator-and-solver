package gui.cave;

import domain.algorithm.generator.CaveGenerator;
import domain.model.Cave;
import filemanager.CaveFileParser;
import filemanager.CaveFileWriter;
import java.io.File;
import javax.swing.*;

public class CaveController {
  private final CaveGenerator generator = new CaveGenerator();

  private CavePanel view;
  private Cave currentCave;
  private Timer autoTimer;
  private int stepCount = 0;

  public void setView(CavePanel view) {
    this.view = view;
  }

  public void loadFromFile(File file) {
    stopAuto();
    try {
      int deathLimit = view != null ? view.getDeathLimit() : 3;
      int lifeLimit = view != null ? view.getLifeLimit() : 4;
      this.currentCave = CaveFileParser.loadFromFile(file, deathLimit, lifeLimit);
      this.stepCount = 0;

      view.getCanvas().setCave(currentCave);
      view.updateDimensions(currentCave.getHeight(), currentCave.getWidth());
      view.setStatus(
          String.format(
              "Пещера загружена (%dx%d)", currentCave.getHeight(), currentCave.getWidth()));
    } catch (Exception e) {
      JOptionPane.showMessageDialog(
          view, "Ошибка загрузки: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      if (view != null) {
        view.setStatus("Ошибка загрузки файла");
      }
    }
  }

  public void saveToFile(File file) {
    if (currentCave == null) {
      JOptionPane.showMessageDialog(
          view,
          "Сначала загрузите или инициализируйте пещеру!",
          "Предупреждение",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    try {
      CaveFileWriter.saveToFile(file, currentCave);
      JOptionPane.showMessageDialog(
          view,
          "Пещера успешно сохранена в: " + file.getName(),
          "Успех",
          JOptionPane.INFORMATION_MESSAGE);
      view.setStatus("Пещера сохранена: " + file.getName());
    } catch (Exception e) {
      JOptionPane.showMessageDialog(
          view, "Ошибка сохранения: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      view.setStatus("Ошибка сохранения");
    }
  }

  public void initRandom(int rows, int cols, int deathLimit, int lifeLimit, double probability) {
    stopAuto();
    try {
      // cols = x, rows = y
      this.currentCave = generator.initCave(cols, rows, deathLimit, lifeLimit, probability);
      this.stepCount = 0;

      view.getCanvas().setCave(currentCave);
      view.updateDimensions(rows, cols);
      view.setStatus(
          String.format(
              "Инициализирована случайная пещера (%dx%d), шанс: %d%%",
              rows, cols, (int) Math.round(probability * 100)));
    } catch (Exception e) {
      JOptionPane.showMessageDialog(
          view, "Ошибка инициализации: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      view.setStatus("Ошибка инициализации");
    }
  }

  public boolean step() {
    if (currentCave == null) {
      JOptionPane.showMessageDialog(
          view, "Пещера не инициализирована!", "Предупреждение", JOptionPane.WARNING_MESSAGE);
      return false;
    }

    try {
      currentCave.setDeathLimit(view.getDeathLimit());
      currentCave.setLifeLimit(view.getLifeLimit());

      Cave nextCave = generator.oneGeneration(currentCave);
      boolean stabilized = nextCave.getMatrix().equals(currentCave.getMatrix());

      currentCave = nextCave;
      stepCount++;
      view.getCanvas().setCave(currentCave);

      if (stabilized) {
        if (isAutoRunning()) {
          stopAuto();
        }
        view.setStatus(
            String.format("Пещера стабилизировалась на шаге %d (нет изменений)", stepCount));
        return false;
      } else {
        view.setStatus(String.format("Шаг симуляции: %d", stepCount));
        return true;
      }
    } catch (Exception e) {
      stopAuto();
      JOptionPane.showMessageDialog(
          view,
          "Ошибка при выполнении шага: " + e.getMessage(),
          "Ошибка",
          JOptionPane.ERROR_MESSAGE);
      view.setStatus("Ошибка шага симуляции");
      return false;
    }
  }

  public void toggleAuto(int delayMs) {
    if (isAutoRunning()) {
      stopAuto();
    } else {
      startAuto(delayMs);
    }
  }

  public void startAuto(int delayMs) {
    if (currentCave == null) {
      JOptionPane.showMessageDialog(
          view,
          "Сначала загрузите или инициализируйте пещеру!",
          "Предупреждение",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    if (isAutoRunning()) {
      return;
    }

    autoTimer =
        new Timer(
            delayMs,
            e -> {
              boolean changed = step();
              if (!changed) {
                stopAuto();
              }
            });
    autoTimer.start();
    view.onAutoStarted();
    view.setStatus(
        String.format("Автосимуляция запущена (каждые %d мс, шаг: %d)...", delayMs, stepCount));
  }

  public void stopAuto() {
    if (autoTimer != null && autoTimer.isRunning()) {
      autoTimer.stop();
    }
    if (view != null) {
      view.onAutoStopped();
      view.setStatus(String.format("Автосимуляция остановлена. Текущий шаг: %d", stepCount));
    }
  }

  public boolean isAutoRunning() {
    return autoTimer != null && autoTimer.isRunning();
  }

  public Cave getCurrentCave() {
    return currentCave;
  }

  public int getStepCount() {
    return stepCount;
  }
}
