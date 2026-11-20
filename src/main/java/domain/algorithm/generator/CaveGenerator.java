package domain.algorithm.generator;

import domain.model.Cave;
import domain.model.Matrix;
import java.util.Random;

public class CaveGenerator {

  public Cave initCave(int x, int y, int deathLimit, int lifeLimit, double lifeProbability) {
    Matrix firstGen = initCave(new Matrix(x, y), lifeProbability);
    return new Cave(firstGen, deathLimit, lifeLimit, lifeProbability);
  }

  public Cave oneGeneration(Cave currentGen) {
    Matrix nextGenM =
        oneGeneration(
            currentGen.getMatrix(), currentGen.getDeathLimit(), currentGen.getLifeLimit());
    return new Cave(
        nextGenM,
        currentGen.getDeathLimit(),
        currentGen.getLifeLimit(),
        currentGen.getLifeProbability());
  }

  public Matrix oneGeneration(Matrix currentGen, int deathLimit, int lifeLimit) {
    int[][] neighbourCount = getNeighborCountM(currentGen);
    return getNextGenMatrix(deathLimit, lifeLimit, currentGen, neighbourCount);
  }

  private int[][] getNeighborCountM(Matrix gen) {
    int x = gen.getSizeX();
    int y = gen.getSizeY();

    int[][] neighborCount = new int[y][x];

    for (int i = 0; i < y; i++) {
      for (int j = 0; j < x; j++) {
        neighborCount[i][j] = gen.neighborsCount(j, i);
      }
    }
    return neighborCount;
  }

  private Matrix getNextGenMatrix(
      int deathLimit, int lifeLimit, Matrix currenGen, int[][] neighborCount) {
    int x = neighborCount[0].length;
    int y = neighborCount.length;

    Matrix nextGen = new Matrix(x, y);

    for (int i = 0; i < y; i++) {
      for (int j = 0; j < x; j++) {
        int count = neighborCount[i][j];
        if (currenGen.getValue(j, i)) {
          if (count >= deathLimit) nextGen.setValue(j, i, true);
        } else {
          if (count > lifeLimit) nextGen.setValue(j, i, true);
        }
      }
    }
    return nextGen;
  }

  private Matrix initCave(Matrix matrix, double probability) {
    Random rand = new Random();
    for (int i = 0; i < matrix.getSizeY(); i++) {
      for (int j = 0; j < matrix.getSizeX(); j++) {
        boolean isAlive = lifeProbability(rand, probability);
        matrix.setValue(j, i, isAlive);
      }
    }
    return matrix;
  }

  private boolean lifeProbability(Random rand, double probability) {
    return (rand.nextDouble() < probability);
  }
}
