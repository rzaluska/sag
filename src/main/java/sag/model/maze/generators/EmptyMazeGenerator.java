package sag.model.maze.generators;

import sag.model.maze.Maze;
import sag.model.maze.MazeGenerator;
import sag.model.maze.Point;
import sag.model.maze.structures.ArrayMaze;
import sag.model.maze.structures.MazeStructure;

public class EmptyMazeGenerator implements MazeGenerator {
    @Override
    public Maze generate(int width, int height, Point finish) {
        MazeStructure mazeStructure = new ArrayMaze(width, height, finish);
        for (int i = 0; i < mazeStructure.getWidth(); i++) {
            for (int j = 0; j < mazeStructure.getHeight(); j++) {
                mazeStructure.removeWall(new Point(i, j), Maze.WallDirection.N);
                mazeStructure.removeWall(new Point(i, j), Maze.WallDirection.S);
                mazeStructure.removeWall(new Point(i, j), Maze.WallDirection.W);
                mazeStructure.removeWall(new Point(i, j), Maze.WallDirection.E);
            }
        }
        return mazeStructure;
    }
}
