package domain.model;

import java.util.ArrayList;
import java.util.List;

public class Maze {
  public static final int MAX_SIZE = 50;
  public static final int MIN_SIZE = 1;

  Matrix vrtMatrix;
  Matrix hrzMatrix;

  public Maze(Matrix vrtMatrix, Matrix hrzMatrix) {
    validateMatrices(vrtMatrix, hrzMatrix);

    this.vrtMatrix = new Matrix(vrtMatrix);
    this.hrzMatrix = new Matrix(hrzMatrix);
  }

  public boolean hasRightWall(int x, int y) {
    if (x == getWidth() - 1) return true; // внешняя правая граница
    return vrtMatrix.getValue(x, y);
  }

  public boolean hasBottomWall(int x, int y) {
    if (y == getHeight() - 1) return true; // внешняя нижняя граница
    return hrzMatrix.getValue(x, y);
  }

  public boolean hasLeftWall(int x, int y) {
    if (x == 0) return true; // внешняя левая граница
    return hasRightWall(x - 1, y);
  }

  public boolean hasTopWall(int x, int y) {
    if (y == 0) return true; // внешняя верхняя граница
    return hasBottomWall(x, y - 1);
  }

  public int getWidth() {
    return vrtMatrix.getSizeX();
  }

  public int getHeight() {
    return vrtMatrix.getSizeY();
  }

  public Matrix getVrtMatrix() {
    return new Matrix(vrtMatrix);
  }

  public Matrix getHrzMatrix() {
    return new Matrix(hrzMatrix);
  }

  public List<Position> getPassableNeighbors(Position pos) {
    List<Position> neighbors = new ArrayList<>();
    int x = pos.x();
    int y = pos.y();

    if (!this.hasRightWall(x, y)) neighbors.add(new Position(x + 1, y));
    if (!this.hasBottomWall(x, y)) neighbors.add(new Position(x, y + 1));
    if (!this.hasLeftWall(x, y)) neighbors.add(new Position(x - 1, y));
    if (!this.hasTopWall(x, y)) neighbors.add(new Position(x, y - 1));

    return neighbors;
  }

  public boolean isInside(int x, int y) {
    return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
  }

  public boolean isInside(Position pos) {
    return pos != null && isInside(pos.x(), pos.y());
  }

  private void validateMatrices(Matrix vrt, Matrix hrz) {

    if (vrt == null || hrz == null) {
      throw new IllegalArgumentException("Матрицы лабиринта не могут быть null");
    }
    int width = vrt.getSizeX();
    int height = vrt.getSizeY();

    if (width != hrz.getSizeX() || height != hrz.getSizeY()) {
      throw new IllegalArgumentException(
          String.format(
              "Размеры матриц не совпадают: vrt (%dx%d) != hrz (%dx%d)",
              width, height, hrz.getSizeX(), hrz.getSizeY()));
    }

    if (width < MIN_SIZE || width > MAX_SIZE || height < MIN_SIZE || height > MAX_SIZE) {
      throw new IllegalArgumentException(
          String.format(
              "Размер лабиринта (%dx%d) выходит за допустимые пределы [%d, %d]",
              width, height, MIN_SIZE, MAX_SIZE));
    }
  }

  // test
  public String toAsciiString() {
    StringBuilder sb = new StringBuilder();
    int w = getWidth();
    int h = getHeight();
    sb.append("+").append("---+".repeat(w)).append("\n");
    for (int y = 0; y < h; y++) {
      sb.append("|");
      for (int x = 0; x < w; x++) {
        sb.append("   ");
        sb.append(vrtMatrix.getValue(x, y) ? "|" : " ");
      }
      sb.append("\n+");
      for (int x = 0; x < w; x++) {
        sb.append(hrzMatrix.getValue(x, y) ? "---" : "   ").append("+");
      }
      sb.append("\n");
    }
    return sb.toString();
  }
}
