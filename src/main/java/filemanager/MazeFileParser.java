package filemanager;

import domain.model.Matrix;
import domain.model.Maze;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MazeFileParser {
  public static Maze Parser(String filePath) {
    return loadFromFile(new File(filePath));
  }

  public static Maze loadFromFile(File file) {
    try (Scanner scanner = new Scanner(file)) {
      if (!scanner.hasNextInt()) {
        throw new IllegalArgumentException("Файл пуст или некорректен!");
      }
      int rows = scanner.nextInt();
      int cols = scanner.nextInt();
      boolean[][] vrtWalls = new boolean[rows][cols];
      boolean[][] hrzWalls = new boolean[rows][cols];

      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          vrtWalls[i][j] = (scanner.nextInt() == 1);
        }
      }

      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          hrzWalls[i][j] = (scanner.nextInt() == 1);
        }
      }
      return new Maze(new Matrix(vrtWalls), new Matrix(hrzWalls));
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Указанный файл не существует!", e);
    } catch (Exception e) {
      throw new RuntimeException("Ошибка чтения лабиринта: " + e.getMessage(), e);
    }
  }
}
