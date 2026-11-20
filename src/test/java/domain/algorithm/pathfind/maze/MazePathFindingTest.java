package domain.algorithm.pathfind.maze;

import static org.junit.jupiter.api.Assertions.*;

import domain.algorithm.generator.MazeGenerator;
import domain.model.Matrix;
import domain.model.Maze;
import domain.model.Position;
import java.util.LinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MazePathFindingTest {

  private MazePathFinding pathFinder;

  @BeforeEach
  void setUp() {
    pathFinder = new MazePathFinding();
  }

  @Test
  @DisplayName("Поиск пути в фиксированном лабиринте 4x4 из описания ТЗ")
  void testFindPathInSampleMaze() {
    // Лабиринт 4x4 из README:
    // vrt walls:
    // 0 0 0 1
    // 1 0 1 1
    // 0 1 0 1
    // 0 0 0 1
    // hrz walls:
    // 1 0 1 0
    // 0 0 1 0
    // 1 1 0 1
    // 1 1 1 1
    boolean[][] vrt = {
      {false, false, false, true},
      {true, false, true, true},
      {false, true, false, true},
      {false, false, false, true}
    };
    boolean[][] hrz = {
      {true, false, true, false},
      {false, false, true, false},
      {true, true, false, true},
      {true, true, true, true}
    };

    Maze maze = new Maze(new Matrix(vrt), new Matrix(hrz));
    Position start = new Position(0, 0);
    Position finish = new Position(3, 3);

    LinkedList<Position> path = pathFinder.findPath(maze, start, finish);

    assertNotNull(path, "Путь от старта к финишу должен существовать");
    assertFalse(path.isEmpty());
    assertEquals(start, path.getFirst(), "Первая точка пути должна быть стартовой");
    assertEquals(finish, path.getLast(), "Последняя точка пути должна быть конечной");

    // Проверяем, что каждый шаг пути корректен (соседняя ячейка и между ними
    // нет стены)
    for (int i = 0; i < path.size() - 1; i++) {
      Position current = path.get(i);
      Position next = path.get(i + 1);

      int dx = Math.abs(current.x() - next.x());
      int dy = Math.abs(current.y() - next.y());
      assertEquals(1, dx + dy, "Шаг пути должен вести в строго соседнюю клетку");

      assertTrue(
          maze.getPassableNeighbors(current).contains(next),
          "Между соседними шагами пути не должно быть стены");
    }
  }

  @Test
  @DisplayName("Стартовая точка совпадает с конечной")
  void testStartEqualsFinish() {
    MazeGenerator generator = new MazeGenerator();
    Maze maze = generator.generateMaze(5, 5);

    Position point = new Position(2, 2);
    LinkedList<Position> path = pathFinder.findPath(maze, point, point);

    assertNotNull(path);
    assertEquals(1, path.size(), "Путь из точки в саму себя должен состоять из 1 точки");
    assertEquals(point, path.getFirst());
  }

  @Test
  @DisplayName("Путь отсутствует (целевая клетка заблокирована со всех сторон)")
  void testNoPathWhenBlocked() {
    boolean[][] allWalls = {{true, true, true}, {true, true, true}, {true, true, true}};
    Maze maze = new Maze(new Matrix(allWalls), new Matrix(allWalls));

    Position start = new Position(0, 0);
    Position finish = new Position(2, 2);

    LinkedList<Position> path = pathFinder.findPath(maze, start, finish);
    assertNull(path, "Если пути нет, алгоритм должен возвращать null");
  }

  @Test
  @DisplayName("Поиск именно кратчайшего пути в незаблокированном поле 2x2")
  void testShortestPathFound() {
    // В пустом лабиринте 2x2 путь из (0,0) в (1,1) должен иметь минимальную
    // длину (3 клетки)
    boolean[][] vrt = {{false, true}, {false, true}};
    boolean[][] hrz = {{false, false}, {true, true}};
    Maze maze = new Maze(new Matrix(vrt), new Matrix(hrz));

    LinkedList<Position> path = pathFinder.findPath(maze, new Position(0, 0), new Position(1, 1));

    assertNotNull(path);
    assertEquals(3, path.size(), "Кратчайший путь в 2x2 должен содержать ровно 3 ячейки");
  }

  @Test
  @DisplayName("Поиск пути в случайно сгенерированном идеальном лабиринте")
  void testFindPathInGeneratedMaze() {
    MazeGenerator generator = new MazeGenerator();
    Maze maze = generator.generateMaze(15, 15);

    Position start = new Position(0, 0);
    Position finish = new Position(14, 14);

    LinkedList<Position> path = pathFinder.findPath(maze, start, finish);
    assertNotNull(path, "В идеальном лабиринте всегда есть путь между любыми двумя точками");
    assertEquals(start, path.getFirst());
    assertEquals(finish, path.getLast());
  }
}
