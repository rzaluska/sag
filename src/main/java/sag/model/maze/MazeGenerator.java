package sag.model.maze;

public interface MazeGenerator {
    Maze generate(int width, int height, Point finish);
}
