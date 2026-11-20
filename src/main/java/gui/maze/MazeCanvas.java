package gui.maze;

import domain.model.Maze;
import domain.model.Position;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.BiConsumer;
import javax.swing.*;

/** Холст для отрисовки лабиринта и маршрута решения строго 500x500 пикселей. */
public class MazeCanvas extends JPanel {
  public static final int CANVAS_SIZE = 500;
  private static final float LINE_THICKNESS = 2.0f;

  private Maze maze;
  private List<Position> solutionPath;
  private Position startPoint;
  private Position finishPoint;

  private BiConsumer<Integer, Integer> cellClickListener;

  public MazeCanvas() {
    Dimension dimension = new Dimension(CANVAS_SIZE, CANVAS_SIZE);
    setPreferredSize(dimension);
    setMinimumSize(dimension);
    setMaximumSize(dimension);
    setBackground(Color.WHITE);
    setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (maze == null || cellClickListener == null) return;

            int cols = maze.getWidth();
            int rows = maze.getHeight();
            double cellW = (double) CANVAS_SIZE / cols;
            double cellH = (double) CANVAS_SIZE / rows;

            int cellX = (int) (e.getX() / cellW);
            int cellY = (int) (e.getY() / cellH);

            if (cellX >= 0 && cellX < cols && cellY >= 0 && cellY < rows) {
              cellClickListener.accept(cellX, cellY);
            }
          }
        });
  }

  public void setCellClickListener(BiConsumer<Integer, Integer> listener) {
    this.cellClickListener = listener;
  }

  public void setMaze(Maze maze) {
    this.maze = maze;
    this.solutionPath = null;
    this.startPoint = null;
    this.finishPoint = null;
    repaint();
  }

  public void setSolution(List<Position> path, Position start, Position finish) {
    this.solutionPath = path;
    this.startPoint = start;
    this.finishPoint = finish;
    repaint();
  }

  public void clearSolution() {
    this.solutionPath = null;
    this.startPoint = null;
    this.finishPoint = null;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (maze == null) {
      drawPlaceholder(g2);
      return;
    }

    int cols = maze.getWidth();
    int rows = maze.getHeight();
    double cellW = (double) CANVAS_SIZE / cols;
    double cellH = (double) CANVAS_SIZE / rows;

    // 1. Отрисовка стен лабиринта (толщина 2px)
    g2.setColor(Color.BLACK);
    g2.setStroke(new BasicStroke(LINE_THICKNESS, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

    // Внешние верхняя и левая границы
    g2.drawLine(0, 0, CANVAS_SIZE, 0);
    g2.drawLine(0, 0, 0, CANVAS_SIZE);

    for (int y = 0; y < rows; y++) {
      for (int x = 0; x < cols; x++) {
        int x1 = (int) Math.round(x * cellW);
        int y1 = (int) Math.round(y * cellH);
        int x2 = (int) Math.round((x + 1) * cellW);
        int y2 = (int) Math.round((y + 1) * cellH);

        if (maze.hasRightWall(x, y)) {
          g2.drawLine(x2, y1, x2, y2);
        }
        if (maze.hasBottomWall(x, y)) {
          g2.drawLine(x1, y2, x2, y2);
        }
      }
    }

    // 2. Отрисовка пути решения (толщина 2px, яркий контрастный цвет)
    if (solutionPath != null && solutionPath.size() > 1) {
      g2.setColor(new Color(220, 20, 60)); // Crimson
      g2.setStroke(new BasicStroke(LINE_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

      for (int i = 0; i < solutionPath.size() - 1; i++) {
        Position p1 = solutionPath.get(i);
        Position p2 = solutionPath.get(i + 1);

        int px1 = (int) Math.round((p1.x() + 0.5) * cellW);
        int py1 = (int) Math.round((p1.y() + 0.5) * cellH);
        int px2 = (int) Math.round((p2.x() + 0.5) * cellW);
        int py2 = (int) Math.round((p2.y() + 0.5) * cellH);

        g2.drawLine(px1, py1, px2, py2);
      }
    }

    // 3. Точки старта и финиша
    drawMarker(g2, startPoint, cellW, cellH, new Color(46, 139, 87), "S"); // Зеленый старт
    drawMarker(g2, finishPoint, cellW, cellH, new Color(255, 69, 0), "F"); // Оранжевый финиш
  }

  private void drawMarker(
      Graphics2D g2, Position pos, double cellW, double cellH, Color color, String label) {
    if (pos == null) return;
    g2.setColor(color);
    int radius = (int) Math.max(3, Math.min(cellW, cellH) / 3.0);
    int cx = (int) Math.round((pos.x() + 0.5) * cellW);
    int cy = (int) Math.round((pos.y() + 0.5) * cellH);
    g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
  }

  private void drawPlaceholder(Graphics2D g2) {
    g2.setColor(Color.GRAY);
    g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
    String text = "Загрузите или сгенерируйте лабиринт";
    FontMetrics fm = g2.getFontMetrics();
    int x = (CANVAS_SIZE - fm.stringWidth(text)) / 2;
    int y = CANVAS_SIZE / 2;
    g2.drawString(text, x, y);
  }
}
