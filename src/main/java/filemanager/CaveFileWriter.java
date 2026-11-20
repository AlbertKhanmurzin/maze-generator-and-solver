package filemanager;

import domain.model.Cave;
import domain.model.Matrix;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CaveFileWriter {
  public static void saveToFile(File file, Cave cave) throws IOException {
    if (cave == null) {
      throw new IllegalArgumentException("Пещера не может быть null");
    }
    int rows = cave.getHeight();
    int cols = cave.getWidth();
    Matrix matrix = cave.getMatrix();

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      writer.write(rows + " " + cols);
      writer.newLine();

      for (int y = 0; y < rows; y++) {
        for (int x = 0; x < cols; x++) {
          writer.write((matrix.getValue(x, y) ? "1" : "0") + (x == cols - 1 ? "" : " "));
        }
        writer.newLine();
      }
    }
  }
}
