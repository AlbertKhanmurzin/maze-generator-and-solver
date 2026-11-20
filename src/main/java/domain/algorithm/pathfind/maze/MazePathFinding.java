package domain.algorithm.pathfind.maze;

import domain.model.Maze;
import domain.model.Position;
import java.util.*;

public class MazePathFinding {

  public LinkedList<Position> findPath(Maze maze, Position start, Position finish) {
    // локальная очередь путей
    List<LinkedList<Position>> queue = new LinkedList<>();
    // база посещенных клеток
    Set<Position> visited = new HashSet<>();

    // инициализация
    LinkedList<Position> firstChain = new LinkedList<>();
    firstChain.add(start);
    queue.add(firstChain);
    visited.add(start);

    // проверяем наличие вариантов хода
    while (!queue.isEmpty()) {

      // берем ПЕРВЫЙ путь и удаляем его в queue(FIFO), переносим его в
      // currentChain
      LinkedList<Position> currentChain = queue.remove(0);
      Position currentPos = currentChain.getLast();
      // проверка, нашли финиш?
      if (currentPos.equals(finish)) {
        return currentChain;
      }
      // генерируем пути до доступных соседей
      List<Position> neighbors = maze.getPassableNeighbors(currentPos);
      for (Position nextPos : neighbors) {
        // проверка проходил ли через эту точку
        if (!visited.contains(nextPos)) {
          visited.add(nextPos); // Отмечаем как посещенную

          // Создаем новый путь
          LinkedList<Position> nextChain = new LinkedList<>(currentChain);
          nextChain.add(nextPos);

          // Добавляем в конец очереди
          queue.add(nextChain);
        }
      }
    }

    return null; // путь не найден, и мы корректно завершили цикл
  }

  private List<LinkedList<Position>> oneStep(Maze maze, LinkedList<Position> movesChain) {
    // получаем всех доступных соседей у последней позиции в списке
    List<Position> passableNeighbors = maze.getPassableNeighbors(movesChain.getLast());

    // создаем новые пути до каждого возможного соседа, исключаем тех которые
    // уже в списке
    return passableNeighbors.stream()
        .filter(item -> !movesChain.contains(item))
        .map(item -> {
          LinkedList<Position> copy = new LinkedList<>(movesChain);
          copy.add(item);
          return copy;
        })
        .toList();
  }

  private LinkedList<Position> initChain(Position start) {
    return new LinkedList<>(List.of(start));
  }

  private LinkedList<Position> findFinish(List<LinkedList<Position>> movesChains, Position finish) {
    return movesChains.stream()
        .filter(item -> item.getLast().equals(finish))
        .findFirst()
        .orElse(null);
  }
}
