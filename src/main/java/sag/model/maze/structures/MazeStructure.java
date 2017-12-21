package sag.model.maze.structures;

import sag.model.maze.Maze;
import sag.model.maze.Point;

public interface MazeStructure extends Maze {
    public void removeWall(Point point, WallDirection direction);
    public boolean notVisited(Point point);
}
