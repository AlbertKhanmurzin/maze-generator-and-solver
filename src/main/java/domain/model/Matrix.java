package domain.model;

import java.util.Arrays;

public class Matrix {
  private final boolean[][] matrix;

  public Matrix(int sizeX, int sizeY) {
    if (sizeX <= 0 || sizeY <= 0) {
      throw new IllegalArgumentException("Размеры матрицы должны быть больше нуля");
    }
    this.matrix = new boolean[sizeY][sizeX];
  }

  public Matrix(boolean[][] rawData) {
    if (rawData == null || rawData.length == 0 || rawData[0] == null || rawData[0].length == 0) {
      throw new IllegalArgumentException("Массив не может быть null или пустым");
    }
    int width = rawData[0].length;
    this.matrix = new boolean[rawData.length][];
    for (int y = 0; y < rawData.length; y++) {
      if (rawData[y] == null || rawData[y].length != width) {
        throw new IllegalArgumentException("Все строки матрицы должны быть одинаковой длины");
      }
      this.matrix[y] = Arrays.copyOf(rawData[y], width);
    }
  }

  public Matrix(Matrix other) {
    this.matrix = new boolean[other.matrix.length][];

    for (int i = 0; i < other.matrix.length; i++) {
      this.matrix[i] = Arrays.copyOf(other.matrix[i], other.matrix[i].length);
    }
  }

  public void setValue(int x, int y, boolean value) {
    this.matrix[y][x] = value;
  }

  public boolean getValue(int x, int y) {
    return this.matrix[y][x];
  }

  public int getSizeY() {
    return this.matrix.length;
  }

  public int getSizeX() {
    return this.matrix[0].length;
  }

  public boolean[][] getMatrix() {
    boolean[][] copyMatrix = new boolean[getSizeY()][getSizeX()];

    for (int i = 0; i < this.matrix.length; i++) {
      copyMatrix[i] = Arrays.copyOf(this.matrix[i], this.matrix[i].length);
    }

    return copyMatrix;
  }

  public void fill(boolean value) {
    for (boolean[] row : matrix) {
      Arrays.fill(row, value);
    }
  }

  public boolean isInside(int x, int y) {
    return x >= 0 && x < getSizeX() && y >= 0 && y < getSizeY();
  }

  public int neighborsCount(int x, int y) {
    int count = 0;

    for (int dx = -1; dx <= 1; dx++) {
      for (int dy = -1; dy <= 1; dy++) {
        if (dx == 0 && dy == 0) {
          continue;
        }
        int nx = x + dx;
        int ny = y + dy;

        // клетки за границей пещеры считаются живыми
        if (!isInside(nx, ny)) {
          count++;
        } else if (getValue(nx, ny)) {
          count++;
        }
      }
    }
    return count;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Matrix matrix1 = (Matrix) o;
    return Arrays.deepEquals(matrix, matrix1.matrix);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(matrix);
  }
}
