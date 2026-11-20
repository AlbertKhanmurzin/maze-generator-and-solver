package domain.model;

import lombok.Getter;

@Getter
public class Cave {
  public static final int MAX_SIZE = 50;
  public static final int MIN_SIZE = 1;
  public static final int MAX_LIMIT = 7;
  public static final int MIN_LIMIT = 0;

  int deathLimit;
  int lifeLimit;
  double lifeProbability;
  Matrix matrix;

  public Cave(Matrix other, int deathLimit, int lifeLimit, double lifeProbability) {
    validInput(other, deathLimit, lifeLimit, lifeProbability);

    this.matrix = new Matrix(other);
    this.deathLimit = deathLimit;
    this.lifeLimit = lifeLimit;
    this.lifeProbability = lifeProbability;
  }

  public Cave(Matrix other, int deathLimit, int lifeLimit) {
    this(other, deathLimit, lifeLimit, 0.0);
  }

  public void setDeathLimit(int deathLimit) {
    if (deathLimit < MIN_LIMIT || deathLimit > MAX_LIMIT) {
      throw new IllegalArgumentException("неверный ввод лимитов");
    }
    this.deathLimit = deathLimit;
  }

  public void setLifeLimit(int lifeLimit) {
    if (lifeLimit < MIN_LIMIT || lifeLimit > MAX_LIMIT) {
      throw new IllegalArgumentException("неверный ввод лимитов");
    }
    this.lifeLimit = lifeLimit;
  }

  private void validInput(Matrix other, int deathLimit, int lifeLimit, double lifeProbability) {
    if (other == null) {
      throw new IllegalArgumentException("матрица == null");
    }

    int width = other.getSizeX();
    int height = other.getSizeY();

    if (width < MIN_SIZE || width > MAX_SIZE || height < MIN_SIZE || height > MAX_SIZE) {
      throw new IllegalArgumentException(
          String.format(
              "Размер пещеры (%dx%d) выходит за допустимые пределы [%d, %d]",
              width, height, MIN_SIZE, MAX_SIZE));
    }

    if (deathLimit > MAX_LIMIT
        || lifeLimit > MAX_LIMIT
        || deathLimit < MIN_LIMIT
        || lifeLimit < MIN_LIMIT) {
      throw new IllegalArgumentException("неверный ввод лимитов");
    }

    if (lifeProbability > 1 || lifeProbability < 0) {
      throw new IllegalArgumentException("некоректный шанс рождения клетки");
    }
  }

  public Matrix getMatrix() {
    return new Matrix(this.matrix);
  }

  public int getWidth() {
    return matrix.getSizeX();
  }

  public int getHeight() {
    return matrix.getSizeY();
  }

  public String toAsciiString() {
    StringBuilder sb = new StringBuilder();
    int w = getWidth();
    int h = getHeight();

    // Верхняя рамка
    sb.append("+").append("--".repeat(w)).append("+\n");

    for (int y = 0; y < h; y++) {
      sb.append("|");
      for (int x = 0; x < w; x++) {
        sb.append(matrix.getValue(x, y) ? "# " : "  ");
      }
      sb.append("|\n");
    }

    // Нижняя рамка
    sb.append("+").append("--".repeat(w)).append("+\n");

    return sb.toString();
  }
}
