package sag.model.maze.structures;

import sag.model.maze.Maze;

public interface MazeStructure extends Maze {
    public void removeWall(int x, int y, WallDirection direction);
    public boolean notVisited(int x, int y);
}
