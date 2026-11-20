package gui.cave;

import domain.model.Cave;
import domain.model.Matrix;
import java.awt.*;
import javax.swing.*;

/** Холст для отрисовки пещеры строго 500x500 пикселей. */
public class CaveCanvas extends JPanel {
  public static final int CANVAS_SIZE = 500;

  private static final Color WALL_COLOR = new Color(40, 40, 40);
  private static final Color SPACE_COLOR = Color.WHITE;

  private Cave cave;

  public CaveCanvas() {
    Dimension dimension = new Dimension(CANVAS_SIZE, CANVAS_SIZE);
    setPreferredSize(dimension);
    setMinimumSize(dimension);
    setMaximumSize(dimension);
    setBackground(SPACE_COLOR);
    setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
  }

  public void setCave(Cave cave) {
    this.cave = cave;
    repaint();
  }

  public Cave getCave() {
    return cave;
  }

  public void clear() {
    this.cave = null;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (cave == null) {
      drawPlaceholder(g2);
      return;
    }

    int cols = cave.getWidth();
    int rows = cave.getHeight();
    double cellW = (double) CANVAS_SIZE / cols;
    double cellH = (double) CANVAS_SIZE / rows;

    Matrix matrix = cave.getMatrix();

    for (int y = 0; y < rows; y++) {
      for (int x = 0; x < cols; x++) {
        int x1 = (int) Math.round(x * cellW);
        int y1 = (int) Math.round(y * cellH);
        int x2 = (int) Math.round((x + 1) * cellW);
        int y2 = (int) Math.round((y + 1) * cellH);

        boolean isWall = matrix.getValue(x, y);
        g2.setColor(isWall ? WALL_COLOR : SPACE_COLOR);
        g2.fillRect(x1, y1, x2 - x1, y2 - y1);
      }
    }

    // Внешняя окантовка холста
    g2.setColor(Color.LIGHT_GRAY);
    g2.drawRect(0, 0, CANVAS_SIZE - 1, CANVAS_SIZE - 1);
  }

  private void drawPlaceholder(Graphics2D g2) {
    g2.setColor(Color.GRAY);
    g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
    String text = "Загрузите или инициализируйте пещеру";
    FontMetrics fm = g2.getFontMetrics();
    int x = (CANVAS_SIZE - fm.stringWidth(text)) / 2;
    int y = CANVAS_SIZE / 2;
    g2.drawString(text, x, y);
  }
}
