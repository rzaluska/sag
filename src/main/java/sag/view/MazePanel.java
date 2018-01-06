package sag.view;

import sag.model.maze.Maze;
import sag.model.maze.Point;
import sag.model.simulation.Simulation;

import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {
    private final int cellSize;
    Simulation simulation;

    MazePanel(Simulation simulation, int cellSize) {
        this.simulation = simulation;
        this.cellSize = cellSize;
    }

    public Dimension getPreferredSize() {
        Maze maze = this.simulation.getMaze();
        return new Dimension(this.cellSize * maze.getWidth(), this.cellSize * maze.getHeight());
    }

    public void paintComponent(Graphics g) {
        Maze maze = this.simulation.getMaze();
        super.paintComponent(g);

        // Draw Text
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, maze.getWidth() * this.cellSize, maze.getHeight() * this.cellSize);
        g.setColor(Color.BLACK);
        for (int i = 0; i < maze.getHeight(); i++) {
            for (int j = 0; j < maze.getWidth(); j++) {
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
        g.setColor(Color.CYAN);
        for (Boolean[][] path : this.simulation.getAgentsPaths()) {
            for (int i = 0; i < this.simulation.getMaze().getWidth(); i++) {
                for (int j = 0; j < this.simulation.getMaze().getHeight(); j++) {
                    if (path[i][j]) {
                        int cellx = i * cellSize;
                        int celly = j * cellSize;
                        g.fillRect(cellx + 2, celly + 2, cellSize - 2, cellSize - 2);
                    }
                }
            }
        }
        g.setColor(Color.RED);
        for (Point point : this.simulation.getAgentsPositions()) {
            int cellx = point.getX() * cellSize;
            int celly = point.getY() * cellSize;
            g.fillRect(cellx + 2, celly + 2, cellSize - 2, cellSize - 2);
        }


        g.setColor(Color.BLUE);
        g.fillRect(maze.getFinish().getX() * cellSize + 2, maze.getFinish().getY() * cellSize + 2, cellSize - 4, cellSize - 4);
    }
}
