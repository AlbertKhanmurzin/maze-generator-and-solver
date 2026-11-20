package filemanager;

import static org.junit.jupiter.api.Assertions.*;

import domain.algorithm.generator.MazeGenerator;
import domain.model.Maze;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MazeFileManagerTest {

  @Test
  @DisplayName("Сохранение и последующая загрузка лабиринта")
  void testSaveAndLoadMaze() throws IOException {
    MazeGenerator generator = new MazeGenerator();
    Maze originalMaze = generator.generateMaze(6, 6);

    File tempFile = File.createTempFile("test_maze", ".txt");
    tempFile.deleteOnExit();

    try {
      MazeFileWriter.saveToFile(tempFile, originalMaze);
      assertTrue(tempFile.exists(), "Файл должен быть успешно создан");

      Maze loadedMaze = MazeFileParser.loadFromFile(tempFile);

      assertEquals(originalMaze.getWidth(), loadedMaze.getWidth(), "Ширина должна совпадать");
      assertEquals(originalMaze.getHeight(), loadedMaze.getHeight(), "Высота должна совпадать");
      assertEquals(
          originalMaze.getVrtMatrix(),
          loadedMaze.getVrtMatrix(),
          "Матрица вертикальных стен должна совпадать");
      assertEquals(
          originalMaze.getHrzMatrix(),
          loadedMaze.getHrzMatrix(),
          "Матрица горизонтальных стен должна совпадать");
    } finally {
      Files.deleteIfExists(tempFile.toPath());
    }
  }

  @Test
  @DisplayName("Попытка загрузки несуществующего файла")
  void testLoadNonExistentFileThrows() {
    File nonExistent = new File("non_existent_maze_file_12345.txt");
    assertThrows(RuntimeException.class, () -> MazeFileParser.loadFromFile(nonExistent));
  }

  @Test
  @DisplayName("Попытка сохранения null-лабиринта")
  void testSaveNullMazeThrows() throws IOException {
    File tempFile = File.createTempFile("null_test", ".txt");
    tempFile.deleteOnExit();

    try {
      assertThrows(IllegalArgumentException.class, () -> MazeFileWriter.saveToFile(tempFile, null));
    } finally {
      Files.deleteIfExists(tempFile.toPath());
    }
  }

  @Test
  @DisplayName("Загрузка некорректного или пустого файла")
  void testLoadCorruptedFileThrows() throws IOException {
    File tempFile = File.createTempFile("corrupt_maze", ".txt");
    tempFile.deleteOnExit();

    try {
      try (FileWriter writer = new FileWriter(tempFile)) {
        writer.write("not a valid maze");
      }
      assertThrows(RuntimeException.class, () -> MazeFileParser.loadFromFile(tempFile));
    } finally {
      Files.deleteIfExists(tempFile.toPath());
    }
  }
}
