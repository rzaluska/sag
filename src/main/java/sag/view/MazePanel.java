package sag.view;

import sag.model.maze.Maze;
import sag.model.maze.Point;

import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {
    private final int cellSize;
    private Maze maze;

    MazePanel(Maze maze, int cellSize) {
        this.maze = maze;
        this.cellSize = cellSize;
    }

    public Dimension getPreferredSize() {
        return new Dimension(this.cellSize * this.maze.getWidth(), this.cellSize * this.maze.getHeight());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Text
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.maze.getWidth() * this.cellSize, this.maze.getHeight() * this.cellSize);
        g.setColor(Color.BLACK);
        for (int i = 0; i < this.maze.getHeight(); i++) {
            for (int j = 0; j < this.maze.getWidth(); j++) {
                int cellx = j * cellSize;
                int celly = i * cellSize;
                if (maze.isWallAt(new Point(j, i), Maze.WallDirection.S)) {
                    g.fillRect(cellx, celly + cellSize, cellSize, 1);
                }
                if (maze.isWallAt(new Point(j, i), Maze.WallDirection.E)) {
                    g.fillRect(cellx + cellSize, celly, 1, cellSize);
                }

            }
        }
        g.setColor(Color.BLUE);
        g.fillRect(this.maze.getFinish().getX() * cellSize + 2, this.maze.getFinish().getY() * cellSize + 2, cellSize - 4, cellSize - 4);
    }
}
