package filemanager;

import domain.model.Matrix;
import domain.model.Maze;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MazeFileWriter {

  public static void saveToFile(File file, Maze maze) throws IOException {
    if (maze == null) {
      throw new IllegalArgumentException("Лабиринт не может быть null");
    }
    int rows = maze.getHeight();
    int cols = maze.getWidth();
    Matrix vrt = maze.getVrtMatrix();
    Matrix hrz = maze.getHrzMatrix();

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      writer.write(rows + " " + cols);
      writer.newLine();

      // Вертикальные стены
      for (int y = 0; y < rows; y++) {
        for (int x = 0; x < cols; x++) {
          writer.write((vrt.getValue(x, y) ? "1" : "0") + (x == cols - 1 ? "" : " "));
        }
        writer.newLine();
      }

      writer.newLine();

      // Горизонтальные стены
      for (int y = 0; y < rows; y++) {
        for (int x = 0; x < cols; x++) {
          writer.write((hrz.getValue(x, y) ? "1" : "0") + (x == cols - 1 ? "" : " "));
        }
        writer.newLine();
      }
    }
  }
}
