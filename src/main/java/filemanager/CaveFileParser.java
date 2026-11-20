package filemanager;

import domain.model.Cave;
import domain.model.Matrix;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CaveFileParser {
  public static Cave parser(String filePath, int deathLimit, int lifeLimit) {
    return loadFromFile(new File(filePath), deathLimit, lifeLimit);
  }

  public static Cave loadFromFile(File file, int deathLimit, int lifeLimit) {
    try (Scanner scanner = new Scanner(file)) {
      if (!scanner.hasNextInt()) {
        throw new IllegalArgumentException("Файл пуст или некорректен!");
      }
      int rows = scanner.nextInt();
      int cols = scanner.nextInt();

      if (rows < Cave.MIN_SIZE
          || rows > Cave.MAX_SIZE
          || cols < Cave.MIN_SIZE
          || cols > Cave.MAX_SIZE) {
        throw new IllegalArgumentException(
            String.format(
                "Размер пещеры (%dx%d) выходит за допустимые пределы [%d, %d]",
                cols, rows, Cave.MIN_SIZE, Cave.MAX_SIZE));
      }

      boolean[][] grid = new boolean[rows][cols];

      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          if (!scanner.hasNextInt()) {
            throw new IllegalArgumentException("Недостаточно данных в файле пещеры");
          }
          grid[i][j] = (scanner.nextInt() == 1);
        }
      }

      Matrix matrix = new Matrix(grid);
      return new Cave(matrix, deathLimit, lifeLimit);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Указанный файл не существует!", e);
    } catch (Exception e) {
      throw new RuntimeException("Ошибка чтения пещеры: " + e.getMessage(), e);
    }
  }
}
