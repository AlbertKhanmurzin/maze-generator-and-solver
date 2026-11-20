package domain.algorithm.generator;

import domain.model.Matrix;
import domain.model.Maze;
import java.util.*;

public class MazeGenerator {

  private final Random random = new Random();

  public Maze generateMaze(int x, int y) {

    validSize(x, y);

    int setCount = 1;
    boolean[][] vrtMatrix = new boolean[y][x];
    boolean[][] hrzMatrix = new boolean[y][x];
    int[][] setMatrix = new int[y][x];

    for (int i = 0; i < y; i++) {
      if (i < y - 1) {
        setCount = fillSet(setMatrix[i], setCount);
        buildVerticalWalls(vrtMatrix[i], setMatrix[i]);
        randomPlaceBottomWall(hrzMatrix[i]);
        guaranteeBottomExit(hrzMatrix[i], setMatrix[i]);
        transferSetToBottom(hrzMatrix[i], setMatrix[i], setMatrix[i + 1]);
        buildRightEdge(vrtMatrix[i]);
      } else {
        setCount = fillSet(setMatrix[i], setCount);
        finalizeLastRow(vrtMatrix[i], setMatrix[i]);
        buildRightEdge(vrtMatrix[i]);
        buildBottomEdge(hrzMatrix[i]);
      }
    }

    return new Maze(new Matrix(vrtMatrix), new Matrix(hrzMatrix));
  }

  private void validSize(int x, int y) {
    if (x < Maze.MIN_SIZE || x > Maze.MAX_SIZE || y < Maze.MIN_SIZE || y > Maze.MAX_SIZE) {
      throw new IllegalArgumentException(
          String.format("Лабиринт не может быть размера x=%d, y=%d", x, y));
    }
  }

  // переносит множества без нижней стены на новую строку
  private void transferSetToBottom(boolean[] hrz, int[] top, int[] bot) {
    for (int i = 0; i < bot.length; i++) {
      if (!hrz[i]) bot[i] = top[i];
    }
  }

  // объеденяет два множества на всей строке
  private void mergeSet(int[] str, int consumer, int payload) {
    for (int i = 0; i < str.length; i++) {
      if (str[i] == payload) str[i] = consumer;
    }
  }

  // заполняет пустыкю ячейки строки если она == 0 множествами,
  // возвращает последний номер созданных множества
  private int fillSet(int[] str, int count) {
    for (int i = 0; i < str.length; i++) {
      if (str[i] == 0) str[i] = count++;
    }
    return count;
  }

  // принимает соседей и рандоино выбирает, объеденить или оставить стену,
  // возвращает boolean
  private boolean shouldPlaceRightWall(int leftSet, int rightSet) {
    if (leftSet == rightSet) return false;

    return random.nextBoolean();
  }

  private void randomPlaceBottomWall(boolean[] hrz) {
    for (int i = 0; i < hrz.length; i++) {
      hrz[i] = random.nextBoolean();
    }
  }

  // проверяет чтобы у каждого множества было минимум по одному проходу снизу
  private void guaranteeBottomExit(boolean[] hrz, int[] setRow) {
    Set<Integer> checkedSets = new HashSet<>();
    for (int i = 0; i < setRow.length; i++) {
      int currentSet = setRow[i];
      // Если это множество уже проверено — пропускаем
      if (checkedSets.contains(currentSet)) {
        continue;
      }
      checkedSets.add(currentSet);
      // Считаем количество проходов вниз у этого множества
      // и собираем индексы всех ячеек, принадлежащих этому множеству
      int exitsCount = 0;
      List<Integer> setCells = new ArrayList<>();
      for (int j = 0; j < setRow.length; j++) {
        if (setRow[j] == currentSet) {
          setCells.add(j);
          if (!hrz[j]) { // false означает отсутствие стены (есть проход)
            exitsCount++;
          }
        }
      }
      // Если у множества нет ни одного выхода вниз:
      if (exitsCount == 0) {
        // Случайно выбираем одну из ячеек этого множества и открываем в ней
        // проход
        int randomCellIndex = setCells.get(random.nextInt(setCells.size()));
        hrz[randomCellIndex] = false;
      }
    }
  }

  // вносит данные о стене в матрицу
  private void putUpWall(boolean[] str, int x, boolean value) {
    str[x] = value;
  }

  // раставляет вертикальные стены, объединяет множества при надобности
  private void buildVerticalWalls(boolean[] verticalWalls, int[] rowSets) {

    for (int i = 0; i < rowSets.length - 1; i++) {
      // соседи из одного множества?
      if (rowSets[i] == rowSets[i + 1]) {
        verticalWalls[i] = true;
      } else if (shouldPlaceRightWall(
          rowSets[i], rowSets[i + 1])) { // случайное решение: ставить ли стену?
        verticalWalls[i] = true;
      } else {
        verticalWalls[i] = false;
        mergeSet(rowSets, rowSets[i], rowSets[i + 1]);
      }
    }
  }

  // раставляет горизонтальные стены на последней строке, объединяет множества
  // при надобности
  private void finalizeLastRow(boolean[] verticalWalls, int[] rowSets) {
    for (int i = 0; i < rowSets.length - 1; i++) {
      // Если клетки из разных множеств — объединяем их и убираем стену между
      // ними
      if (rowSets[i] != rowSets[i + 1]) {
        mergeSet(rowSets, rowSets[i], rowSets[i + 1]);
        verticalWalls[i] = false;

        // если одинакова, ставим стену
      } else {
        verticalWalls[i] = true;
      }
    }
  }

  private void buildRightEdge(boolean[] vrtWalls) {
    vrtWalls[vrtWalls.length - 1] = true;
  }

  private void buildBottomEdge(boolean[] hrzMatrix) {
    Arrays.fill(hrzMatrix, true);
  }
}
