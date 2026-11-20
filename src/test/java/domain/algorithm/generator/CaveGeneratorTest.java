package domain.algorithm.generator;

import static org.junit.jupiter.api.Assertions.*;

import domain.model.Cave;
import domain.model.Matrix;
import filemanager.CaveFileParser;
import filemanager.CaveFileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CaveGeneratorTest {
  private CaveGenerator generator;

  @BeforeEach
  void setUp() {
    generator = new CaveGenerator();
  }

  @Test
  @DisplayName("Инициализация пещеры с шансом 1.0 (все клетки живые)")
  void testInitCaveAllAlive() {
    Cave cave = generator.initCave(10, 10, 3, 4, 1.0);
    Matrix matrix = cave.getMatrix();
    for (int y = 0; y < 10; y++) {
      for (int x = 0; x < 10; x++) {
        assertTrue(matrix.getValue(x, y), "Клетка (" + x + ", " + y + ") должна быть живой");
      }
    }
  }

  @Test
  @DisplayName("Инициализация пещеры с шансом 0.0 (все клетки мертвые)")
  void testInitCaveAllDead() {
    Cave cave = generator.initCave(10, 10, 3, 4, 0.0);
    Matrix matrix = cave.getMatrix();
    for (int y = 0; y < 10; y++) {
      for (int x = 0; x < 10; x++) {
        assertFalse(matrix.getValue(x, y), "Клетка (" + x + ", " + y + ") должна быть мертвой");
      }
    }
  }

  @Test
  @DisplayName("Проверка одного шага симуляции автомата")
  void testOneGeneration() {
    // 3x3 матрица, где только центр живой
    boolean[][] initial = {{false, false, false}, {false, true, false}, {false, false, false}};
    // Границы считаются живыми!
    // Для центра (1,1): соседи все мертвые (0 живых соседей).
    // Если deathLimit = 3, живая клетка с 0 соседями должна умереть (0 < 3).
    // Для угла (0,0): 5 соседей за границей (все живые) + 3 соседа внутри
    // (только центр (1,1) живой) -> 6 живых соседей. Если lifeLimit = 4, угол
    // был мертв, но соседей 6 > 4 -> угол должен стать живым!
    Matrix m = new Matrix(initial);
    Cave cave = new Cave(m, 3, 4, 0.0);

    Cave nextCave = generator.oneGeneration(cave);
    assertFalse(nextCave.getMatrix().getValue(1, 1), "Центральная клетка должна погибнуть");
    assertTrue(
        nextCave.getMatrix().getValue(0, 0), "Угловая клетка должна ожить из-за внешних границ");
  }

  @Test
  @DisplayName("Проверка валидации параметров пещеры")
  void testCaveValidation() {
    Matrix m = new Matrix(10, 10);

    // Неверный deathLimit
    assertThrows(IllegalArgumentException.class, () -> new Cave(m, -1, 4, 0.5));
    assertThrows(IllegalArgumentException.class, () -> new Cave(m, 8, 4, 0.5));

    // Неверный lifeLimit
    assertThrows(IllegalArgumentException.class, () -> new Cave(m, 3, -1, 0.5));
    assertThrows(IllegalArgumentException.class, () -> new Cave(m, 3, 8, 0.5));

    // Неверная вероятность
    assertThrows(IllegalArgumentException.class, () -> new Cave(m, 3, 4, -0.1));
    assertThrows(IllegalArgumentException.class, () -> new Cave(m, 3, 4, 1.1));

    // Null матрица
    assertThrows(IllegalArgumentException.class, () -> new Cave(null, 3, 4, 0.5));
  }

  @Test
  @DisplayName("Тест чтения и записи файла пещеры")
  void testCaveFileSaveAndLoad() throws IOException {
    Cave cave = generator.initCave(5, 5, 3, 4, 0.5);
    File tempFile = File.createTempFile("test_cave", ".txt");
    tempFile.deleteOnExit();

    CaveFileWriter.saveToFile(tempFile, cave);
    assertTrue(tempFile.exists());

    Cave loadedCave = CaveFileParser.loadFromFile(tempFile, 3, 4);
    assertEquals(cave.getWidth(), loadedCave.getWidth());
    assertEquals(cave.getHeight(), loadedCave.getHeight());
    assertEquals(cave.getMatrix(), loadedCave.getMatrix());

    Files.deleteIfExists(tempFile.toPath());
  }

  @Test
  @DisplayName("Сеттеры лимитов работают с валидацией")
  void testSetLimits() {
    Matrix m = new Matrix(5, 5);
    Cave cave = new Cave(m, 3, 4);

    cave.setDeathLimit(5);
    assertEquals(5, cave.getDeathLimit());

    cave.setLifeLimit(6);
    assertEquals(6, cave.getLifeLimit());

    assertThrows(IllegalArgumentException.class, () -> cave.setDeathLimit(-1));
    assertThrows(IllegalArgumentException.class, () -> cave.setDeathLimit(8));
    assertThrows(IllegalArgumentException.class, () -> cave.setLifeLimit(-1));
    assertThrows(IllegalArgumentException.class, () -> cave.setLifeLimit(8));
  }

  @Test
  @DisplayName("Граничные допустимые и недопустимые размеры пещеры")
  void testCaveBoundarySizes() {
    // 1x1 и 50x50 допустимы
    Cave minCave = generator.initCave(1, 1, 3, 4, 0.5);
    assertEquals(1, minCave.getWidth());
    assertEquals(1, minCave.getHeight());

    Cave maxCave = generator.initCave(50, 50, 3, 4, 0.5);
    assertEquals(50, maxCave.getWidth());
    assertEquals(50, maxCave.getHeight());

    // Недопустимые размеры
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Cave(new Matrix(1, 1), 3, 4) {
              {
                // тест выхода за границы 51
                new Cave(new Matrix(51, 10), 3, 4);
              }
            });
    assertThrows(IllegalArgumentException.class, () -> new Cave(new Matrix(10, 51), 3, 4));
  }

  @Test
  @DisplayName("Правила клеточного автомата: рождение и выживание клеток")
  void testCellularAutomataExactRules() {
    // Создаем матрицу 3x3, где все внутренние клетки мертвы
    boolean[][] grid = {{false, false, false}, {false, false, false}, {false, false, false}};
    Matrix m = new Matrix(grid);

    // Центр (1,1) окружен только границами (0 живых внутри + 0 за границей
    // касаются центра). У центра (1,1) соседи - это только ячейки сетки (все
    // мертвые) -> 0 живых соседей. Если deathLimit = 1, lifeLimit = 2: Центр
    // мертв, count=0 <= 2 -> остается мертвым.
    Cave cave1 = new Cave(m, 1, 2, 0.0);
    Cave next1 = generator.oneGeneration(cave1);
    assertFalse(
        next1.getMatrix().getValue(1, 1), "Мертвая клетка с 0 соседями должна остаться мертвой");

    // Для угловой клетки (0,0): 5 внешних соседей за границей (все живые) + 3
    // внутренних (все мертвые) = 5. При lifeLimit = 4: 5 > 4 -> угловая клетка
    // должна ожить!
    assertTrue(next1.getMatrix().getValue(0, 0), "Мертвая клетка с 5 соседями (> 4) должна ожить");

    // При lifeLimit = 6: 5 <= 6 -> угловая клетка должна остаться мертвой!
    Cave cave2 = new Cave(m, 1, 6, 0.0);
    Cave next2 = generator.oneGeneration(cave2);
    assertFalse(
        next2.getMatrix().getValue(0, 0),
        "Мертвая клетка с 5 соседями (<= 6) должна остаться мертвой");
  }
}
