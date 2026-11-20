package gui.maze;

import domain.algorithm.generator.MazeGenerator;
import domain.algorithm.pathfind.maze.MazePathFinding;
import domain.model.Maze;
import domain.model.Position;
import filemanager.MazeFileParser;
import filemanager.MazeFileWriter;
import java.io.File;
import java.util.LinkedList;
import javax.swing.*;

public class MazeController {
  private final MazeGenerator generator = new MazeGenerator();
  private final MazePathFinding pathFinder = new MazePathFinding();

  private MazePanel view;
  private Maze currentMaze;
  private Position startPos;
  private Position finishPos;

  public void setView(MazePanel view) {
    this.view = view;
  }

  public void loadFromFile(File file) {
    try {
      this.currentMaze = MazeFileParser.loadFromFile(file);
      this.startPos = null;
      this.finishPos = null;

      view.getCanvas().setMaze(currentMaze);
      view.updateDimensions(currentMaze.getHeight(), currentMaze.getWidth());
      view.setStatus(
          String.format(
              "Лабиринт загружен (%dx%d)", currentMaze.getHeight(), currentMaze.getWidth()));
    } catch (Exception e) {
      JOptionPane.showMessageDialog(
          view, "Ошибка загрузки: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      view.setStatus("Ошибка загрузки файла");
    }
  }

  public void saveToFile(File file) {
    if (currentMaze == null) {
      JOptionPane.showMessageDialog(
          view,
          "Сначала загрузите или сгенерируйте лабиринт!",
          "Предупреждение",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    try {
      MazeFileWriter.saveToFile(file, currentMaze);
      JOptionPane.showMessageDialog(
          view,
          "Лабиринт успешно сохранен в: " + file.getName(),
          "Успех",
          JOptionPane.INFORMATION_MESSAGE);
      view.setStatus("Лабиринт сохранен: " + file.getName());
    } catch (Exception e) {
      JOptionPane.showMessageDialog(
          view, "Ошибка сохранения: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      view.setStatus("Ошибка сохранения");
    }
  }

  public void generate(int rows, int cols) {
    try {
      // В MazeGenerator x = cols, y = rows
      this.currentMaze = generator.generateMaze(cols, rows);
      this.startPos = null;
      this.finishPos = null;

      view.getCanvas().setMaze(currentMaze);
      view.updateDimensions(rows, cols);
      view.setStatus(String.format("Сгенерирован идеальный лабиринт (%dx%d)", rows, cols));
    } catch (Exception e) {
      JOptionPane.showMessageDialog(
          view, "Ошибка генерации: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      view.setStatus("Ошибка генерации");
    }
  }

  public void solve(int startX, int startY, int finishX, int finishY) {
    if (currentMaze == null) {
      JOptionPane.showMessageDialog(
          view, "Лабиринт не инициализирован!", "Предупреждение", JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (!currentMaze.isInside(startX, startY) || !currentMaze.isInside(finishX, finishY)) {
      JOptionPane.showMessageDialog(
          view, "Координаты выходят за границы лабиринта!", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }

    this.startPos = new Position(startX, startY);
    this.finishPos = new Position(finishX, finishY);

    LinkedList<Position> path = pathFinder.findPath(currentMaze, startPos, finishPos);
    if (path != null) {
      view.getCanvas().setSolution(path, startPos, finishPos);
      view.setStatus(String.format("Путь найден! Длина: %d ячеек", path.size()));
    } else {
      view.getCanvas().setSolution(null, startPos, finishPos);
      view.setStatus("Путь между заданными точками не найден");
    }
  }

  public void handleCanvasClick(int cellX, int cellY) {
    if (currentMaze == null) return;

    if (startPos == null || (startPos != null && finishPos != null)) {
      // Устанавливаем новую точку старта
      startPos = new Position(cellX, cellY);
      finishPos = null;
      view.setStartCoordinates(cellX, cellY);
      view.getCanvas().setSolution(null, startPos, null);
      view.setStatus(
          String.format(
              "Выбран старт: (%d, %d). Теперь кликните для выбора финиша.", cellX, cellY));
    } else {
      // Устанавливаем точку финиша и ищем путь
      finishPos = new Position(cellX, cellY);
      view.setFinishCoordinates(cellX, cellY);
      solve(startPos.x(), startPos.y(), finishPos.x(), finishPos.y());
    }
  }

  public void clearSolution() {
    startPos = null;
    finishPos = null;
    if (view != null) {
      view.getCanvas().clearSolution();
      view.setStatus("Путь очищен");
    }
  }

  public Maze getCurrentMaze() {
    return currentMaze;
  }
}
