package domain.algorithm.generator;

import static org.junit.jupiter.api.Assertions.*;

import domain.model.Maze;
import domain.model.Position;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MazeGeneratorTest {

  private MazeGenerator generator;

  @BeforeEach
  void setUp() {
    generator = new MazeGenerator();
  }

  @Test
  @DisplayName("Генерация лабиринта заданных размеров и граничные размеры")
  void testGenerateMazeValidDimensions() {
    Maze maze10 = generator.generateMaze(10, 15);
    assertEquals(10, maze10.getWidth());
    assertEquals(15, maze10.getHeight());

    // Минимальный допустимый размер 1x1
    Maze mazeMin = generator.generateMaze(1, 1);
    assertEquals(1, mazeMin.getWidth());
    assertEquals(1, mazeMin.getHeight());

    // Максимальный допустимый размер 50x50
    Maze mazeMax = generator.generateMaze(50, 50);
    assertEquals(50, mazeMax.getWidth());
    assertEquals(50, mazeMax.getHeight());
  }

  @Test
  @DisplayName("Валидация размеров: выброс исключения при выходе за пределы 1..50")
  void testGenerateMazeInvalidDimensions() {
    assertThrows(IllegalArgumentException.class, () -> generator.generateMaze(0, 10));
    assertThrows(IllegalArgumentException.class, () -> generator.generateMaze(10, 0));
    assertThrows(IllegalArgumentException.class, () -> generator.generateMaze(-5, 10));
    assertThrows(IllegalArgumentException.class, () -> generator.generateMaze(51, 10));
    assertThrows(IllegalArgumentException.class, () -> generator.generateMaze(10, 51));
  }

  @Test
  @DisplayName("Внешние границы лабиринта всегда закрыты")
  void testOuterWallsClosed() {
    int w = 8;
    int h = 8;
    Maze maze = generator.generateMaze(w, h);

    for (int y = 0; y < h; y++) {
      assertTrue(maze.hasLeftWall(0, y), "Левая внешняя граница должна быть закрыта");
      assertTrue(maze.hasRightWall(w - 1, y), "Правая внешняя граница должна быть закрыта");
    }

    for (int x = 0; x < w; x++) {
      assertTrue(maze.hasTopWall(x, 0), "Верхняя внешняя граница должна быть закрыта");
      assertTrue(maze.hasBottomWall(x, h - 1), "Нижняя внешняя граница должна быть закрыта");
    }
  }

  @Test
  @DisplayName("Связность идеального лабиринта: отсутствие изолированных областей")
  void testMazeReachabilityNoIsolatedAreas() {
    int w = 10;
    int h = 10;
    Maze maze = generator.generateMaze(w, h);

    // Обход в ширину (BFS) из стартовой ячейки (0, 0)
    Set<Position> visited = new HashSet<>();
    Queue<Position> queue = new ArrayDeque<>();

    Position start = new Position(0, 0);
    visited.add(start);
    queue.add(start);

    while (!queue.isEmpty()) {
      Position current = queue.poll();
      for (Position neighbor : maze.getPassableNeighbors(current)) {
        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          queue.add(neighbor);
        }
      }
    }

    // Все клетки лабиринта должны быть достижимы из (0,0)
    assertEquals(
        w * h,
        visited.size(),
        "Все клетки лабиринта должны быть связны (нет изолированных областей)");
  }

  @Test
  @DisplayName("Свойство дерева: ровно V - 1 проходов (отсутствие петель и циклов)")
  void testPerfectMazeTreeStructure() {
    int w = 12;
    int h = 12;
    Maze maze = generator.generateMaze(w, h);

    int totalPassages = 0;

    // Считаем внутренние горизонтальные проходы (между (x,y) и (x+1,y))
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w - 1; x++) {
        if (!maze.hasRightWall(x, y)) {
          totalPassages++;
        }
      }
    }

    // Считаем внутренние вертикальные проходы (между (x,y) и (x,y+1))
    for (int y = 0; y < h - 1; y++) {
      for (int x = 0; x < w; x++) {
        if (!maze.hasBottomWall(x, y)) {
          totalPassages++;
        }
      }
    }

    // В дереве с V вершинами число рёбер (проходов) в точности равно V - 1
    int verticesCount = w * h;
    assertEquals(
        verticesCount - 1,
        totalPassages,
        "В идеальном лабиринте количество проходов должно быть "
            + "ровно (V - 1), гарантируя отсутствие петель");
  }
}
