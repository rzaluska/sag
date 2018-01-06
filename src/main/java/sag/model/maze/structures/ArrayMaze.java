package sag.model.maze.structures;


import sag.model.maze.Point;

public class ArrayMaze implements MazeStructure {
    private int directions[][];
    private int width;
    private int height;
    private Point finish;

    public ArrayMaze(int width, int height, Point finish) {
        this.directions = new int[width][height];
        this.width = width;
        this.height = height;
        this.finish = finish;
    }

    @Override
    public void removeWall(Point point, WallDirection direction) {
        this.directions[point.getX()][point.getY()] |= direction.getDirection();
    }

    @Override
    public boolean notVisited(Point point) {
        return this.directions[point.getX()][point.getY()] == 0;
    }

    @Override
    public boolean isWallAt(Point point, WallDirection direction) {
        return (this.directions[point.getX()][point.getY()] & direction.getDirection()) == 0;
    }

    @Override
    public Point getFinish() {
        return this.finish;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}
