package sag.model.maze.structures;


import sag.model.maze.Maze;

public class ArrayMaze implements MazeStructure {
    private final int finishY;
    private final int finishX;
    private int directions[][];
    private int width;
    private int height;

    public ArrayMaze(int width, int height, int finishX, int finishY) {
        this.directions = new int[width][height];
        this.width = width;
        this.height = height;
        this.finishX = finishX;
        this.finishY = finishY;
    }

    @Override
    public void removeWall(int x, int y, WallDirection direction) {
        this.directions[x][y] |= direction.getDirection();
    }

    @Override
    public boolean notVisited(int x, int y) {
        return this.directions[x][y] == 0;
    }

    @Override
    public boolean isWallAt(int x, int y, WallDirection direction) {
        return (this.directions[x][y] & direction.getDirection()) == 0;
    }

    @Override
    public int getFinishX() {
        return this.finishX;
    }

    @Override
    public int getFinishY() {
        return this.finishY;
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
